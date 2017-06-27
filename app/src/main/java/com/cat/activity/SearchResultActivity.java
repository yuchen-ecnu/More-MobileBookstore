package com.cat.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
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
import com.cat.R;
import com.cat.adapter.SearchBookAdapter;
import com.cat.entity.SearchBookJson;
import com.squareup.picasso.Picasso;
import com.ta.TASyncHttpClient;
import com.ta.annotation.TAInject;
import com.ta.util.http.AsyncHttpClient;
import com.ta.util.http.JsonHttpResponseHandler;
import com.ta.util.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;

import static com.baidu.mapapi.BMapManager.getContext;
import static com.cat.activity.MainActivity.stringToList;

public class SearchResultActivity extends AppCompatActivity implements BaiduMap.OnMarkerClickListener{
    //声明自有变量
    private Toolbar title;
    private ArrayList<HashMap<String, Object>> list_data;

    //百度地图变量声明
    public LocationClient mLocationClient;
    public BDLocationListener myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    //控件声明
    private AlertDialog dialog;
    private ListView searchlist;

    //网络请求相关
    @TAInject
    private TASyncHttpClient syncHttpClient;
    @TAInject
    private AsyncHttpClient asyncHttpClient;
    final String BASEURL = "http://115.159.35.11:8080/bookstore/restful/";

    private static SearchResultActivity searchResultActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        initView();
        initData();
    }

    private void initData() {
        dialog.show();

        String keyword = getIntent().getStringExtra("keyword");
        String positionGEO = getIntent().getStringExtra("pos");
        if(keyword==null||keyword.equals("")){
            Toast.makeText(this, "输入关键词试试吧~", Toast.LENGTH_SHORT).show();
            return;
        }else if(positionGEO==null||positionGEO.equals("")){
            Toast.makeText(this, "请定位后再试试呀~", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestParams rp = new RequestParams();
        rp.put("keyword",keyword);
        rp.put("positionGEO",positionGEO);
        asyncHttpClient.post(BASEURL+"book/getBooksByKeyword",rp,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(JSONObject response) {
                super.onSuccess(response);
                try {
                    String retcode = response.getString("retcode");
                    String errMsg = response.getString("errorMsg");
                    String obj = response.getString("obj");
                    if (retcode.equals("0001")) {
                        Toast.makeText(SearchResultActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        List<SearchBookJson> books = stringToList(response.getString("obj"),SearchBookJson.class);
                        mBaiduMap.clear();
                        for (SearchBookJson i : books) {
                            /*标记Marker*/
                            //准备 marker 的图片
                            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.bike_marker);
                            //准备 marker option 添加 marker 使用
                            MarkerOptions markerOptions = new MarkerOptions().icon(bitmap).position(new LatLng(i.getLat(), i.getLon()));
                            //获取添加的 marker 这样便于后续的操作
                            Marker marker = (Marker) mBaiduMap.addOverlay(markerOptions);
                            Bundle bundle = new Bundle();
                            bundle.putString("username",i.getUserName());
                            bundle.putString("phone",i.getPhone());
                            bundle.putString("img",i.getHeadpic());
                            bundle.putString("storeid",i.getStoreid());

                            marker.setExtraInfo(bundle);
                        }

                        //建立list
                        getData(books);
                        SearchBookAdapter adapter = new SearchBookAdapter(SearchResultActivity.this,list_data);
                        searchlist.setAdapter(adapter);
                        dialog.dismiss();
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
                Toast.makeText(SearchResultActivity.this, "网络出错，请检查网络！", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initView() {
        //绑定控件
        title = (Toolbar) findViewById(R.id.template_toolbar);
        searchResultActivity = this;
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

        // 地图初始化
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;//普通定位（一次）
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

        mBaiduMap.setOnMarkerClickListener(this);

        dialog = new SpotsDialog(this);
        asyncHttpClient = new AsyncHttpClient();
        searchlist = (ListView) findViewById(R.id.searchbooklist);

        searchlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mBaiduMap.clear();
                HashMap<String, Object> info = list_data.get(i);
                /*标记Marker*/
                //准备 marker 的图片
                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.bike_marker);
                //准备 marker option 添加 marker 使用
                MarkerOptions markerOptions = new MarkerOptions().icon(bitmap).position(new LatLng((Double)info.get("lat"),(Double)info.get("lon")));
                //获取添加的 marker 这样便于后续的操作
                Marker marker = (Marker) mBaiduMap.addOverlay(markerOptions);
                Bundle bundle = new Bundle();
                bundle.putString("username", (String) info.get("username"));
                bundle.putString("phone",(String) info.get("phone"));
                bundle.putString("img",(String) info.get("img"));
                bundle.putString("storeid",(String) info.get("storeid"));

                marker.setExtraInfo(bundle);
            }
        });

        list_data = new ArrayList<HashMap<String, Object>>();
    }

    private void getData(List<SearchBookJson> books) {
        list_data.clear();
        for (SearchBookJson j : books){
            HashMap h = new HashMap();
            h.put("author",j.getAuthor());
            h.put("bookimage",j.getImage());
            h.put("phone",j.getPhone());
            h.put("username",j.getUserName());
            h.put("title",j.getTitle());
            h.put("userid",j.getUserId());
            h.put("bookid",j.getBookId());
            h.put("lat",j.getLat());
            h.put("lon",j.getLon());
            h.put("img",j.getHeadpic());
            h.put("storeid",j.getStoreid());
            list_data.add(h);
        }
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
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
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

        final Bundle bundle = marker.getExtraInfo();

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchResultActivity.this,ShowBookShelfActivity.class);
                intent.putExtra("storeid",bundle.getString("storeid"));
                startActivity(intent);
            }
        });

        ((TextView)view.findViewById(R.id.title1)).setText("用户名：");
        ((TextView)view.findViewById(R.id.title3)).setText("联系方式：");
        ((LinearLayout)view.findViewById(R.id.option)).setVisibility(View.GONE);
        ImageView headpic = (ImageView) view.findViewById(R.id.headview);

        Picasso.with(getContext()).load(bundle.getString("img")).placeholder(R.drawable.default_image).into(headpic);//set headpic
        ((TextView)view.findViewById(R.id.store_id)).setText(bundle.getString("username","无数据"));
        ((TextView)view.findViewById(R.id.store_name)).setText(bundle.getString("phone","无数据"));
        ((TextView)view.findViewById(R.id.store_describe)).setText(bundle.getString("img","无数据"));
        // 定义用于显示该InfoWindow的坐标点
        // 创建InfoWindow的点击事件监听者
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaiduMap.hideInfoWindow();
            }
        });
        InfoWindow mInfoWindow = new InfoWindow(view,marker.getPosition(),-170);
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
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }
    public static void finishSearchResult(){
        searchResultActivity.finish();
    }
}
