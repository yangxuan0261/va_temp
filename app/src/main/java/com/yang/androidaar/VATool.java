package com.yang.androidaar;

import android.content.Context;

import com.yang.androidaar.other.AppsflyerHelper;
import com.yang.androidaar.webview.WebviewHelper;

import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class VATool {
    private static String _bPkgName;

    // ------------------------ 工具
    public static String getBDataDirInA(Context context) {
//        return String.format("%s/virtual/data/user/0/%s", context.getDataDir().getAbsolutePath(), _bPkgName);
        return String.format("%s/Android_va/0/Android/data/%s", context.getExternalFilesDir("").getAbsolutePath(), _bPkgName);
    }

    public static String getBFilesDirInA(Context context) {
        return String.format("%s/files", getBDataDirInA(context));
    }

    public static String getBFilesDirInA(Context context, String filePath) {
        return String.format("%s/files/%s", getBDataDirInA(context), filePath);
    }

    public Exception writeFile(Context context, String fileName, String content) {
        try {
            String dstPath = String.format("%s/%s", getBFilesDirInA(context), fileName);
            File dstFile = new File(dstPath);
            return FileTool.writeFile(dstFile, content.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            return e;
        }
    }

    public static void OpenUrl(String url) {
        final Map<String, Object> m1 = new HashMap<>();
        m1.put(LogUtil.PGFmt("url"), url);
        Tools.runOnUiThread(() -> {
            WebviewHelper.showWebview(ActivityMgr.getIns().getActivity(), new JSONObject(m1).toString(), null);
        });
    }

    // ------------------------ 业务
    private @interface EType {
        int None = 0;
        int AfEvent = 1;

    }

    private static class CMsg {
        public int type;
        public String msg;

        public static CMsg Json2Obj(String json) {
            try {
                JSONObject jo = new JSONObject(json);
                CMsg ins = new CMsg();
                ins.type = jo.optInt("type", EType.None);
                ins.msg = jo.optString("msg", null);
                return ins;
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }

    // ------------------------ 业务
    public static void setBPkgName(String bPkgName) {
        _bPkgName = bPkgName;
    }

    public static void onRecvGameMsg(String jsonMsg) {
        CMsg ins = CMsg.Json2Obj(jsonMsg);
        if (ins == null) {
            LogUtil.D("--- recv unknown msg1: %s", jsonMsg);
            return;
        }

        if (ins.type == EType.AfEvent) {
            MsgAfEvent(ins.msg);
        } else {
            LogUtil.D("--- recv unknown msg2: %s", jsonMsg);
        }
    }

    private static void MsgAfEvent(String jsonMsg) {
        AppsflyerHelper.getIns().logEvent(ActivityMgr.getIns().getActivity(), jsonMsg, null);
    }
}
