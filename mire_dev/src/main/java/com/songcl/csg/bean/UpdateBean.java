package com.songcl.csg.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by songchunlin on 2016/12/31.
 */

public class UpdateBean extends BaseBean {

    /**
     * flag : update
     * redMsg : com.tencent.mm:id/a56
     * redBack : com.tencent.mm:id/gr
     * redOpen : com.tencent.mm:id/be_
     * info : 微信版本
     */

    private String redMsg;
    private String redBack;
    private String redOpen;
    private String info;

    public String getRedMsg() {
        return redMsg;
    }

    public void setRedMsg(String redMsg) {
        this.redMsg = redMsg;
    }

    public String getRedBack() {
        return redBack;
    }

    public void setRedBack(String redBack) {
        this.redBack = redBack;
    }

    public String getRedOpen() {
        return redOpen;
    }

    public void setRedOpen(String redOpen) {
        this.redOpen = redOpen;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.redMsg);
        dest.writeString(this.redBack);
        dest.writeString(this.redOpen);
        dest.writeString(this.info);
    }

    public UpdateBean() {
    }

    protected UpdateBean(Parcel in) {
        this.redMsg = in.readString();
        this.redBack = in.readString();
        this.redOpen = in.readString();
        this.info = in.readString();
    }

    public static final Parcelable.Creator<UpdateBean> CREATOR = new Parcelable.Creator<UpdateBean>() {
        @Override
        public UpdateBean createFromParcel(Parcel source) {
            return new UpdateBean(source);
        }

        @Override
        public UpdateBean[] newArray(int size) {
            return new UpdateBean[size];
        }
    };
}
