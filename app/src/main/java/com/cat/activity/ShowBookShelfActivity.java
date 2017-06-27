package com.cat.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.baoyz.swipemenulistview.BaseSwipListAdapter;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.cat.R;
import com.cat.entity.BookJson;
import com.cat.entity.BookShelfItem;
import com.cat.entity.ShelfInfoJson;
import com.cat.layout.VerticalSwipeRefreshLayout;
import com.squareup.picasso.Picasso;
import com.ta.TASyncHttpClient;
import com.ta.annotation.TAInject;
import com.ta.util.http.AsyncHttpClient;
import com.ta.util.http.JsonHttpResponseHandler;
import com.ta.util.http.RequestParams;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import dmax.dialog.SpotsDialog;

import static com.cat.activity.MainActivity.stringToList;


public class ShowBookShelfActivity extends AppCompatActivity{


    //声明自有变量
    private final List<BookShelfItem> list = new ArrayList<>();
    private String userId;
    private ImageView personal_icon;
    private String photoURL;
    private String shelfId;
    private String describe;
    private String phone;
    private String shelfName;

    //控件声明
    private Toolbar title;
    private android.app.AlertDialog dialog;
    private SwipeMenuListView listView;
    private VerticalSwipeRefreshLayout refreshLayout;
    private BookListAdapter bookListAdapter;
    private TextView phoneText;
    private TextView shelfNameText;
    private TextView describeText;


