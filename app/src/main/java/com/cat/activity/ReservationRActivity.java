
package com.cat.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.cat.entity.BookShelfItem;
import com.cat.entity.ReservationJson;
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
import io.github.xudaojie.qrcodelib.CaptureActivity;

import static com.cat.activity.MainActivity.stringToList;


public class ReservationRActivity extends AppCompatActivity{


    //声明自有变量
    private final List<ReservationJson> list = new ArrayList<>();
    private String userId;

    //控件声明
    private Toolbar title;
    private android.app.AlertDialog dialog;
    private SwipeMenuListView listView;
    private VerticalSwipeRefreshLayout refreshLayout;
    private ReservationAdapter reservationAdapter;


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
        setContentView(R.layout.activity_reservation_r);
        initview();
        loadData();
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
        asyncHttpClient.post(BASEURL + "bookShelf/reservationInfoForReceiver",rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                super.onSuccess(response);
                try {
                    String retcode = response.getString("retcode");
                    if (retcode != null && !retcode.equals("0000")) {
                        String errorMsg = response.getString("errorMsg");
                        Toast.makeText(ReservationRActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                    } else {
                        List<ReservationJson> reserves = stringToList(response.getJSONArray("obj").toString(),ReservationJson.class);
                        for(ReservationJson rj:reserves){
                            ReservationJson item = new ReservationJson();
                            item.setBookName(rj.getBookName());
                            item.setPhone(rj.getPhone());
                            item.setUserName(rj.getUserName());
                            item.setBookId(rj.getBookId());
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
                Toast.makeText(ReservationRActivity.this, "网络出错，请检查网络！", Toast.LENGTH_SHORT).show();
            }
        });
        listView.setAdapter(reservationAdapter);
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void deleteMethod(final int position, final Integer bookId) {
        new AlertDialog.Builder(this).setTitle("友情提示").setMessage("取消预约吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 通过程序我们知道删除了，但是怎么刷新ListView呢？
                        // 只需要重新设置一下adapter
                        asyncHttpClient = new AsyncHttpClient();
                        RequestParams rp = new RequestParams();
                        rp.put("bookid", String.valueOf(bookId));
                        asyncHttpClient.post(BASEURL + "bookShelf/deleteReservationByBookId",rp, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                super.onSuccess(response);
                                try {
                                    String errorMsg = response.getString("errorMsg");
                                    Toast.makeText(ReservationRActivity.this,errorMsg, Toast.LENGTH_SHORT).show();
                                    String retcode = response.getString("retcode");
                                    if(retcode.equals("0000")){
                                        list.remove(position);
                                        listView.setAdapter(reservationAdapter);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Throwable error) {
                                super.onFailure(error);
                                Toast.makeText(ReservationRActivity.this, "网络出错，请检查网络！", Toast.LENGTH_SHORT).show();
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
        title = (Toolbar) findViewById(R.id.template_toolbarForRes);

        dialog = new SpotsDialog(ReservationRActivity.this);

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
        listView = (SwipeMenuListView) findViewById(R.id.reserve_list);
        refreshLayout = (VerticalSwipeRefreshLayout) findViewById(R.id.refreshLayoutForRes);
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


        reservationAdapter = new ReservationAdapter(ReservationRActivity.this,list);
        listView.setAdapter(reservationAdapter);
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);



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
                ReservationJson item = list.get(position);
                switch (index) {
                    case 0:
                        deleteMethod(position,item.getBookId());
                        break;
                }
                return true;
            }
        });
        //什麽都沒做
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

    }

    static class ViewHolder{
        public TextView reserve_title;
        public TextView reserve_user;
        public TextView reserve_phone;
    }


    public class ReservationAdapter extends BaseSwipListAdapter {
        private LayoutInflater mInflater = null;
        private List<ReservationJson> itemList;

        public ReservationAdapter(Context context, List<ReservationJson> itemList){
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
                convertView = mInflater.inflate(R.layout.reservation_item,null);
                viewHolder.reserve_phone = (TextView) convertView.findViewById(R.id.reserve_phone);
                viewHolder.reserve_title = (TextView) convertView.findViewById(R.id.reserve_title);
                viewHolder.reserve_user = (TextView) convertView.findViewById(R.id.reserve_user);
                convertView.setTag(viewHolder);
            }else{
                convertView = convertView;
                viewHolder= (ViewHolder)convertView.getTag();
            }
            viewHolder.reserve_phone.setText(itemList.get(position).getPhone());
            viewHolder.reserve_title.setText(itemList.get(position).getBookName());
            viewHolder.reserve_user.setText(itemList.get(position).getUserName());
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
    }
}
