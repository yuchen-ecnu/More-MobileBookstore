package com.cat.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.cat.R;
import com.cat.entity.BookJson;
import com.cat.entity.Travel;
import com.squareup.picasso.Picasso;
import com.ta.util.http.AsyncHttpClient;
import com.ta.util.http.JsonHttpResponseHandler;
import com.ta.util.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.baidu.mapapi.BMapManager.getContext;
import static com.cat.activity.MainActivity.stringToList;

public class AddBookActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String EXTRA_TRAVEL = "EXTRA_TRAVEL";
    private AsyncHttpClient asyncHttpClient;
    private Travel travel;
    private String userId;
    private String ISBN;
    private String summary;
    @Bind(R.id.image) ImageView image;
    @Bind(R.id.title) TextView title;
    @Bind(R.id.isbn) TextView isbn;
    @Bind(R.id.author) TextView author;
    @Bind(R.id.rating) ImageView rating;
    @Bind(R.id.price) TextView price;
    @Bind(R.id.authortext) TextView authortext;
    @Bind(R.id.publisher) TextView publisher;
    @Bind(R.id.binding) TextView binding;
    @Bind(R.id.url) TextView url;
    @Bind(R.id.pages) TextView pages;
    @Bind(R.id.url_btn) Button url_btn;
    @Bind(R.id.isbnText) TextView isbnText;
    @Bind(R.id.add_btn) Button add_btn;
    @Bind(R.id.cancel_btn) Button cancel_btn;
    final String BASEURL = "http://115.159.35.11:8080/bookstore/restful/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        ButterKnife.bind(this);
        initData();
        initView();
        initListener();

    }

    private void initData() {
        Intent intent = getIntent();
        try {
            JSONObject doubanJson = new JSONObject(intent.getStringExtra("doubanResponse"));
            Travel t = new Travel();
            JSONArray a = doubanJson.getJSONArray("author");
            String tmp="";
            for (int j = 0;j<a.length()-1;j++){
                tmp+=a.get(j)+"|";
            }
            userId = intent.getStringExtra("userid");
            ISBN = doubanJson.getString("isbn13");
            summary = doubanJson.getString("summary");
            if(summary.length()>=100){
                summary = summary.substring(0,100);
            }

            tmp+=a.get(a.length()-1);
            JSONArray b = doubanJson.getJSONArray("tags");
            List<String> tags = new ArrayList<>();
            for (int j=0;j<b.length();j++){
                tags.add(b.getJSONObject(j).get("title").toString());
            }
            t.setTags(tags);
            t.setAuthor(tmp);
            t.setName(doubanJson.getString("title"));
            t.setIsbn(doubanJson.getString("isbn13"));
            t.setImage(doubanJson.getJSONObject("images").getString("large"));
            t.setRating(doubanJson.getJSONObject("rating").getString("average"));
            t.setPrice(doubanJson.getString("price"));
            t.setDoubanURL(doubanJson.getString("alt"));
            t.setPublisher(doubanJson.getString("publisher"));
            t.setPages(doubanJson.getString("pages"));
            t.setBinds(doubanJson.getString("binding"));
            travel = t;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initListener() {
        url_btn.setOnClickListener(this);
        add_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
    }

    private void initView() {
        if (travel != null) {
            Picasso.with(getContext()).load(travel.getImage()).placeholder(R.drawable.default_image).into(image);//set bg
            title.setText(travel.getName());

            List<String> tags = travel.getTags();//set tags
//            mTagContainerLayout.setTags(tags);

            author.setText("作者："+travel.getAuthor());
            isbn.setText("ISBN:"+travel.getIsbn());
            url.setText(travel.getDoubanURL());
            binding.setText(travel.getBinds());
            publisher.setText(travel.getPublisher());
            authortext.setText(travel.getAuthor());
            price.setText(travel.getPrice());
            pages.setText(travel.getPages());
            isbnText.setText(travel.getIsbn());
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.url_btn:
                Intent intent = new Intent(AddBookActivity.this,WebActivity.class);
                intent.putExtra("url",travel.getDoubanURL());
                startActivity(intent);
                break;
            case R.id.add_btn:
                AddMethod();
                break;
            case R.id.cancel_btn:
                this.finish();
                break;
        }
    }


    private void AddMethod() {
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date time = new Date();
        final String storeTime = formatter.format(time);
        final EditText inputString = new EditText(this);
        inputString.setHint("给借书的人留个言吧~");

        new AlertDialog.Builder(this).setTitle("友情提示").setMessage("确认要将这本人类的知识财宝上架吗？").setView(inputString)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RequestParams rp = new RequestParams();
                        rp.put("isbn",ISBN);
                        rp.put("userid",userId);
                        rp.put("author",travel.getAuthor());
                        rp.put("url",travel.getDoubanURL());
                        rp.put("binding",travel.getBinds());
                        rp.put("price",travel.getPrice());
                        rp.put("pages",travel.getPages());
                        rp.put("image",travel.getImage());
                        rp.put("rating",travel.getRating());
                        rp.put("publisher",travel.getPublisher());
                        rp.put("storetime",storeTime);
                        rp.put("title",travel.getName());
                        rp.put("summary",summary);
                        rp.put("message",inputString.getText().toString());
                        String s  = BASEURL + "bookShelf/addBook";
                        asyncHttpClient = new AsyncHttpClient();
                        asyncHttpClient.post(s,rp,new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                Toast.makeText(AddBookActivity.this, "书籍上架成功！积分+1", Toast.LENGTH_SHORT).show();
                                finish();

                            }
                            @Override
                            public void onFailure(Throwable error) {
                                Toast.makeText(AddBookActivity.this, "哎呀呀，出了点问题呢...咱表示已经习惯了", Toast.LENGTH_SHORT).show();
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
}
