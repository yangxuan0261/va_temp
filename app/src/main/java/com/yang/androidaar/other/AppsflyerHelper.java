package com.yang.androidaar.other;

import android.content.Context;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.yang.androidaar.ActivityMgr;
import com.yang.androidaar.Define;
import com.yang.androidaar.FileTool;
import com.yang.androidaar.JsonTool;
import com.yang.androidaar.LogUtil;
import com.yang.androidaar.MyCode;
import com.yang.androidaar.MyCode.ECode;
import com.yang.androidaar.SystemInfoUtil;
import com.yang.androidaar.Tools;
import com.yang.androidaar.other.JsonSerializer.CEventInfo;
import com.yang.androidaar.other.JsonSerializer.CTransfer;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AppsflyerHelper {

    static AppsflyerHelper instance = null;

    public static AppsflyerHelper getIns() {
        if (instance == null) {
            instance = new AppsflyerHelper();
        }
        return instance;
    }

    private static final String TAG = LogUtil.PGFmt("--- AppsflyerHelper");

    private boolean mIsCall = false;
    private CTransfer mTransfer = new CTransfer(); // 保存数据

    private String mAfUid = null;

    public void init(Context context, boolean isDebug, String devKey, Define.TransferRunnable task) {
        LogUtil.A(devKey != null && devKey.length() > 0, "--- devKey invalid: %s", devKey);

        // af 回调
        initAppsflyer(context, devKey, isDebug, (code, afJson) -> {
            if (code != ECode.Ok) {
                LogUtil.D("--- initAppsflyer fail, code: %d, afJson: %s", code, afJson);
                mTransfer.AfJson = afJson;
                callTask(code, task);
                return;
            }
            dumpInfo(context); // 导出 af 相关信息到文件中

            mTransfer.AfJson = afJson;
            LogUtil.TD(TAG, "--- initAppsflyer ok, AfJson: %s", mTransfer.AfJson);
            callTask(code, task);
        });

        // referer 回调
        initReferer(context, (code, msg) -> {
            if (code != ECode.Ok) {
                return;
            }

            mTransfer.GgJson = Tools.parseUrl2Json(msg);
            LogUtil.TD(TAG, "--- initReferer ok, GgJson: %s", mTransfer.GgJson);
            // callTask(task); 不能 callTask, 要等 onConversionDataSuccess 回调
        });
    }

    private void initAppsflyer(Context context, String devKey, boolean isDebug, Define.CodeRunnable task) {
        AppsFlyerConversionListener conversionListener = new AppsFlyerConversionListener() {

            @Override
            public void onConversionDataSuccess(Map<String, Object> conversionData) {
//                conversionData.put("af_status", "Non-organic"); // 模拟非自然量

                // 转化回调
                String jsonMsg = new JSONObject(conversionData).toString();
                LogUtil.D("--- onConversionDataSuccess: %s", jsonMsg);
                task.run(ECode.Ok, jsonMsg);

                Object val = conversionData.get("af_status");
                if (val != null && val.toString().equals("Non-organic")) {
                    Exception ex = FileTool.writeFileEncrypt(ActivityMgr.getIns().getActivity(), Define.File_Conversion, jsonMsg);
                }
            }

            @Override
            public void onConversionDataFail(String errorMessage) {
                task.run(ECode.LoginError, errorMessage);
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> attributionData) {
                // 深度连接的回调: https://support.appsflyer.com/hc/zh-cn/articles/213766183-%E5%BC%80%E5%8F%91%E8%80%85Unity-Plugin-V4%E5%AF%B9%E6%8E%A5%E6%8C%87%E5%8D%97#%E6%A0%B8%E5%BF%83api
                LogUtil.TD(TAG, "onAppOpenAttribution: ");
            }

            @Override
            public void onAttributionFailure(String errorMessage) {
                LogUtil.TD(TAG, "onAttributionFailure: ");
            }
        };

        AppsFlyerLib.getInstance().init(devKey, conversionListener, context);
        AppsFlyerLib.getInstance().start(context);
        AppsFlyerLib.getInstance().setDebugLog(isDebug);
    }

    private void initReferer(Context context, Define.CodeRunnable task) {
        GoogleReferrerHelper.getIns().start(context, task);
    }

    public void logEvent(Context context, String jsonMsg, Define.CodeRunnable task) {
        CEventInfo ei = JsonSerializer.deserializeCEventInfo(jsonMsg);
        logEvent(context, ei, task);
    }

    public void logEvent(Context context, CEventInfo ei, Define.CodeRunnable task) {
        if (ei == null || ei.name.length() == 0) {
            LogUtil.E("--- eventName is empty");
            return;
        }

        LogUtil.TD(TAG, "--- af logEvent, name: %s", ei.name);
        AppsFlyerLib.getInstance().logEvent(context, ei.name, ei.params);
    }

    public String getInfo() {
        return JsonSerializer.serializeCTransfer(mTransfer);
    }

    private void dumpInfo(Context context) {
        try {
            String relaPath = Define.getFile_AppsflyerDb();
            File file = FileTool.getFile(context, relaPath);
            if (file.exists()) {
                return;
            }

            mAfUid = AppsFlyerLib.getInstance().getAppsFlyerUID(context);

            Map<String, Object> args = new HashMap<>();
            args.put("AfUID", mAfUid);
            args.put("AttrId", AppsFlyerLib.getInstance().getAttributionId(context));
            args.put("OutOfStore", AppsFlyerLib.getInstance().getOutOfStore(context));
            String jsonMsg = new JSONObject(args).toString();
            LogUtil.D("--- write AfUID: %s", mAfUid);
            FileTool.writeFileEncrypt(context, relaPath, jsonMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String GetAfUid(Context context) {
        try {
            if (mAfUid == null) {
                // 从文件中读取
                String txt = FileTool.readFileEncrypt(context, Define.getFile_AppsflyerDb());
                JSONObject jo = new JSONObject(txt);
                mAfUid = jo.optString("AfUID", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mAfUid;
    }

    public String GetAfJson() {
        return mTransfer.AfJson;
    }

    private void callTask(@MyCode.ECode int code, Define.TransferRunnable task) {
        if (mIsCall || task == null) {
            return;
        }

        mIsCall = true;
        task.run(code, mTransfer);
    }
}
