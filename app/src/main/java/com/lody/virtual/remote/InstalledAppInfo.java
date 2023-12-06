package com.lody.virtual.remote;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Parcel;
import android.os.Parcelable;


import java.io.File;

/**
 * @author Lody
 */
public final class InstalledAppInfo implements Parcelable {
    public String packageName;
    public boolean dynamic;
    public int flag;
    public int appId;
    public String primaryCpuAbi;
    public String secondaryCpuAbi;
    public boolean is64bit;

    public InstalledAppInfo(String packageName, boolean dynamic, int flags, int appId, String primaryCpuAbi, String secondaryCpuAbi, boolean is64bit) {
        this.packageName = packageName;
        this.dynamic = dynamic;
        this.flag = flags;
        this.appId = appId;
        this.primaryCpuAbi = primaryCpuAbi;
        this.secondaryCpuAbi = secondaryCpuAbi;
        this.is64bit = is64bit;
    }





    public String getOatPath() {
       return null;
    }



    public File getOatFile(boolean isExt, String instructionSet) {
        return null;
    }

    public ApplicationInfo getApplicationInfo(int userId) {
      return null;
    }

    public PackageInfo getPackageInfo(int userId) {
        return null;
    }

    public int[] getInstalledUsers() {
        return null;
    }

    public boolean isLaunched(int userId) {
        return false;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.packageName);
        dest.writeByte(this.dynamic ? (byte) 1 : (byte) 0);
        dest.writeInt(this.flag);
        dest.writeInt(this.appId);
        dest.writeString(this.primaryCpuAbi);
        dest.writeString(this.secondaryCpuAbi);
        dest.writeByte(this.is64bit ? (byte) 1 : (byte) 0);
    }

    protected InstalledAppInfo(Parcel in) {
        this.packageName = in.readString();
        this.dynamic = in.readByte() != 0;
        this.flag = in.readInt();
        this.appId = in.readInt();
        this.primaryCpuAbi = in.readString();
        this.secondaryCpuAbi = in.readString();
        this.is64bit = in.readByte() != 0;
    }

    public static final Creator<InstalledAppInfo> CREATOR = new Creator<InstalledAppInfo>() {
        @Override
        public InstalledAppInfo createFromParcel(Parcel source) {
            return new InstalledAppInfo(source);
        }

        @Override
        public InstalledAppInfo[] newArray(int size) {
            return new InstalledAppInfo[size];
        }
    };
}
