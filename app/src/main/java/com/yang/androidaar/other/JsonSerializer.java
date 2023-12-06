package com.yang.androidaar.other;


import com.yang.androidaar.JsonTool;
import com.yang.androidaar.LogUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

// json 转换助手, model 可以混淆, 减少被 gp 识别的风险
public class JsonSerializer {

    // --------------- model 定义
    public static class CTips {
        public String title = LogUtil.PGFmt("Tips");
        public String msg = LogUtil.PGFmt("null");
        public String yes = LogUtil.PGFmt("yes");
        public String no = LogUtil.PGFmt("no");
    }

    // 第三方事件数据结构
    public static class CEventInfo {
        public String name = "";
        public Map<String, Object> params = new HashMap<>();
        public float valueToSum = 0.0f;
    }

    // 透传数据
    public static class CTransfer {
        public String AfJson;
        public String GgJson;
        public String AdJson;
        public String ExtA;
    }

    public static class CPackDB {
        public int PlatId;
        public boolean DBG;
        public String ReportUrl;
        public String UrlB;
        public String Version;
        public int ThirdId;
        public int PkgId;
        public int LogLevel;
        public String ResVersion;
    }

    public static class CPhoneInfo {
        public String SystemLanguage;
        public String SystemVersion;
        public int SystemSdk;
        public String SystemModel;
        public String DeviceBrand;
        public String PackgeName;
        public String DeviceID;
        public int ThirdSdk;
        public String ApiVersion;

        public String SystemCountry;
        public String SystemAbis;
        public String NetworkOperator;
        public String TimeZone;
        public String TimeId;

        // api 更新标记
        public boolean IsWebviewIntercept; // 支持 webview 拦截并拉起 app
        public boolean IsCancelIntentCheck; // 取消 intent 检查
        public boolean IsFeature; // 支持 位存储标记
        public int FeatureFlag01;

        public String SchemeInfo;
        public int VersionCode;
        public String VersionName;

    }


    // --------------- model 转换

    public static CTips deserializeCTips(String json) {
        try {
            JSONObject jo = new JSONObject(json);
            CTips ins = new CTips();
            ins.title = jo.optString("title", "Tips");
            ins.msg = jo.optString("msg", "null");
            ins.yes = jo.optString("yes", "yes");
            ins.no = jo.optString("no", "no");
            return ins;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new CTips();
    }


    public static CEventInfo deserializeCEventInfo(String json) {
        try {
            JSONObject jo = new JSONObject(json);
            CEventInfo ins = new CEventInfo();
            ins.name = jo.optString("name", "");
            ins.valueToSum = (float) jo.optDouble("valueToSum", 0.0f);
            ins.params = JsonTool.toMap(jo.optJSONObject("params"));
            return ins;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new CEventInfo();
    }

    public static String serializeCTransfer(CTransfer ins) {
        if (ins == null) return new JSONObject().toString();

        try {
            JSONObject jo = new JSONObject();
            jo.put("AfJson", ins.AfJson);
            jo.put("GgJson", ins.GgJson);
            jo.put("AdJson", ins.AdJson);
            jo.put("ExtA", ins.ExtA);
            return jo.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject().toString();
        }
    }
}
