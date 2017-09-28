package com.songcl.csg.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by songchunlin on 2017/1/22.
 */

public class BaseBean implements Parcelable {
    private String flag;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.flag);
    }

    public BaseBean() {
    }

    protected BaseBean(Parcel in) {
        this.flag = in.readString();
    }

}
