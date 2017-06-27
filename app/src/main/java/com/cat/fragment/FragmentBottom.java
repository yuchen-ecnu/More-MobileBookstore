package com.cat.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.cat.R;
import com.cat.activity.BookOptionActivity;
import com.cat.activity.InfoActivity;
import com.cat.activity.MainActivity;
import com.cat.entity.Travel;
import com.squareup.picasso.Picasso;
import com.ta.TASyncHttpClient;
import com.ta.annotation.TAInject;
import com.ta.util.http.AsyncHttpClient;
import com.ta.util.http.JsonHttpResponseHandler;
import com.ta.util.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;


public class FragmentBottom extends Fragment{
    static final String ARG_TRAVEL = "BTN_TRAVEL";
    Travel travel;

    @Bind(R.id.bookstoreid)
    TextView bookstoreid;
    @Bind(R.id.rating)
    ImageView rating;
    @Bind(R.id.headview)
    ImageView headView;
    @Bind(R.id.bookStoreMessage)
    TextView bookStoreMessage;
    Button btn_confirm;

    //控件声明
    private AlertDialog dialog;

    //存储相关
    SharedPreferences preferences;

    //网络请求相关
    @TAInject
    private TASyncHttpClient syncHttpClient;
    @TAInject
    private AsyncHttpClient asyncHttpClient;
    final String BASEURL = "http://115.159.35.11:8080/bookstore/restful/";

    public static FragmentBottom newInstance(Travel travel) {
        Bundle args = new Bundle();
        FragmentBottom fragmentBottom = new FragmentBottom();args.putParcelable(ARG_TRAVEL, travel);
        fragmentBottom.setArguments(args);
        return fragmentBottom;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            travel = args.getParcelable(ARG_TRAVEL);
        }
        preferences = getActivity().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        initView();
    }

    private void initView() {
        asyncHttpClient = new AsyncHttpClient();
        dialog = new SpotsDialog(getContext());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom, null);

        ButterKnife.bind(this, view);
        btn_confirm = (Button) view.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder confirm_dialog =
                        new AlertDialog.Builder(getActivity());
                confirm_dialog.setTitle("确认借书？");
                confirm_dialog.setMessage("确认借阅这本书？你将扣去1点借书积分。");
                confirm_dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), "取消操作", Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
                confirm_dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogc, int which) {
                        dialog.show();
                        RequestParams rp = new RequestParams();
                        rp.put("bookid",travel.getBookid());
                        rp.put("id",preferences.getString("userid",""));
                        asyncHttpClient.post(BASEURL+"book/borrowBook",rp,new JsonHttpResponseHandler(){
                            @Override
                            public void onSuccess(JSONObject response) {
                                super.onSuccess(response);
                                try {
                                    String retcode = response.getString("retcode");
                                    String errMsg = response.getString("errorMsg");
                                    String obj = response.getString("obj");
                                    if (!retcode.equals("0000")) {
                                        Toast.makeText(getContext(), errMsg, Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    } else {
                                        dialog.dismiss();
                                        Toast.makeText(getContext(), errMsg, Toast.LENGTH_SHORT).show();
                                        if(!obj.equals("")) {
                                            AlertDialog.Builder message =
                                                    new AlertDialog.Builder(getActivity());
                                            message.setTitle("留言");
                                            message.setMessage(obj);
                                            message.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    getActivity().finish();
                                                }
                                            });
                                            message.show();
                                        }else{
                                            getActivity().finish();
                                        }
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
                            }
                        });
                    }
                });
                confirm_dialog.show();


            }
        });
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (travel != null) {
            Picasso.with(getContext()).load(travel.getHeadPic()).placeholder(R.drawable.default_image).into(headView);
            bookstoreid.setText("书架编号：NO."+travel.getStoreID());
            bookStoreMessage.setText(travel.getBookStoreMessage());
        }

    }

    @SuppressWarnings("unchecked")
    private void startInfoActivity(View view, Travel travel) {
        FragmentActivity activity = getActivity();
        ActivityCompat.startActivity(activity,
                InfoActivity.newInstance(activity, travel),
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity,
                        new Pair<>(view, getString(R.string.transition_image)))
                        .toBundle());
    }

}
