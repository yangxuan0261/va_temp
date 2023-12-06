package com.yang.androidaar.webview;

import android.webkit.WebSettings;

import org.json.JSONObject;

public class WebviewArgs {

    // 拦截参数
    public static class InterceptArgs {
        public String pattern;
        public boolean isIntercept; // true/false or null:交由系统负责
        public boolean isLaunchUrl;
    }

    public InterceptArgs intercept;

    public boolean isJsEnabled;
    public boolean isJsOpenWinAuto;
    public int cacheMode;
    public boolean isZoom;
    public boolean isBuiltInZoomCtrl;

    public String url;

    public String htmlStr;
    public String mimeType;
    public String encoding;

    public static WebviewArgs parseJson(final String jsonMsg) {
        try {
            WebviewArgs args = new WebviewArgs();
            JSONObject jo = new JSONObject(jsonMsg);
            args.isJsEnabled = jo.optBoolean("isJsEnabled", true);
            args.isJsOpenWinAuto = jo.optBoolean("isJsOpenWinAuto", true);
            args.cacheMode = jo.optInt("cacheMode", WebSettings.LOAD_NO_CACHE);
            args.isZoom = jo.optBoolean("isZoom", true);
            args.isBuiltInZoomCtrl = jo.optBoolean("isBuiltInZoomCtrl", true);

            args.url = jo.optString("url", "");

            args.htmlStr = jo.optString("htmlStr", "");
            args.mimeType = jo.optString("mimeType", "text/html");
            args.encoding = jo.optString("encoding", "UTF-8");

            JSONObject interceptJo = jo.optJSONObject("intercept");
            if (interceptJo != null) {
                InterceptArgs intercept = new InterceptArgs();
                args.intercept = intercept;

                intercept.pattern = interceptJo.optString("pattern", null);
                intercept.isIntercept = interceptJo.optBoolean("isIntercept", true);
                intercept.isLaunchUrl = interceptJo.optBoolean("isLaunchUrl", true);
            }
            return args;
        } catch (Exception e) {
            return null;
        }
    }
}