    //网络请求相关
    @TAInject
    private TASyncHttpClient syncHttpClient;
    @TAInject
    private AsyncHttpClient asyncHttpClient;
    final String BASEURL = "http://115.159.35.11:8080/bookstore/restful/";

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
        setContentView(R.layout.activity_show_book_shelf);
        initview();
    }

    private void loadData() {
        Intent intent  = getIntent();
        shelfId = intent.getStringExtra("storeid");

        list.clear();
        userId = sharedPreferences.getString("userid", null);


        RequestParams rp = new RequestParams();
        rp.put("shelfid", shelfId);
        asyncHttpClient.post(BASEURL + "bookShelf/getBooksByShelfId",rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                super.onSuccess(response);
                try {
                    String retcode = response.getString("retcode");
                    if (retcode != null && !retcode.equals("0000")) {
                        String errorMsg = response.getString("errorMsg");
                        Toast.makeText(ShowBookShelfActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        if(retcode.equals("0003")){
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
                Toast.makeText(ShowBookShelfActivity.this, "网络出错，请检查网络！", Toast.LENGTH_SHORT).show();
            }
        });
        asyncHttpClient.post(BASEURL + "bookShelf/getInfosByShelfId",rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                super.onSuccess(response);
                try {
                    String retcode = response.getString("retcode");
                    if (retcode != null && !retcode.equals("0000")) {
                        String errorMsg = response.getString("errorMsg");
                        Toast.makeText(ShowBookShelfActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        if(retcode.equals("0003")){
                        }

                    } else {
                        List<ShelfInfoJson> infos = stringToList(response.getJSONArray("obj").toString(),ShelfInfoJson.class);
                        for(ShelfInfoJson bj:infos){
                            photoURL = bj.getImageURI();
                            describe =bj.getShelfDescribe();
                            phone = bj.getPhone();
                            shelfName = bj.getShelfName();
                            Picasso.with(ShowBookShelfActivity.this).load(photoURL).placeholder(R.drawable.default_image).into(personal_icon);
                            phoneText.setText(phone);
                            shelfNameText.setText(shelfName);
                            describeText.setText(describe);
                            listView.setAdapter(bookListAdapter);
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
                Toast.makeText(ShowBookShelfActivity.this, "网络出错，请检查网络！", Toast.LENGTH_SHORT).show();
            }
        });
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void initview() {
        //绑定控件
        title = (Toolbar) findViewById(R.id.template_toolbar);

        dialog = new SpotsDialog(ShowBookShelfActivity.this);

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

        sharedPreferences = getApplicationContext().getSharedPreferences("user",Context.MODE_PRIVATE);
        personal_icon = (ImageView) findViewById(R.id.person_image1);
        phoneText =(TextView)findViewById(R.id.phoneNumber) ;
        shelfNameText = (TextView)findViewById(R.id.shelfname);
        describeText = (TextView)findViewById(R.id.storedescribe);

        phoneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowListDialog();
            }
        });


        bookListAdapter = new BookListAdapter(ShowBookShelfActivity.this,list);
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
                            String retcode = response.getString("retcode");
                            if (retcode != null && !retcode.equals("0000")) {
                                String errorMsg = response.getString("errorMsg");
                                String obj = response.getString("obj");
                                Toast.makeText(ShowBookShelfActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else {
                                String obj = response.getString("obj");
                                Intent intent = new Intent(ShowBookShelfActivity.this, ShelfBookActivity.class);
                                intent.putExtra("serverResponse",obj);
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        super.onFailure(error);
                        dialog.dismiss();
                        Toast.makeText(ShowBookShelfActivity.this, "网络出错，请检查网络！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



        //Create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {


                // create "delete" item
                SwipeMenuItem bookItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                bookItem.setBackground(new ColorDrawable(Color.rgb(0,
                        121, 214)));
                // set item width
                bookItem.setWidth(dp2px(90));
                // set a icon
                bookItem.setIcon(R.drawable.ic_book);
                // add to menu
                menu.addMenuItem(bookItem);
            }
        };

        listView.setMenuCreator(creator);

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                BookShelfItem item = list.get(position);
                switch (index) {
                    case 0:
                        // delete
                        bookMethod(position,item.getBookId());
                        break;
                }
                return false;
            }
        });
    }

    private void ShowListDialog() {
        //TODO:CALL
        final String[] items = {"呼叫","短信","复制","取消"};
        AlertDialog.Builder listDialog = new AlertDialog.Builder(ShowBookShelfActivity.this);
        listDialog.setTitle("这是一个电话号码,要做什么");
        listDialog.setItems(items,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri uri;
                Intent intent;
                switch (which){
                    case 0:
                        uri = Uri.parse("tel:" + phone);
                        intent = new Intent(Intent.ACTION_DIAL,uri);
                        startActivity(intent);
                        break;
                    case 1:
                        uri = Uri.parse("smsto:" + phone);
                        intent = new Intent(Intent.ACTION_SENDTO,uri);
                        startActivity(intent);
                        break;
                    case 2:
                        ClipboardManager cm = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                        cm.setText(phone);
                        Toast.makeText(ShowBookShelfActivity.this, "复制成功", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        break;

                }
            }
        });
        listDialog.show();

    }

    private void bookMethod(int position,final Integer bookId) {
        //TODO：预约
        new AlertDialog.Builder(this).setTitle("友情提示").setMessage("确定预约吗？为了保证有效预约，建议预约后电话联系")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 通过程序我们知道删除了，但是怎么刷新ListView呢？
                        // 只需要重新设置一下adapter
                        asyncHttpClient = new AsyncHttpClient();
                        RequestParams rp = new RequestParams();
                        rp.put("userid",userId);
                        rp.put("bookid", String.valueOf(bookId));
                        asyncHttpClient.post(BASEURL + "bookShelf/booking",rp, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                super.onSuccess(response);
                                try {
                                    String errorMsg = response.getString("errorMsg");
                                    Toast.makeText(ShowBookShelfActivity.this,errorMsg, Toast.LENGTH_SHORT).show();
                                    String retcode = response.getString("retcode");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Throwable error) {
                                super.onFailure(error);
                                Toast.makeText(ShowBookShelfActivity.this, "网络出错，请检查网络！", Toast.LENGTH_SHORT).show();
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

    static class ViewHolder{
        public ImageView bookImage;
        public TextView author;
        public TextView bookName;
        public TextView publisher;
        public TextView inTime;
        public TextView ISBN;
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
    protected void onResume() {
        super.onResume();
        loadData();
    }
}
