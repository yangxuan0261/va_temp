package com.yang.androidaar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {
    private static final String RECV_GAME = "RECV_GAME";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtil.D("--- MyReceiver A onReceive action:%s", action);

        try {
            if (RECV_GAME.equals(action)) {
                VATool.onRecvGameMsg(intent.getStringExtra("message"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
