package com.yang.androidaar;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class MyProvider extends ContentProvider {
    private static final String RECV_GAME = "RECV_GAME";

    private Context mContext;

    @Override
    public boolean onCreate() {
        mContext = getContext().getApplicationContext();
        //SDK初始化
        LogUtil.D("--- MyProvider.onCreate");
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public void attachInfo(Context context, ProviderInfo info) {
        mContext = context.getApplicationContext();
        super.attachInfo(context, info);
    }

    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        LogUtil.D("--- MyProvider A onReceive method:%s", method);
        try {
            if (RECV_GAME.equals(method)) {
                VATool.onRecvGameMsg(extras.getString("message"));
                return extras;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
