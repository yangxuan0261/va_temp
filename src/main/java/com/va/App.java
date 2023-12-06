package com.va;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import io.busniess.va.common.CommonApp;

/**
 * @author Lody
 */
public class App extends Application {
    private static App gApp;

    public static App getApp() {
        return gApp;
    }

    CommonApp commonApp = new CommonApp();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        gApp = this;
        commonApp.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        commonApp.onCreate(this);
        try {
            if(isMainProcess()){
                Log.e("11111","isMainProcess....true");
            }else{
                Log.e("11111","isMainProcess....false");
            }
        }catch (Throwable e){
            e.printStackTrace();
        }
    }


    private boolean isMainProcess() {
        String mainProcessName = getApplicationInfo().processName;
        String processName = getProcessName(this);
        Log.e("11111","processName:"+processName);
        if(TextUtils.isEmpty(processName)){
            return false;
        }
        if (processName.equals(mainProcessName)) {
            return true;
        }
        return false;
    }


    private static String getProcessName(Context context) {
        int pid = Process.myPid();
        String processName = null;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
            if (info.pid == pid) {
                processName = info.processName;
                break;
            }
        }
        return processName;
    }
}

