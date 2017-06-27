package com.cat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.ta.TAApplication;
import com.ta.TASyncHttpClient;
import com.ta.annotation.TAInject;
import com.ta.util.http.AsyncHttpClient;
import com.ta.util.http.JsonHttpResponseHandler;
import com.ta.util.http.RequestParams;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.common.UmLog;
import com.umeng.message.entity.UMessage;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by 雨晨 on 2017/3/15.
 */

public class MyApplication extends TAApplication {
    private static final String TAG = MyApplication.class.getName();
    private Handler handler;

    private PushAgent mPushAgent;

    //存储相关
    SharedPreferences preferences;

    //网络请求相关
    @TAInject
    private TASyncHttpClient syncHttpClient;
    @TAInject
    private AsyncHttpClient asyncHttpClient;
    final String BASEURL = "http://115.159.35.11:8080/bookstore/restful/";

    @Override
    public void onCreate() {
        super.onCreate();
        initData();
        initPush();
        initBaiduSDK();
    }

    private void initBaiduSDK() {
        SDKInitializer.initialize(this);
    }

    private void initPush() {
        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setDebugMode(true);
        handler = new Handler();

        //sdk开启通知声音
//        mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);
        // sdk关闭通知声音
//		mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_DISABLE);
        // 通知声音由服务端控制
		mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);

		mPushAgent.setNotificationPlayLights(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);
		mPushAgent.setNotificationPlayVibrate(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);
        mPushAgent.setNoDisturbMode(0, 0, 0, 0);
        mPushAgent.setMuteDurationSeconds(1);
        mPushAgent.setDisplayNotificationNumber(3);
        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            /**
             * 自定义消息的回调方法
             * */
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {

                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        // 对自定义消息的处理方式，点击或者忽略
                        boolean isClickOrDismissed = true;
                        if (isClickOrDismissed) {
                            //自定义消息的点击统计
                            UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
                        } else {
                            //自定义消息的忽略统计
                            UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
                        }
                        Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
                    }
                });
            }


        };
        mPushAgent.setMessageHandler(messageHandler);

        /**
         * 自定义行为的回调处理，参考文档：高级功能-通知的展示及提醒-自定义通知打开动作
         * UmengNotificationClickHandler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * */
//        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
//            @Override
//            public void dealWithCustomAction(Context context, UMessage msg) {
//                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
//            }
//        };
//        //使用自定义的NotificationHandler，来结合友盟统计处理消息通知，参考http://bbs.umeng.com/thread-11112-1-1.html
//        //CustomNotificationHandler notificationClickHandler = new CustomNotificationHandler();
//        mPushAgent.setNotificationClickHandler(notificationClickHandler);

        // 统计应用启动数据
        PushAgent.getInstance(this).onAppStart();

        //注册推送服务 每次调用register都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                UmLog.i(TAG, "device token: " + deviceToken);
                preferences.edit().putString("devicetoken",deviceToken).commit();
                preferences.edit().putBoolean("isGetToken",true).commit();
                if(!preferences.getBoolean("isSend",false)&&preferences.getBoolean("isLogin",false)) {
                    //提交DeviceToken
                    preferences.edit().putBoolean("isSend",true).commit();
                    RequestParams rp = new RequestParams();
                    rp.put("devicetoken", deviceToken);
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
                                    Toast.makeText(MyApplication.this, errMsg, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Throwable error) {
                            super.onFailure(error);
                            Toast.makeText(MyApplication.this, "device Token上传失败！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(String s, String s1) {
                UmLog.i(TAG, "register failed: " + s + " " +s1);
            }
        });
    }

    private void initData() {
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        preferences.edit().putBoolean("isLogin",false).commit();
        preferences.edit().putBoolean("isGetToken",false).commit();
        preferences.edit().putBoolean("isSend",false).commit();
    }

}
