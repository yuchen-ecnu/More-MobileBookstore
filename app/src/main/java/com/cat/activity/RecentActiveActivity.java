package com.cat.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.cat.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 雨晨 on 2017/5/12.
 */

public class RecentActiveActivity extends AppCompatActivity {

    //声明自有变量
    private Toolbar title;
    private ListView listView;
    private List<Map<String, Object>> DATA;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recentactive);

        initview();
    }

    private void initview() {
        //绑定控件
        title = (Toolbar) findViewById(R.id.template_toolbar);
        listView = (ListView) findViewById(R.id.active_list);

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

        SimpleAdapter adapter = new SimpleAdapter(this, getDATA(), R.layout.activitylist_item,
                new String[]{"img"},
                new int[]{R.id.imageView2});

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(RecentActiveActivity.this,DetailActivity.class));
            }
        });
    }

    public List<? extends Map<String,?>> getDATA() {
        DATA = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;
        for (int i =0;i<10;i++) {
            map = new HashMap<String, Object>();
            map.put("img", R.drawable.t1);
            DATA.add(map);

            map = new HashMap<String, Object>();
            map.put("img", R.drawable.t2);
            DATA.add(map);

            map = new HashMap<String, Object>();
            map.put("img", R.drawable.t3);
            DATA.add(map);

            map = new HashMap<String, Object>();
            map.put("img", R.drawable.t4);
            DATA.add(map);

            map = new HashMap<String, Object>();
            map.put("img", R.drawable.t5);
            DATA.add(map);

            map = new HashMap<String, Object>();
            map.put("img", R.drawable.t6);
            DATA.add(map);

            map = new HashMap<String, Object>();
            map.put("img", R.drawable.t7);
            DATA.add(map);

        }

        return DATA;
    }
}