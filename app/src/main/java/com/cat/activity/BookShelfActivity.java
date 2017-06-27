package com.cat.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baoyz.swipemenulistview.BaseSwipListAdapter;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.cat.R;
import com.cat.entity.BookJson;
import com.cat.entity.BookShelfItem;
import com.cat.layout.VerticalSwipeRefreshLayout;
import com.squareup.picasso.Picasso;
import com.ta.TASyncHttpClient;
import com.ta.annotation.TAInject;
import com.ta.util.http.AsyncHttpClient;
import com.ta.util.http.JsonHttpResponseHandler;
import com.ta.util.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.OnClick;
import dmax.dialog.SpotsDialog;
import io.github.xudaojie.qrcodelib.CaptureActivity;

import static com.cat.activity.MainActivity.stringToList;


public class BookShelfActivity extends AppCompatActivity implements View.OnClickListener{


    //声明自有变量
    private final List<BookShelfItem> list = new ArrayList<>();
    private String userId;

    //定位变量声明
    public LocationClient mLocationClient;
    public BDLocationListener myListener = new MyLocationListenner();
    private String latitude;
    private String longtitude;
    private String city;
    private String province;

    //控件声明
    private Toolbar title;
    private android.app.AlertDialog dialog;
    private SwipeMenuListView listView;
    private VerticalSwipeRefreshLayout refreshLayout;
    private BookListAdapter bookListAdapter;


    //网络请求相关
    @TAInject
    private TASyncHttpClient syncHttpClient;
    @TAInject
    private AsyncHttpClient asyncHttpClient;
    final String BASEURL = "http://115.159.35.11:8080/bookstore/restful/";


    //扫码变量声明
    private int REQUEST_QR_CODE = 0;

