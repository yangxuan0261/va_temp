package com.yang.androidaar.webview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.yang.androidaar.ActivityMgr;
import com.yang.androidaar.Define;
import com.yang.androidaar.LogUtil;
import com.yang.androidaar.MyCode.ECode;
import com.yang.androidaar.Tools;
import com.yang.androidaar.UnityHelper;
import com.yang.androidaar.other.AppsflyerHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

import org.json.JSONObject;

public class WebviewHelper {
    private static WebviewHelper instance = null;
    private final String TAG = LogUtil.PGFmt("--- WebviewHelper");

    private RelativeLayout mLayoutRoot;
    private BridgeWebView mWebview;
    private WebviewArgs mArgs;

    private static Map<String, BridgeHandler> mJsCb = new HashMap<>();

    public static void showWebview(final Activity activity, final String jsonMsg, final Define.CodeRunnable task) {
        final WebviewArgs args = WebviewArgs.parseJson(jsonMsg);
        if (args == null || (Tools.isEmpty(args.url) && Tools.isEmpty(args.htmlStr))) {
            if (task != null) {
                task.run(ECode.JsonError, "wrong args");
            }
            return;
        }

        closeWebview(activity, () -> {
            instance = new WebviewHelper();
            instance.show(activity, args);
            if (task != null) {
                task.run(ECode.Ok, "");
            }
        });
    }

    public static void closeWebview(final Activity activity, final Runnable task) {
        activity.runOnUiThread(() -> {
            if (instance != null) {
                instance.destroy();
            }

            if (task != null) {
                task.run();
            }
        });
    }

    public static boolean goBack() {
        // 禁止返回
//        if (instance != null) {
//            if (instance.mWebview != null) {
//                boolean b = instance.mWebview.canGoBack();
//                UnityHelper.callUnityPerFuncCode(ECode.WebviewGoback, Define.getNativePersistFunc_WebviewNotify(), "" + b);
//                if (b) {
//                    instance.mWebview.goBack();
//                } else {
//                    instance.destroy();
//                }
//            } else {
//                instance.destroy();
//            }
//            return true;
//        }
        return false;
    }

    public void show(Activity activity, final WebviewArgs args) {
        mArgs = args;
        final RelativeLayout rLayout = new RelativeLayout(activity);
        mLayoutRoot = rLayout;
        RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final BridgeWebView wv = new BridgeWebView(activity);
        mWebview = wv;
        wv.setLayoutParams(lParams);
        rLayout.addView(wv);
        activity.addContentView(rLayout, rParams);
        LogUtil.D("--- args.url: %s", args.url);
        // 加载 url
        if (!Tools.isEmpty(args.url)) {
            wv.loadUrl(args.url);
        } else if (!Tools.isEmpty(args.htmlStr)) {
            wv.loadData(args.htmlStr, args.mimeType, args.encoding);
        }

        // 设置webview
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptCanOpenWindowsAutomatically(args.isJsOpenWinAuto);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(args.cacheMode);//不使用缓存，只从网络获取数据.

        // 屏幕缩放
        webSettings.setSupportZoom(args.isZoom);
        webSettings.setBuiltInZoomControls(args.isBuiltInZoomCtrl);
        WebViewClient wvc = createWVC();
//        WebChromeClient wcc = createWCC();
//        wv.setWebViewClient(wvc);
//        wv.setWebChromeClient(wcc);
//        wv.registerHandler("submitFromWeb", new BridgeHandler() {
//            @Override
//            public void handler(String data, CallBackFunction function) {
//                LogUtil.D("---- get data from JS, data = " + data);
//                function.onCallBack("java callback JS");
//            }
//        });


        for (Map.Entry<String, BridgeHandler> entry : mJsCb.entrySet()) {
            wv.registerHandler(entry.getKey(), entry.getValue());
        }
    }

    public static void regHandler(String name, BridgeHandler bh) {
        mJsCb.put(name, bh);
    }

    public static void clearHandler() {
        mJsCb.clear();
    }

    private WebViewClient createWVC() {
        return new BridgeWebViewClient(mWebview) {

            @Override
            public void onPageFinished(WebView view, String url) {//页面加载完成
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载
                UnityHelper.callUnityPerFuncCode(ECode.WebviewStarted, Define.getNativePersistFunc_WebviewNotify(), url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 返回 true: 用户处理, 不再传递给系统; 返回 false 让系统处理
                boolean isIntercept = false;
                int code = ECode.WebviewOverrideUrl;
                String retUrl = url;

                // 拦截参数
                if (mArgs.intercept != null) {
                    Matcher m = Pattern.compile(mArgs.intercept.pattern).matcher(url);
                    if (m.find()) { // url 匹配中了
                        if (mArgs.intercept.isLaunchUrl) { // 拉起 app
                            try {
                                ActivityMgr.getIns().getActivity().startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(url)), RESULT_OK);
                            } catch (Exception e) {
                                code = ECode.WebviewOverrideError;
                                retUrl = e.getMessage();
                                LogUtil.TE(TAG, LogUtil.PGFmt("--- webview launch app error: %s"), e.getMessage());
                            }
                        }

                        isIntercept = mArgs.intercept.isIntercept; // 匹配中的情况下是否拦截
                    }
                }

                UnityHelper.callUnityPerFuncCode(code, Define.getNativePersistFunc_WebviewNotify(), retUrl);
                return isIntercept ? isIntercept : super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                String tmpUrl = Tools.urlDecode(failingUrl);
                String msg = String.format("onReceivedError, errorCode: %d, description: %s, failingUrl: %s", errorCode, description, tmpUrl);
                UnityHelper.callUnityPerFuncCode(ECode.WebviewReceivedError, Define.getNativePersistFunc_WebviewNotify(), msg);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                UnityHelper.callUnityPerFuncCode(ECode.WebviewReceivedSslError, Define.getNativePersistFunc_WebviewNotify(), error.toString());
            }
        };
    }

    private WebChromeClient createWCC() {
        return new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
                //不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
                AlertDialog.Builder localBuilder = new AlertDialog.Builder(webView.getContext());
                localBuilder.setMessage(message).setPositiveButton("确定", null);
                localBuilder.setCancelable(false);
                localBuilder.create().show();
                result.confirm();
                return true;
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                LogUtil.TD(TAG, String.format("onReceivedTitle, title: %s", title));
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                LogUtil.TD(TAG, String.format("onProgressChanged, newProgress: %d", newProgress));
            }
        };
    }

    private void destroy() {
        if (mWebview != null) {
            mWebview.destroy();
            mWebview = null;
        }

        if (mLayoutRoot != null) {
            ViewGroup vg = (ViewGroup) mLayoutRoot.getParent();
            vg.removeView(mLayoutRoot);
            mLayoutRoot = null;
        }

        instance = null;
        LogUtil.TD(TAG, "destroy WebviewHelper instance");
    }
}
