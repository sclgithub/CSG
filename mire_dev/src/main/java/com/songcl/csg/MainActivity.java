package com.songcl.csg;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private CheckBox mCBAuto, mCBAutoNotification, mCBAutoOpen;
    private boolean mAuto, mAutoNotification, mAutoOpen;
    private TextView mTVStatus, mTVFor, mTVUpdateTime, mTVInfo;
    private AlertDialog alertDialog;
    private Button mBTNFeedback;
    private ImageView mIVMP;
    private boolean scale = false;
    private SimpleDateFormat sdf;
    private Notify mNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, "A7FSDGvxGcP54ODW6Osz0hXD");

        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        getConfig();

        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Toast.makeText(this, wifiInfo.getSSID(), Toast.LENGTH_SHORT).show();
        sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        mNotify = new Notify();
        initViews();
        String s = "";
        try {

            s = telephonyManager.getLine1Number() != null ? telephonyManager.getLine1Number() : "无USIM卡";
            s = s + "@" + telephonyManager.getDeviceId();//weixin://profile/gh_c28e28a2510d
        } catch (Exception e) {
            s = "模拟器";
        }
        mTVFor.setText(s);

        setConfigStatus();

        mCBAuto.setOnCheckedChangeListener(this);
        mCBAutoNotification.setOnCheckedChangeListener(this);
        mCBAutoOpen.setOnCheckedChangeListener(this);
        mTVStatus.setOnClickListener(this);
        mBTNFeedback.setOnClickListener(this);
        mIVMP.setOnClickListener(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.scl);
        builder.setTitle(getText(R.string.notice));
        builder.setMessage(getText(R.string.notice_content));
        builder.setPositiveButton(getText(R.string.notice_action), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivityForResult(intent, 1001);
            }
        });
        alertDialog = builder.create();
        if (!isAccessibilitySettingsOn(this)) {
            alertDialog.show();
            mTVStatus.setText(getText(R.string.accessibility_stop));
            mTVStatus.setClickable(true);
        } else {
            mTVStatus.setText(getText(R.string.accessibility_running));
            mTVStatus.setClickable(false);
        }

    }

    private void setConfigStatus() {
        if (mAuto) {
            mCBAuto.setChecked(true);
        } else {
            mCBAuto.setChecked(false);
        }
        if (mAutoNotification) {
            mCBAutoNotification.setChecked(true);
        } else {
            mCBAutoNotification.setChecked(false);
        }
        if (mAutoOpen) {
            mCBAutoOpen.setChecked(true);
        } else {
            mCBAutoOpen.setChecked(false);
        }
    }

    private void getConfig() {
        mAuto = (boolean) SharedPreferencePUtils.get(this, "auto", false);
        mAutoNotification = (boolean) SharedPreferencePUtils.get(this, "cb_auto_notification", false);
        mAutoOpen = (boolean) SharedPreferencePUtils.get(this, "cb_auto_open", false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter("notify");
        registerReceiver(mNotify, intentFilter);
    }

    public int getWifiApState(Context mContext) {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApState");
            int i = (Integer) method.invoke(wifiManager);
            return i;
        } catch (Exception e) {
            return 0;
        }
    }

    private void initViews() {
        mCBAuto = (CheckBox) findViewById(R.id.cb_auto);
        mTVStatus = (TextView) findViewById(R.id.tv_status);
        mTVFor = (TextView) findViewById(R.id.tv_for);
        mCBAutoNotification = (CheckBox) findViewById(R.id.cb_auto_notification);
        mCBAutoOpen = (CheckBox) findViewById(R.id.cb_auto_open);
        mBTNFeedback = (Button) findViewById(R.id.feedback);
        mIVMP = (ImageView) findViewById(R.id.iv_mp);
        mIVMP.setAlpha(0f);
        mTVUpdateTime = (TextView) findViewById(R.id.tv_update_time);
        mTVInfo = (TextView) findViewById(R.id.tv_info);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            if (mAuto) {
                Toast.makeText(MainActivity.this, getText(R.string.auto_running), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, getText(R.string.running), Toast.LENGTH_SHORT).show();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.cb_auto:
                if (b) {
                    SharedPreferencePUtils.put(this, "auto", true);
                    mAuto = true;
                } else {
                    SharedPreferencePUtils.put(this, "auto", false);
                    mAuto = false;
                }
                break;
            case R.id.cb_auto_open:
                if (b) {
                    SharedPreferencePUtils.put(this, "cb_auto_open", true);
                    mAutoOpen = true;
                } else {
                    SharedPreferencePUtils.put(this, "cb_auto_open", false);
                    mAutoOpen = false;
                }
                break;
            case R.id.cb_auto_notification:
                if (b) {
                    SharedPreferencePUtils.put(this, "cb_auto_notification", true);
                    mAutoNotification = true;
                } else {
                    SharedPreferencePUtils.put(this, "cb_auto_notification", false);
                    mAutoNotification = false;
                }
                break;
        }
    }

    // To check if service is enabled
    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + RedAccessibilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        } else {
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if (!isAccessibilitySettingsOn(this)) {
                alertDialog.show();
                mTVStatus.setText(getText(R.string.accessibility_stop));
                mTVStatus.setClickable(true);
            } else {
                mTVStatus.setText(getText(R.string.accessibility_running));
                mTVStatus.setClickable(false);
                alertDialog.dismiss();
            }
        }

    }

    class Notify extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            setData();
            getConfig();
            setConfigStatus();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isAccessibilitySettingsOn(this)) {
            alertDialog.show();
            mTVStatus.setText(getText(R.string.accessibility_stop));
            mTVStatus.setClickable(true);
        } else {
            mTVStatus.setText(getText(R.string.accessibility_running));
            mTVStatus.setClickable(false);
        }
        setData();
    }

    private void setData() {
        long updateTime = (long) SharedPreferencePUtils.get(this, "updateTime", 0l);
        Date date = new Date(updateTime);
        mTVUpdateTime.setText("助手更新日期：" + sdf.format(date).toString());
        String info = (String) SharedPreferencePUtils.get(this, "info", "");
        mTVInfo.setText(info);
        if (info.length() > 0) {
            mTVInfo.setVisibility(View.VISIBLE);
        } else {
            mTVInfo.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_status:
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivityForResult(intent, 1001);
                break;
            case R.id.feedback:
                Intent data = new Intent(Intent.ACTION_SENDTO);
                data.setData(Uri.parse("mailto:songchunlin1314@qq.com"));
                data.putExtra(Intent.EXTRA_SUBJECT, "主题");
                data.putExtra(Intent.EXTRA_TEXT, "内容");
                startActivity(data);
                break;
            case R.id.iv_mp:
                if (scale) {
                    mIVMP.setAlpha(0f);
                    scale = false;
                } else {
                    mIVMP.setAlpha(1f);
                    scale = true;
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNotify);
    }
}