    //共享变量
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置android 4.4以上即api19以上的状态栏为半透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow ().getAttributes ();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        setContentView(R.layout.content_book_shelf);
        initview();
    }

    private void loadData() {
        //伪数据

        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
        list.clear();
        userId = sharedPreferences.getString("userid", null);

        RequestParams rp = new RequestParams();
        rp.put("userid", userId);
        asyncHttpClient.post(BASEURL + "bookShelf/getBooksByUserId",rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                super.onSuccess(response);
                try {
                    String retcode = response.getString("retcode");
                    if (retcode != null && !retcode.equals("0000")) {
                        String errorMsg = response.getString("errorMsg");
                        Toast.makeText(BookShelfActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        if(retcode.equals("0003")){
                            ShelfCreate();
                        }

                    } else {
                        List<BookJson> book = stringToList(response.getJSONArray("obj").toString(),BookJson.class);
                        for(BookJson bj:book){
                            BookShelfItem item = new BookShelfItem();
                            item.setAuthor(bj.getAuthor());
                            item.setPublisher(bj.getPublisher());
                            item.setISBN(bj.getIsbn());
                            item.setImageURL(bj.getBookImageURI());
                            item.setInnerTime(bj.getStoreTime());
                            item.setBookId(bj.getBookId());
                            item.setBookName(bj.getTitle());
                            list.add(item);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable error) {
                super.onFailure(error);
                dialog.dismiss();
                Toast.makeText(BookShelfActivity.this, "网络出错，请检查网络！", Toast.LENGTH_SHORT).show();
            }
        });
        listView.setAdapter(bookListAdapter);
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void ShelfCreate() {

        // 定位初始化
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
//        option.setScanSpan(0);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
        mLocationClient.requestLocation();
        final EditText inputString = new EditText(this);
        inputString.setHint("描述一下你的书架吧~");


        new AlertDialog.Builder(this).setTitle("友情提示").setMessage("你还没有书架，是否在当前位置创建一个书架？").setView(inputString)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RequestParams rp = new RequestParams();
                        rp.put("userid",userId);
                        rp.put("latitude",latitude);
                        rp.put("longtitude",longtitude);
                        rp.put("province",province);
                        rp.put("city",city);
                        rp.put("storeDescribe",inputString.getText().toString());
                        String s  = BASEURL + "bookShelf/createShelf";
                        asyncHttpClient = new AsyncHttpClient();
                        asyncHttpClient.post(s,rp,new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                try {
                                    String errorMsg = response.getString("errorMsg");
                                    Toast.makeText(BookShelfActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                                }catch (Exception E){
                                    E.printStackTrace();
                                }
                            }
                            @Override
                            public void onFailure(Throwable error) {
                                super.onFailure(error);
                                Toast.makeText(BookShelfActivity.this, "网络出错，请检查网络！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
    }

    private void deleteMethod(final int position, final Integer bookId) {
        new AlertDialog.Builder(this).setTitle("友情提示").setMessage("确定要下架吗,这样做会扣除一点积分")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 通过程序我们知道删除了，但是怎么刷新ListView呢？
                        // 只需要重新设置一下adapter
                        asyncHttpClient = new AsyncHttpClient();
                        RequestParams rp = new RequestParams();
                        rp.put("bookid", String.valueOf(bookId));
                        asyncHttpClient.post(BASEURL + "bookShelf/deleteBooksByBookId",rp, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                super.onSuccess(response);
                                try {
                                    String errorMsg = response.getString("errorMsg");
                                    Toast.makeText(BookShelfActivity.this,errorMsg, Toast.LENGTH_SHORT).show();
                                    String retcode = response.getString("retcode");
                                    if(retcode.equals("0000")){
                                        list.remove(position);
                                        listView.setAdapter(bookListAdapter);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Throwable error) {
                                super.onFailure(error);
                                Toast.makeText(BookShelfActivity.this, "网络出错，请检查网络！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

    }

    private void initview() {
        //绑定控件
        title = (Toolbar) findViewById(R.id.template_toolbar);

        dialog = new SpotsDialog(BookShelfActivity.this);

        //初始化ToolBar
        title.setTitle(getIntent().getStringExtra("title"));
        setSupportActionBar(title);
        title.setNavigationIcon(R.drawable.return_btn);//设置返回icon
        title.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        asyncHttpClient = new AsyncHttpClient();
        listView = (SwipeMenuListView) findViewById(R.id.bookList);
        refreshLayout = (VerticalSwipeRefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setVerticalScrollBarEnabled(true);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       loadData();
                        refreshLayout.setRefreshing(false);
                    }
                },2500);
            }
        });


        bookListAdapter = new BookListAdapter(BookShelfActivity.this,list);
        listView.setAdapter(bookListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                asyncHttpClient = new AsyncHttpClient();

                RequestParams rp = new RequestParams();
                rp.put("bookid", String.valueOf(id));
                asyncHttpClient.post(BASEURL + "bookShelf/getBooksByBookId",rp, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        super.onSuccess(response);
                        try {
                            dialog.dismiss();
                            String retcode = response.getString("retcode");
                            if (retcode != null && !retcode.equals("0000")) {
                                String errorMsg = response.getString("errorMsg");
                                String obj = response.getString("obj");
                                Toast.makeText(BookShelfActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                            } else {
                                String obj = response.getString("obj");
                                Intent intent = new Intent(BookShelfActivity.this, ShelfBookActivity.class);
                                intent.putExtra("serverResponse",obj);
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            dialog.dismiss();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        super.onFailure(error);
                        dialog.dismiss();
                        Toast.makeText(BookShelfActivity.this, "网络出错，请检查网络！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        sharedPreferences = getApplicationContext().getSharedPreferences("user",Context.MODE_PRIVATE);



        //Create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        listView.setMenuCreator(creator);

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                BookShelfItem item = list.get(position);
                switch (index) {
                    case 0:
                        deleteMethod(position,item.getBookId());
                        break;
                }
                return false;
            }
        });


        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
//        option.setScanSpan(0);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
        mLocationClient.requestLocation();
    }



    static class ViewHolder{
        public ImageView bookImage;
        public TextView author;
        public TextView bookName;
        public TextView publisher;
        public TextView inTime;
        public TextView ISBN;
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.addBook:
                Intent i = new Intent(BookShelfActivity.this, CaptureActivity.class);
                startActivityForResult(i, REQUEST_QR_CODE);
//                fakeAction();
                break;
        }
    }

    public class BookListAdapter extends BaseSwipListAdapter {
        private LayoutInflater mInflater = null;
        private List<BookShelfItem> itemList;

        public BookListAdapter(Context context, List<BookShelfItem> itemList){
            this.mInflater = LayoutInflater.from(context);
            this.itemList = itemList;
        }

        @Override
        public int getCount() {
            return itemList.size();
        }

        @Override
        public Object getItem(int position) {
            return itemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return itemList.get(position).getBookId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = new ViewHolder();
            if(convertView ==null){
                convertView = mInflater.inflate(R.layout.bookshelf_item,null);
                viewHolder.bookImage = (ImageView) convertView.findViewById(R.id.bookImage);
                viewHolder.author = (TextView)convertView.findViewById(R.id.author);
                viewHolder.bookName = (TextView)convertView.findViewById(R.id.bookName);
                viewHolder.publisher = (TextView)convertView.findViewById(R.id.publisher);
                viewHolder.ISBN = (TextView)convertView.findViewById(R.id.ISBN);
                viewHolder.inTime = (TextView)convertView.findViewById(R.id.inTime);
                convertView.setTag(viewHolder);
            }else{
                convertView = convertView;
                viewHolder= (ViewHolder)convertView.getTag();
            }
            String path = list.get(position).getImageURL();
            Picasso.with(mInflater.getContext()).load(path).placeholder(R.drawable.default_image).into(viewHolder.bookImage);
//            viewHolder.bookImage.setImageBitmap(bitmap);
            viewHolder.author.setText(itemList.get(position).getAuthor());
            viewHolder.publisher.setText(itemList.get(position).getPublisher());
            viewHolder.ISBN.setText(itemList.get(position).getISBN());
            viewHolder.inTime.setText(itemList.get(position).getInnerTime());
            viewHolder.bookName.setText(itemList.get(position).getBookName());
            return convertView;
        }

        @Override
        public boolean getSwipEnableByPosition(int position) {
            return true;
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK
                && requestCode == REQUEST_QR_CODE
                && data != null) {
            final String result = data.getStringExtra("result");
            dialog.show();

            RequestParams rp = new RequestParams();
            rp.put("isbn",result);
            String s = "https://api.douban.com/v2/book/isbn/"+result;
            asyncHttpClient.get(s,new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    super.onSuccess(response);
                    String retcode = response.optString("code");
                    if (retcode!=null&&retcode.equals("6000")) {
                        Toast.makeText(BookShelfActivity.this, "未查到相关书籍信息，请联系系统管理员！", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        final String doubanResponse = response.toString();
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "获取成功！", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(BookShelfActivity.this, AddBookActivity.class);
                        intent.putExtra("doubanResponse",doubanResponse);
                        intent.putExtra("userid",userId);
                        startActivity(intent);
                    }
                }
                @Override
                public void onFailure(Throwable error) {
                    super.onFailure(error);
                    dialog.dismiss();
                    Toast.makeText(BookShelfActivity.this, "网络出错，请检查网络！", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.update) {
            // 定位初始化
            mLocationClient = new LocationClient(this);
            mLocationClient.registerLocationListener(myListener);
            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(true); // 打开gps
            option.setCoorType("bd09ll"); // 设置坐标类型
//        option.setScanSpan(0);
            mLocationClient.setLocOption(option);
            mLocationClient.start();
            mLocationClient.requestLocation();


            RequestParams rp = new RequestParams();
            rp.put("userid",userId);
            rp.put("latitude",latitude);
            rp.put("longtitude",longtitude);
            String s  = BASEURL + "bookShelf/updateLocation";
            asyncHttpClient = new AsyncHttpClient();
            asyncHttpClient.post(s,rp,new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        String errorMsg = response.getString("errorMsg");
                        Toast.makeText(BookShelfActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }catch (Exception E){
                        E.printStackTrace();
                    }
                }
                @Override
                public void onFailure(Throwable error) {
                    super.onFailure(error);
                    Toast.makeText(BookShelfActivity.this, "网络出错，请再尝试一次！", Toast.LENGTH_SHORT).show();
                }
            });

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    protected void onDestroy() {// 退出时销毁定位
        mLocationClient.stop();
        // 关闭定位图层
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            latitude = String.valueOf(location.getLatitude());
            longtitude = String.valueOf(location.getLongitude());
            city = location.getCity();
            province = location.getProvince();
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

}
