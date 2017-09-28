package com.songcl.csg;

import android.accessibilityservice.AccessibilityService;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by songchunlin on 16/8/9.
 */
public class MyRedAccessibilityService extends AccessibilityService {
    private boolean toList = false;
    private boolean toHome = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Log.e("toHome", "OK");
                MyRedAccessibilityService.this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                toHome = false;
                handler.removeMessages(0);
            }
            if (msg.what == 1) {
//                getPacket();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        int eventType = accessibilityEvent.getEventType();
        Log.e("className", accessibilityEvent.getClassName().toString());
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                if ((boolean) SharedPreferencePUtils.get(getApplicationContext(), "cb_auto_notification", false)) {
                    List<CharSequence> texts = accessibilityEvent.getText();
                    if (!texts.isEmpty()) {
                        for (CharSequence text : texts) {
                            String content = text.toString();
                            if (content.contains("[微信红包]")) {
                                wakeUpAndUnlock(MyRedAccessibilityService.this);
                                MediaPlayer mediaPlayer = MediaPlayer.create(MyRedAccessibilityService.this, R.raw.tune);
                                mediaPlayer.start();
                                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        mediaPlayer.release();
                                    }
                                });
                                //模拟打开通知栏消息
                                if (accessibilityEvent.getParcelableData() != null &&
                                        accessibilityEvent.getParcelableData() instanceof Notification) {
                                    Notification notification = (Notification) accessibilityEvent.getParcelableData();
                                    PendingIntent pendingIntent = notification.contentIntent;
                                    try {
                                        pendingIntent.send();
                                    } catch (PendingIntent.CanceledException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String className = accessibilityEvent.getClassName().toString();
                if (className.equals("com.tencent.mm.ui.LauncherUI")) {
//                    handler.sendEmptyMessage(1);
                    //开始抢红包
                    if ((boolean) SharedPreferencePUtils.get(getApplicationContext(), "auto", false)) {
                        if (toList) {
                            Log.e("toList", "OK");
                            MyRedAccessibilityService.this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                            toList = false;
                            toHome = true;
                        } else {
                            getPacket();
                        }
                        if (toHome) {
                            handler.sendEmptyMessageDelayed(0, 500);
                        }
                    }
                } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.En_fba4b94f")) {
                    handler.removeMessages(1);
                    //开始打开红包
                    openPacket();
                } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")) {
                    handler.removeMessages(1);
                    if ((boolean) SharedPreferencePUtils.get(getApplicationContext(), "auto", false)) {
                        String redBack = (String) SharedPreferencePUtils.get(this, "redBack", "com.tencent.mm:id/gv");//查找返回键
                        List<AccessibilityNodeInfo> list = getRootInActiveWindow().findAccessibilityNodeInfosByViewId(redBack);//com.tencent.mm:id/gp
                        if (list.size() > 0) {
                            if (list.get(list.size() - 1).isClickable()) {
                                Log.e("Details", "OK，Back");
                                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                toList = true;
                            } else {
                                Log.e("Details", "Back");
                                this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                                toList = true;
                            }
                        }
                    }
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                Log.e("TYPE", "TYPE_WINDOW_CONTENT_CHANGED");
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                Log.e("TYPE", "TYPE_VIEW_SCROLLED");
                break;
        }
    }

    private void openPacket() {
        if ((boolean) SharedPreferencePUtils.get(this, "cb_auto_open", false)) {
            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            if (nodeInfo != null) {
                String redOpen = (String) SharedPreferencePUtils.get(this, "redOpen", "com.tencent.mm:id/bi3");//查找开红包按钮
                List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(redOpen);//com.tencent.mm:id/bdh
                if (list.size() == 0) {
                    this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    toList = true;
                } else {
                    for (AccessibilityNodeInfo n : list) {
                        n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    Log.e("Open", "Open");
                }
            }
        }

    }

    private void getPacket() {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
//        if (rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/f_") != null &&
//                rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/f_").size() > 0) {
//            List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByText("[微信红包]");
//            Log.e("FindList", "size=" + list.size());
//            for (AccessibilityNodeInfo n : list) {
//                if (list.size() > 0) {
//                    list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                    recycle(rootNode);
//                }
//            }
//        } else {
        recycle(rootNode);
//        }
    }

    public void recycle(AccessibilityNodeInfo info) {
        String redMsg = (String) SharedPreferencePUtils.get(this, "redMsg", "com.tencent.mm:id/a5u");//查找红包消息
        List<AccessibilityNodeInfo> list = info.findAccessibilityNodeInfosByViewId(redMsg);
        Log.e("Find", "size=" + list.size());
        for (AccessibilityNodeInfo n : list) {
            if (list.size() > 0) {
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    @Override
    public void onInterrupt() {
        Log.e("className", "onInterrupt");
    }
    public static void wakeUpAndUnlock(Context context){
        KeyguardManager km= (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁
        kl.disableKeyguard();
        //获取电源管理器对象
        PowerManager pm=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,"bright");
        //点亮屏幕
        wl.acquire();
        //释放
        wl.release();
    }
}
