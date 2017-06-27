package com.cat.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cat.R;
import com.cat.adapter.PersonData;
import com.cat.entity.User;
import com.ta.TASyncHttpClient;
import com.ta.annotation.TAInject;
import com.ta.util.http.AsyncHttpClient;
import com.ta.util.http.JsonHttpResponseHandler;
import com.ta.util.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import dmax.dialog.SpotsDialog;

import static com.cat.activity.MainActivity.stringToList;

/**
 * Created by 汤圆儿 on 2017/5/17.
 */

public class EditNameActivity extends AppCompatActivity implements View.OnClickListener{
    private Toolbar title;
    private Toast mToast;
    private EditText p_name;
    private Button button;
    Dialog dialog;
    String n;
    String pid;
    String username;

    //网络请求相关
    @TAInject
    private TASyncHttpClient syncHttpClient;
    @TAInject
    private AsyncHttpClient asyncHttpClient;
    final String BASEURL = "http://115.159.35.11:8080/bookstore/restful/";

    //存储相关
    SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置android 4.4以上即api19以上的状态栏为半透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow ().getAttributes ();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        setContentView(R.layout.edit_name);
        initview();
        initdata();
    }

    private void initview() {
        //绑定控件
        title = (Toolbar) findViewById(R.id.toolbar3);
        dialog = new SpotsDialog(this);
        button = (Button) findViewById(R.id.button_save);
        p_name = (EditText) findViewById(R.id.editText);

        button.setOnClickListener(this);
        //初始化ToolBar
        title.setTitle("修改昵称");
        setSupportActionBar(title);
        title.setNavigationIcon(R.drawable.return_btn);//设置返回icon
        title.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        asyncHttpClient = new AsyncHttpClient();
    }

    private void initdata()
    {
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        n = preferences.getString("username","");
        p_name.setText(n);
    }

//    private void initlistener() {
//        RequestParams rp = new RequestParams();
//        rp.put("username",p_name.getText().toString() );
//        asyncHttpClient.post(BASEURL + "user/login",rp,new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(JSONObject response) {
//                super.onSuccess(response);
//                try {
//                    String retcode = response.getString("retcode");
//                    String errMsg = response.getString("errorMsg");
//                    String obj = response.getString("obj");
//                    if (retcode.equals("0001")) {
//                        Toast.makeText(EditNameActivity.this, errMsg, Toast.LENGTH_SHORT).show();
//                        dialog.dismiss();
//                    } else {
//                        List<User> list = stringToList(obj, User.class);
//                        User user = list.get(0);
//                        //存储用户资料到本地
//                        SharedPreferences.Editor editor = preferences.edit();
//                        editor.putString("userid", user.getUserId() + "");
//                        editor.putString("username",user.getUserName()+"");
//                        editor.commit();
//                        dialog.dismiss();
//                        Toast.makeText(getApplicationContext(), "登陆成功！", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(EditNameActivity.this, MainActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                } catch(JSONException e){
//                    dialog.dismiss();
//                    e.printStackTrace();
//                }
//            }
//            @Override
//            public void onFailure(Throwable error) {
//                super.onFailure(error);
//                dialog.dismiss();
//                Toast.makeText(EditNameActivity.this, "保存失败，请检查网络！", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            Intent intent = new Intent(EditNameActivity.this,PersonDataActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_save:
                if(("").equals(p_name.getText().toString())) {
                    Toast.makeText(this, "昵称不得为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.show();
                RequestParams rp = new RequestParams();
                username = p_name.getText().toString();
                pid = preferences.getString("userid","");
                rp.put("username",username);
                rp.put("id",pid);
                asyncHttpClient.post(BASEURL + "user/update",rp,new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(JSONObject response) {
                        super.onSuccess(response);
                        try{
                            String retcode = response.getString("retcode");
                            String errMsg = response.getString("errorMsg");
                            String obj = response.getString("obj");
                            if (retcode.equals("0001")){
                                Toast.makeText(EditNameActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                            else
                            {
                                //存储用户资料到本地
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("username",username);
                                editor.commit();
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "修改成功！", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }catch(JSONException e){
                            dialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(Throwable e, JSONObject errorResponse) {
                        super.onFailure(e, errorResponse);
                        dialog.dismiss();
                        Toast.makeText(EditNameActivity.this, "修改失败，请检查网络！", Toast.LENGTH_SHORT).show();
                    }
                    });
        }
    }
}
