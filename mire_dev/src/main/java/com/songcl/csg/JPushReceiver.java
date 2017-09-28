package com.songcl.csg;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.songcl.csg.bean.BaseBean;
import com.songcl.csg.bean.ConfigBean;
import com.songcl.csg.bean.UpdateBean;

import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

public class JPushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPushReceiver";
    private Gson mGson = new Gson();
    private NotificationManager nm;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null == nm) {
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        Bundle bundle = intent.getExtras();
        Log.e(TAG, "onReceive - " + intent.getAction() + ", extras: " + bundle.toString());

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            Log.e(TAG, "JPush用户注册成功");

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.e(TAG, "接受到推送下来的自定义消息:" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            try {
                BaseBean baseBean = mGson.fromJson(bundle.getString(JPushInterface.EXTRA_MESSAGE), BaseBean.class);
                if (baseBean != null && baseBean.getFlag().equals("update")) {
                    UpdateBean updateBean = mGson.fromJson(bundle.getString(JPushInterface.EXTRA_MESSAGE), UpdateBean.class);
                    if (updateBean.getRedBack() != null && updateBean.getRedBack().length() > 0) {
                        SharedPreferencePUtils.put(context, "redBack", updateBean.getRedBack());
                    }
                    if (updateBean.getRedMsg() != null && updateBean.getRedMsg().length() > 0) {
                        SharedPreferencePUtils.put(context, "redMsg", updateBean.getRedMsg());
                    }
                    if (updateBean.getRedOpen() != null && updateBean.getRedOpen().length() > 0) {
                        SharedPreferencePUtils.put(context, "redOpen", updateBean.getRedOpen());
                    }
                    if (updateBean.getInfo() != null && updateBean.getInfo().length() > 0) {
                        SharedPreferencePUtils.put(context, "info", updateBean.getInfo());
                    }
                    Toast.makeText(context, "更新助手成功", Toast.LENGTH_SHORT).show();
                    SharedPreferencePUtils.put(context, "updateTime", System.currentTimeMillis());
                } else if (baseBean != null && baseBean.getFlag().equals("config")) {
                    ConfigBean configBean = mGson.fromJson(bundle.getString(JPushInterface.EXTRA_MESSAGE), ConfigBean.class);
                    SharedPreferencePUtils.put(context, "auto", configBean.isAutoMsg());
                    SharedPreferencePUtils.put(context, "cb_auto_open", configBean.isAutoOpen());
                    SharedPreferencePUtils.put(context, "cb_auto_notification", configBean.isAutoNotification());
                    Toast.makeText(context, "配置修改成功", Toast.LENGTH_SHORT).show();
                }
                context.sendBroadcast(new Intent("notify"));
            } catch (com.google.gson.JsonSyntaxException e) {
                e.printStackTrace();
            }

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.e(TAG, "接受到推送下来的通知");

            receivingNotification(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.e(TAG, "用户点击打开了通知");

            openNotification(context, bundle);

        } else {
            Log.e(TAG, "Unhandled intent - " + intent.getAction());
        }
    }

    private void receivingNotification(Context context, Bundle bundle) {
        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        Log.e(TAG, " title : " + title);
        String message = bundle.getString(JPushInterface.EXTRA_ALERT);
        Log.e(TAG, "message : " + message);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Log.e(TAG, "extras : " + extras);
    }

    private void openNotification(Context context, Bundle bundle) {
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        String myValue = "";
        try {
            JSONObject extrasJson = new JSONObject(extras);
            myValue = extrasJson.optString("myKey");
        } catch (Exception e) {
            Log.e(TAG, "Unexpected: extras is not a valid json", e);
            return;
        }
    }
}