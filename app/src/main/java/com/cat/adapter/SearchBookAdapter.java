package com.cat.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.cat.R;
import com.cat.activity.BookShelfActivity;
import com.cat.activity.ShowBookShelfActivity;
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

import dmax.dialog.SpotsDialog;

import static com.baidu.mapapi.BMapManager.getContext;
import static com.cat.activity.MainActivity.stringToList;
import static com.cat.activity.SearchResultActivity.finishSearchResult;

/**
 * Created by 雨晨 on 2017/6/6.
 */

public class SearchBookAdapter extends BaseAdapter {
    private ArrayList<HashMap<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;

    //网络请求相关
    @TAInject
    private TASyncHttpClient syncHttpClient;
    @TAInject
    private AsyncHttpClient asyncHttpClient;
    final String BASEURL = "http://115.159.35.11:8080/bookstore/restful/";

    //控件声明
    AlertDialog dialog;

    public SearchBookAdapter(Context c ,ArrayList<HashMap<String, Object>> data) {
        this.data = data;
        this.context = c;
        this.layoutInflater = LayoutInflater.from(context);
        asyncHttpClient = new AsyncHttpClient();
        dialog = new SpotsDialog(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * android绘制每一列的时候，都会调用这个方法
     */
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        View v = layoutInflater.inflate(R.layout.search_book_result_item,null);
        ImageView image = (ImageView) v.findViewById(R.id.bookImage);
        TextView title = (TextView) v.findViewById(R.id.bookName);
        TextView author = (TextView) v.findViewById(R.id.author);
        TextView username = (TextView) v.findViewById(R.id.username);
        TextView phone = (TextView) v.findViewById(R.id.phone);
        Button btn_reverse = (Button) v.findViewById(R.id.btn_reverse);
        Button btn_viewBookStore = (Button) v.findViewById(R.id.viewbookstore);

        title.setText((CharSequence) data.get(i).get("title"));
        author.setText((CharSequence) data.get(i).get("author"));
        username.setText((CharSequence) data.get(i).get("username"));
        phone.setText((CharSequence) data.get(i).get("phone"));
        Picasso.with(getContext()).load((String) data.get(i).get("bookimage"))
                .placeholder(R.drawable.default_image).into(image);//set bookimage

        btn_reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //预约书籍
                RequestParams rp = new RequestParams();
                SharedPreferences s = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
                String userid = s.getString("userid","");
                if(userid.equals("")){
                    Toast.makeText(context, "登录超时，请重新登录后尝试！", Toast.LENGTH_SHORT).show();
                    return;
                }
                rp.put("userid",userid);
                rp.put("bookid", data.get(i).get("bookid")+"");
                asyncHttpClient.post(BASEURL+"book/reversebook",rp,new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(JSONObject response) {
                        super.onSuccess(response);
                        try {
                            String retcode = response.getString("retcode");
                            String errMsg = response.getString("errorMsg");
                            String obj = response.getString("obj");
                            Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            finishSearchResult();
                        } catch(JSONException e){
                            dialog.dismiss();
                            Toast.makeText(context, "服务器出错，请联系管理员！", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        super.onFailure(error);
                        dialog.dismiss();
                        Toast.makeText(context, "网络出错，请检查网络！", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        btn_viewBookStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //传递参数userid，获取书架信息
                Intent intent= new Intent(context, ShowBookShelfActivity.class);
                intent.putExtra("storeid", (String) data.get(i).get("storeid"));
                context.startActivity(intent);
            }
        });
        return v;
    }
}
