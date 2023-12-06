package com.example.testapp;

import static com.yang.androidaar.other.JsonSerializer.CTips;

import android.os.Build;

import com.yang.androidaar.ActivityMgr;
import com.yang.androidaar.Tools;

public class Utils {

    public static void android11Check(Runnable task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            CTips tps = new CTips();
            tps.msg = "暂时不支持 Android 11+";
            Tools.tips02(ActivityMgr.getIns().getActivity(), tps, null);
            return;
        }

        if (task != null) {
            task.run();
        }
    }
}
