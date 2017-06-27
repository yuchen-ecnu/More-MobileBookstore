package com.cat.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.cat.R;
import com.cat.adapter.BookLendHistoryAdapter;
import com.cat.entity.BookLendHistoryItem;
import com.ta.TASyncHttpClient;
import com.ta.annotation.TAInject;
import com.ta.util.http.AsyncHttpClient;
import com.ta.util.http.JsonHttpResponseHandler;
import com.ta.util.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;

import dmax.dialog.SpotsDialog;

import static com.cat.activity.MainActivity.stringToList;

public class BookLendHistoryActivity extends AppCompatActivity {

    //声明自有变量
    private Toolbar title;
    private String userId;
    private List<BookLendHistoryItem> list = new ArrayList<>();

    //控件声明
    private android.app.AlertDialog dialog;

    //共享变量
    private SharedPreferences sharedPreferences;

    //网络请求相关
    @TAInject
    private TASyncHttpClient syncHttpClient;
    @TAInject
    private AsyncHttpClient asyncHttpClient;
    final String BASEURL = "http://115.159.35.11:8080/bookstore/restful/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置android 4.4以上即api19以上的状态栏为半透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow ().getAttributes ();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        setContentView(R.layout.activity_book_lend_history);
        initview();
        loadData();
    }

    private void initview() {
        //网络相关
        asyncHttpClient = new AsyncHttpClient();
        dialog = new SpotsDialog(this);
        //绑定控件
        title = (Toolbar) findViewById(R.id.template_toolbar);

        //   初始化ToolBar
        title.setTitle(getIntent().getStringExtra("title"));
        setSupportActionBar(title);
        title.setNavigationIcon(R.drawable.return_btn);//设置返回icon
        title.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void loadData() {
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        //list.clear();
        userId = sharedPreferences.getString("userid", null);

        dialog.show();
        RequestParams rp = new RequestParams();
        rp.put("userid", userId);
        asyncHttpClient.post(BASEURL + "bookLendHistory/getBooksLendHistroyByUserId",rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                super.onSuccess(response);
                try {
                    String retcode = response.getString("retcode");
                    if (retcode != null && !retcode.equals("0000")) {
                        String errorMsg = response.getString("errorMsg");
                        Toast.makeText(BookLendHistoryActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        String s = response.getJSONArray("obj").toString();
                        List<BookLendHistoryItem> bookLendHistory = stringToList(response.getJSONArray("obj").toString(),BookLendHistoryItem.class);
                        ListView listView = (ListView) findViewById(R.id.history_list_view);
                        for(BookLendHistoryItem b : bookLendHistory){
                            if(b.getBookName()== ""){
                                b.setBookName("暂无书名");
                            }
                            if(b.getOperTime() == ""){
                                b.setOperTime(flushLeft(' ',10,"操作时间:")+"暂无");
                            } else { b.setOperTime(flushLeft(' ',10,"操作时间:")+b.getOperTime()); }
                            if(b.getISBN() == ""){
                                b.setISBN(flushLeft(' ',19,"ISBN:")+"暂无");
                            } else { b.setISBN(flushLeft(' ',19,"ISBN:")+b.getISBN()); }
                            if(b.getMessage() == ""){
                                b.setMessage(flushLeft(' ',10,"附加消息:")+"暂无");
                            } else { b.setMessage(flushLeft(' ',10,"附加消息:")+b.getMessage()); }
                        }
                        BookLendHistoryAdapter adapter = new BookLendHistoryAdapter(BookLendHistoryActivity.this,R.layout.content_book_lend_history_item,bookLendHistory);
                        listView.setAdapter(adapter);
                        dialog.dismiss();
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
                Toast.makeText(BookLendHistoryActivity.this, "网络出错，请检查网络！", Toast.LENGTH_SHORT).show();
            }
        });

    }
    //为了实现左对齐，填充字符串
    private String flushLeft(char c, long length, String content){
        String str = "";
        long cl = 0;
        String cs = "";
        if (content.length() > length){
            str = content;
        }else{
            for (int i = 0; i < length - content.length(); i++){
                cs = cs + c;
            }
        }
        str = content + cs;
        return str;
    }

}
