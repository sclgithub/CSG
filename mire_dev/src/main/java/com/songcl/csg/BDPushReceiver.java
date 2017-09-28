package com.songcl.csg;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.google.gson.Gson;
import com.songcl.csg.bean.BaseBean;
import com.songcl.csg.bean.ConfigBean;
import com.songcl.csg.bean.UpdateBean;

import java.util.List;

/**
 * Created by songchunlin on 16/8/30.
 */
public class BDPushReceiver extends PushMessageReceiver {
    private Gson mGson = new Gson();

    @Override
    public void onBind(Context context, int i, String s, String s1, String s2, String s3) {
        String responseString = "onBind errorCode=" + i + " appid="
                + s + " userId=" + s1 + " channelId=" + s2
                + " requestId=" + s3;
        Log.e("BDPushReceiver:", responseString);

    }

    @Override
    public void onUnbind(Context context, int i, String s) {

    }

    @Override
    public void onSetTags(Context context, int i, List<String> list, List<String> list1, String s) {

    }

    @Override
    public void onDelTags(Context context, int i, List<String> list, List<String> list1, String s) {

    }

    @Override
    public void onListTags(Context context, int i, List<String> list, String s) {

    }

    @Override
    public void onMessage(Context context, String s, String s1) {

        String message = s + s1;
        Log.e("onMessage:", message);
        try {
            BaseBean baseBean = mGson.fromJson(message.toString().replace("null", ""), BaseBean.class);
            if (baseBean != null && baseBean.getFlag().equals("update")) {
                UpdateBean updateBean = mGson.fromJson(message.toString().replace("null", ""), UpdateBean.class);
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
                ConfigBean configBean = mGson.fromJson(message.toString().replace("null", ""), ConfigBean.class);
                SharedPreferencePUtils.put(context, "auto", configBean.isAutoMsg());
                SharedPreferencePUtils.put(context, "cb_auto_open", configBean.isAutoOpen());
                SharedPreferencePUtils.put(context, "cb_auto_notification", configBean.isAutoNotification());
                Toast.makeText(context, "配置修改成功", Toast.LENGTH_SHORT).show();
            }
            context.sendBroadcast(new Intent("notify"));
        } catch (com.google.gson.JsonSyntaxException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onNotificationClicked(Context context, String s, String s1, String s2) {

    }

    @Override
    public void onNotificationArrived(Context context, String s, String s1, String s2) {

    }
}
