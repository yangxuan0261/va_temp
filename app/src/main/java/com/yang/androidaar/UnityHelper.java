package com.yang.androidaar;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UnityHelper {

    private static Define.UnityCaller unityCaller;

    static {
        // 默认回调
        unityCaller = new Define.UnityCaller() {
            @Override
            public void callUnityFunc(String javaFunc, String jsonMsg) {
                LogUtil.D(LogUtil.PGFmt("--- callUnityFunc, javaFunc: %s, jsonMsg: %s"), javaFunc, jsonMsg);
            }

            @Override
            public void callUnityPerFunc(String javaFunc, String jsonMsg) {
                LogUtil.D(LogUtil.PGFmt("--- callUnityPerFunc, javaFunc: %s, jsonMsg: %s"), javaFunc, jsonMsg);
            }
        };
    }

    public static void SetUnityCaller(Define.UnityCaller caller) {
        unityCaller = caller;
    }

    public static String createCodeJson(@MyCode.ECode int errCode, final String msg) {
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("code", errCode);
        retMap.put("msg", msg);
        return new JSONObject(retMap).toString();
    }

    public static String createFuncJson(final String javaFunc, final String msg) {
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("func", javaFunc);
        retMap.put("msg", msg);
        return new JSONObject(retMap).toString();
    }

    public final static void callUnityFuncCode(@MyCode.ECode int errCode, final String javaFunc, final String msg) {
        callUnityFunc(javaFunc, createCodeJson(errCode, msg));
    }

    public final static void callUnityPerFuncCode(@MyCode.ECode int errCode, final String javaFunc, final String msg) {
        callUnityPerFunc(javaFunc, createCodeJson(errCode, msg));
    }

    // 只适用于一次调用一次回调的方式
    public final static void callUnityFunc(final String javaFunc, final String jsonMsg) {
        unityCaller.callUnityFunc(javaFunc, jsonMsg);
    }

    // 适用于一次调用可多次回调的方式
    public final static void callUnityPerFunc(final String javaFunc, final String jsonMsg) {
        unityCaller.callUnityPerFunc(javaFunc, jsonMsg);
    }
}
