package com.songcl.csg.bean;

import android.os.Parcel;

/**
 * Created by songchunlin on 2017/1/22.
 */

public class ConfigBean extends BaseBean {
    private boolean autoMsg;
    private boolean autoNotification;
    private boolean autoOpen;

    public boolean isAutoMsg() {
        return autoMsg;
    }

    public void setAutoMsg(boolean autoMsg) {
        this.autoMsg = autoMsg;
    }

    public boolean isAutoNotification() {
        return autoNotification;
    }

    public void setAutoNotification(boolean autoNotification) {
        this.autoNotification = autoNotification;
    }

    public boolean isAutoOpen() {
        return autoOpen;
    }

    public void setAutoOpen(boolean autoOpen) {
        this.autoOpen = autoOpen;
    }

    public ConfigBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte(this.autoMsg ? (byte) 1 : (byte) 0);
        dest.writeByte(this.autoNotification ? (byte) 1 : (byte) 0);
        dest.writeByte(this.autoOpen ? (byte) 1 : (byte) 0);
    }

    protected ConfigBean(Parcel in) {
        super(in);
        this.autoMsg = in.readByte() != 0;
        this.autoNotification = in.readByte() != 0;
        this.autoOpen = in.readByte() != 0;
    }

    public static final Creator<ConfigBean> CREATOR = new Creator<ConfigBean>() {
        @Override
        public ConfigBean createFromParcel(Parcel source) {
            return new ConfigBean(source);
        }

        @Override
        public ConfigBean[] newArray(int size) {
            return new ConfigBean[size];
        }
    };
}
