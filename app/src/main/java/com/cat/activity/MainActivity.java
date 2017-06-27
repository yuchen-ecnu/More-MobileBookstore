package com.cat.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.cat.MyApplication;
import com.cat.R;
import com.cat.entity.Bookstore;
import com.cat.entity.User;
import com.cat.util.stringcache.GeoHash;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;
import com.ta.TASyncHttpClient;
import com.ta.annotation.TAInject;
import com.ta.util.http.AsyncHttpClient;
import com.ta.util.http.JsonHttpResponseHandler;
import com.ta.util.http.RequestParams;
import com.umeng.message.common.UmLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import io.github.xudaojie.qrcodelib.CaptureActivity;

import static com.baidu.mapapi.BMapManager.getContext;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,BaiduMap.OnMarkerClickListener {

    //变量声明
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private FloatingActionButton btn_useBike;
    private FloatingActionButton btn_getLocation;
    private FloatingActionButton btn_postMsg;
    private ArrayList <LatLng> myBike;
    private int mBackKeyPressedTimes;
    private FloatingSearchView mSearchView;
    private ImageView headpic;
    private TextView username;
    private TextView phone;
    private static MainActivity mainActivity;
    SharedPreferences preferences;
    private boolean isopen=false;

    //定位变量声明
    public LocationClient mLocationClient;
    public BDLocationListener myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    BitmapDescriptor mCurrentMarker;
    LocationManager locationManager;
    private String currentPositionGEO;

    //扫码变量声明
    private int REQUEST_QR_CODE = 0;

    //控件声明
    private AlertDialog dialog;

    //网络请求相关
    @TAInject
    private TASyncHttpClient syncHttpClient;
    @TAInject
    private AsyncHttpClient asyncHttpClient;
    final String BASEURL = "http://115.159.35.11:8080/bookstore/restful/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        checkDeviceToken();
        initGps(false);
    }

    private void checkDeviceToken() {
        preferences.edit().putBoolean("isLogin",true).commit();
        if(!preferences.getBoolean("isSend",false)&&preferences.getBoolean("isGetToken",false)) {
            //提交DeviceToken
            preferences.edit().putBoolean("isSend",true).commit();
            RequestParams rp = new RequestParams();
            rp.put("devicetoken", preferences.getString("devicetoken",""));
            rp.put("userid", preferences.getString("userid",""));
            asyncHttpClient.post(BASEURL + "user/registerDevice", rp, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    super.onSuccess(response);
                    try {
                        String retcode = response.getString("retcode");
                        String errMsg = response.getString("errorMsg");
                        String obj = response.getString("obj");
                        if (!retcode.equals("0000")) {
                            Toast.makeText(MainActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Throwable error) {
                    super.onFailure(error);
                    Toast.makeText(MainActivity.this, "device Token上传失败！", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (mBackKeyPressedTimes == 0) {
            Toast.makeText(this, "再按一次退出程序 ", Toast.LENGTH_SHORT).show();
            mBackKeyPressedTimes = 1;
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        mBackKeyPressedTimes = 0;
                    }
                }
            }.start();
            return;
        }
        else{
                finish();
            }
        super.onBackPressed();
    }

    private void initView() {
        asyncHttpClient = new AsyncHttpClient();
        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        dialog = new SpotsDialog(MainActivity.this);
        btn_useBike = (FloatingActionButton) findViewById(R.id.useBike);
        btn_getLocation = (FloatingActionButton) findViewById(R.id.getMyLocation);
        btn_postMsg = (FloatingActionButton) findViewById(R.id.ReportMsg);
        btn_useBike.setOnClickListener(this);
        btn_getLocation.setOnClickListener(this);
        btn_postMsg.setOnClickListener(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        NavigationView view = (NavigationView)findViewById(R.id.navigation_view);
        View headerLayout = view.inflateHeaderView(R.layout.header);
        headpic = (ImageView) headerLayout.findViewById(R.id.profile_image);
        username = (TextView) headerLayout.findViewById(R.id.username);
        phone = (TextView) headerLayout.findViewById(R.id.email);
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        mainActivity = this;
        setSupportActionBar(toolbar);
        initSearch();

        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                if(menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()){
                    case R.id.inbox:
                        //个人资料
                        Intent intent = new Intent(MainActivity.this,PersonDataActivity.class);
                        intent.putExtra("title","个人资料");
                        startActivity(intent);
                        return true;
                    case R.id.starred:
                        //借阅记录
                        Intent intent2 = new Intent(MainActivity.this,BookLendHistoryActivity.class);
                        intent2.putExtra("title","借阅记录");
                        startActivity(intent2);
                        return true;
                    case R.id.sent_mail:
                        //我的书架
                        Intent intent3 = new Intent(MainActivity.this,BookShelfActivity.class);
                        intent3.putExtra("title","我的书架");
                        startActivity(intent3);
                        return true;
                    case R.id.drafts:
                        //设置
                        Intent intent4 = new Intent(MainActivity.this,ReservationSActivity.class);
                        intent4.putExtra("title","我的预约");
                        startActivity(intent4);
                        return true;
                    case R.id.allmail:
                        //使用指南
                        Intent intent5 = new Intent(MainActivity.this,ReservationRActivity.class);
                        intent5.putExtra("title","被预约信息");
                        startActivity(intent5);
                        return true;
                    case R.id.trash:
                        //关于我们
                        Intent intent6 = new Intent(MainActivity.this,TemplateActivity.class);
                        intent6.putExtra("title","关于我们");
                        startActivity(intent6);
                        return true;
                    default:
                        return true;

                }
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {

                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;//普通定位（一次）
        mCurrentMarker = null;//默认图标
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
//        option.setScanSpan(0);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
        initData();

        mBaiduMap.setOnMarkerClickListener(this);
    }

    private void initSearch() {
        mSearchView.setAlpha(0.7f);
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {
                if(oldQuery.equals("")) mSearchView.setAlpha(1.0f);
                else if (newQuery.equals("")) mSearchView.setAlpha(0.7f);
            }
        });
    }

    private void initData() {
        dialog.show();
        Picasso.with(getContext()).load(preferences.getString("headpic","")).placeholder(R.drawable.default_image).into(headpic);//set bg
        username.setText(preferences.getString("username",""));
        phone.setText(preferences.getString("phone",""));
        asyncHttpClient.post(BASEURL + "bookStore/getAllBookStore",new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(JSONObject response) {
                super.onSuccess(response);
                myBike = new ArrayList<LatLng>();
                try {
                    String s = response.getString("obj");
                    //准备 marker 的图片
                    BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.bike_marker);
                    List<Bookstore> bikeInfos = stringToList(response.getString("obj"),Bookstore.class);

                    mBaiduMap.clear();
                    for (Bookstore i : bikeInfos) {
                        //准备 marker option 添加 marker 使用
                        MarkerOptions markerOptions = new MarkerOptions().icon(bitmap).position(new LatLng(i.getAddress().getLatitude(), i.getAddress().getLongtitude()));
                        //获取添加的 marker 这样便于后续的操作
                        Marker marker = (Marker) mBaiduMap.addOverlay(markerOptions);
                        Bundle bundle = new Bundle();
                        bundle.putString("storeDescribe",i.getStoreDescribe());
                        bundle.putString("storeName",i.getStoreName());
                        bundle.putString("storeId",i.getStoreId()+"");
                        marker.setExtraInfo(bundle);
                    }
                    dialog.dismiss();
                } catch (JSONException e) {
                    dialog.dismiss();
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(Throwable error) {
                super.onFailure(error);
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "获取数据失败，请联系管理员！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initGps(final boolean isUseBike) {
        locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager
                .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            Toast.makeText(MainActivity.this, "请打开GPS",
                    Toast.LENGTH_SHORT).show();
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setCancelable(false);
            dialog.setTitle("定位未打开");
            dialog.setMessage("您需要在系统设置中打开GPS定位\n才可用车");
            dialog.setPositiveButton("去设置",
                    new android.content.DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                            // 转到手机设置界面，用户设置GPS
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 0); // 设置完成后返回到原来的界面

                        }
                    });
            dialog.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                }
            });
            dialog.show();
        }else {
            if (isUseBike) {
                Intent i = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(i, REQUEST_QR_CODE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this,RecentActiveActivity.class);
            intent.putExtra("title","近期活动");
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {// 退出时销毁定位
        mLocationClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        initData();
        mMapView.onResume();
        isopen = false;
    }
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
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
            asyncHttpClient.post(BASEURL+"book/getBooksByISBN",rp,new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(JSONObject response) {
                    super.onSuccess(response);
                    try {
                        String retcode = response.getString("retcode");
                        String errMsg = response.getString("errorMsg");
                        String obj = response.getString("obj");
                        if (retcode.equals("0001")) {
                            Toast.makeText(MainActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            final String serverResponse = response.toString();
                            Toast.makeText(getApplicationContext(), errMsg, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            Intent intent = new Intent(MainActivity.this, BookOptionActivity.class);
                            intent.putExtra("serverResponse",serverResponse);
                            startActivity(intent);
                        }
                    } catch(JSONException e){
                        dialog.dismiss();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Throwable e, JSONObject errorResponse) {
                    super.onFailure(e, errorResponse);
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "网络出错，请检查网络！", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.useBike:
                initGps(true);

                break;
            case R.id.ReportMsg:
                break;
            case R.id.getMyLocation:
                mLocationClient.requestLocation();
                break;
        }
    }

    /**
     *  图标(Marker) 点击监听事件
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        View view = LayoutInflater.from(this).inflate(R.layout.infowindow, null);
        Button b = (Button) view.findViewById(R.id.btn_info);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = (View)v.getParent().getParent();
                TextView text = (TextView)view.findViewById(R.id.store_id);
                String store_id = text.getText().toString().substring(4);
                Intent intent = new Intent(MainActivity.this,ShowBookShelfActivity.class);
                intent.putExtra("storeid",store_id);
                startActivity(intent);
            }
        });
        Bundle bundle = marker.getExtraInfo();
        ((TextView)view.findViewById(R.id.store_id)).setText("NO. "+bundle.getString("storeId","无数据"));
        ((TextView)view.findViewById(R.id.store_name)).setText(bundle.getString("storeName","无数据"));
        ((TextView)view.findViewById(R.id.store_describe)).setText(bundle.getString("storeDescribe","无数据"));
        // 定义用于显示该InfoWindow的坐标点
        // 创建InfoWindow的点击事件监听者
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaiduMap.hideInfoWindow();
            }
        });
        InfoWindow mInfoWindow = new InfoWindow(view,marker.getPosition(),-130);
        mBaiduMap.showInfoWindow(mInfoWindow); //显示气泡
        return true;
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(18.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            GeoHash g = new GeoHash(ll.latitude,ll.longitude);
            Gson gson = new Gson();
            currentPositionGEO = gson.toJson(g.getGeoHashBase32For9());
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    /**
     * 把json 字符串转化成list
     * @param json
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> List<T> stringToList(String json , Class<T> cls  ){
        Gson gson = new Gson();
        List<T> list = new ArrayList<T>();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        for(final JsonElement elem : array){
            list.add(gson.fromJson(elem, cls));
        }
        return list ;
    }


    /**
     * 监听搜索按钮
     * @param event
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER&&!isopen){
            /*隐藏软键盘*/
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if(inputMethodManager.isActive()){
                inputMethodManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);
            }

            if(currentPositionGEO == null){
                Toast.makeText(this, "定位后才能帮你找到合适的书呀~", Toast.LENGTH_SHORT).show();
                mLocationClient.requestLocation();
                return true;
            }
            dialog.show();
            Intent intent = new Intent(MainActivity.this,SearchResultActivity.class);
            intent.putExtra("keyword",mSearchView.getQuery());
            intent.putExtra("pos",currentPositionGEO);
            startActivity(intent);
            isopen = true;
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public static void finishMain(){
        mainActivity.finish();
    }
}
