package com.yang.androidaar;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.util.HashMap;
import java.util.Map;

public class ActivityMgr {

    private static ActivityMgr instance = null;

    public static ActivityMgr getIns() {
        if (instance == null) {
            instance = new ActivityMgr();
        }
        return instance;
    }

    // Activity 跳转码定义
    public @interface EActCode {
        int UnknownAppSources = 21001;
        int LocSettings = 21002;
        int PhotoPhoto = 21003;
        int PhotoCamera = 21004;
        int PhotoCrop = 21005;
        int InstallApk = 21006;
        int GoogleSignIn = 21007;
        int GotoAction = 21008;
        int PtmSDKPurchase = 21009;
    }

    // Permission 跳转码定义
    public @interface EPerCode {
        int InstallApk = 31001;
        int PhotoReadImage = 31002;
        int PhotoCamera = 31003;
        int Location = 31004;
        int ReqPerms = 31005;
    }

    public class CActReq {
        public int reqCode;
        public int resCode;
        public Intent data;
        public boolean isJumpOk;
        public ActRunnable task;

        public String toString() {
            return String.format(LogUtil.PGFmt("CActReq, reqCode:%d, resCode:%d\n"), reqCode, resCode);
        }
    }

    public class CPerReq {
        public int reqCode;
        public String[] permissions;
        public int[] grantResults;
        public PerRunnable task;

        public String toString() {
            for (String per : permissions) {
                LogUtil.TD(TAG, "permission:" + per);
            }
            return String.format(LogUtil.PGFmt("CActReq, reqCode:%d, permissions:%s\n"), reqCode, permissions != null ? permissions.toString() : "null");
        }
    }

    public interface ActRunnable {
        void run(CActReq actReq);
    }

    public interface PerRunnable {
        void run(CPerReq perReq);
    }

    private static final String TAG = LogUtil.PGFmt("--- ActivityMgr");
    private Activity mActivity;
    private Map<Integer, CActReq> mActReqMap = new HashMap<>();
    private Map<Integer, CPerReq> mPerReqMap = new HashMap<>();

    private ActivityMgr() {
    }

    public void regActResListener(int requestCode, ActRunnable task) {
//        if (mActReqMap.containsKey(requestCode)) { // 不做限制, 防止直接杀 第三方进程
//            Log.e(TAG, "regActResListener, mActReqMap.containsKey:" + requestCode);
//            return false;
//        }

        if (task != null) {
            CActReq actReq = new CActReq();
            actReq.reqCode = requestCode;
            actReq.task = task;
            mActReqMap.put(requestCode, actReq);
        }
    }

    public ActRunnable unregActResListener(int requestCode) {
        CActReq task = mActReqMap.get(requestCode);
        mActReqMap.remove(requestCode);
        return task == null ? null : task.task;
    }

    // 适配其他 activity
    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    public Activity getActivity() {
        return mActivity;
    }

    // -------------- activity 跳转
    public void startActForResult(Intent intent, int requestCode, ActRunnable task) {
        regActResListener(requestCode, task);

        try {
            mActivity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.TE(TAG, LogUtil.PGFmt("--- startActForResult error: %s"), e.getMessage());
            unregActResListener(requestCode);

            if (task != null) {
                intent.putExtra("myerror", e.getMessage()); // 错误信息塞进去

                CActReq actReq = new CActReq();
                actReq.reqCode = requestCode;
                actReq.isJumpOk = false;
                actReq.data = intent;
                task.run(actReq);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mActReqMap.containsKey(requestCode)) {
            return;
        }

        CActReq actReq = mActReqMap.get(requestCode);
        mActReqMap.remove(requestCode);

        if (actReq.task != null) {
            actReq.resCode = resultCode;
            actReq.isJumpOk = true;
            actReq.data = data;
            actReq.task.run(actReq);
        }
    }

    // -------------- 权限申请
    public void reqPermissions(String[] permissions, int requestCode, PerRunnable task) {
        if (task != null) {
            CPerReq perReq = new CPerReq();
            perReq.reqCode = requestCode;
            perReq.task = task;
            mPerReqMap.put(requestCode, perReq);
        }
        ActivityCompat.requestPermissions(mActivity, permissions, requestCode);
    }

    public void onReqPerResult(int requestCode, String[] permissions, int[] grantResults) {
        if (!mPerReqMap.containsKey(requestCode)) {
            return;
        }

        CPerReq perReq = mPerReqMap.get(requestCode);
        mPerReqMap.remove(requestCode);

        if (perReq.task != null) {
            perReq.permissions = permissions;
            perReq.grantResults = grantResults;
            perReq.task.run(perReq);
        }
    }

}
