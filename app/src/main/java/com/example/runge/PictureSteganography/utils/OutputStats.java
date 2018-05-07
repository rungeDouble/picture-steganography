package com.example.runge.PictureSteganography.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class OutputStats implements Parcelable {

    private String path;
    private long time;
    private String msg;
    private double SNR,PSNR1,PSNR2;

    public OutputStats(String path, String msg, long time, double SNR, double PSNR1, double PSNR2){
        this.path= path;
        this.msg= msg;
        this.time= time;

        this.SNR = SNR;
        this.PSNR1 = PSNR1;
        this.PSNR2 = PSNR2;
    }

    public String getPath() {
        return path;
    }

    public long getTime() {
        return time;
    }

    public String getMsg() {
        return msg;
    }

    public double getSNR() {
        return SNR;
    }

    public double getPSNR1() {
        return PSNR1;
    }

    public double getPSNR2() {
        return PSNR2;
    }

    protected OutputStats(Parcel in) {
        path = in.readString();
        time = in.readLong();
        msg = in.readString();
        SNR = in.readDouble();
        PSNR1 = in.readDouble();
        PSNR2 = in.readDouble();

    }

    public static final Creator<OutputStats> CREATOR = new Creator<OutputStats>() {
        @Override
        public OutputStats createFromParcel(Parcel in) {
            return new OutputStats(in);
        }

        @Override
        public OutputStats[] newArray(int size) {
            return new OutputStats[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeLong(time);
        dest.writeString(msg);
        dest.writeDouble(SNR);
        dest.writeDouble(PSNR1);
        dest.writeDouble(PSNR2);
    }
}