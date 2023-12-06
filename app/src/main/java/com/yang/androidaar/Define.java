package com.yang.androidaar;

import android.net.Uri;

import com.yang.androidaar.other.JsonSerializer.CTransfer;


public class Define {

    // --------------- runnable
    public interface BoolRunnable {
        void run(boolean b);
    }

    public interface CodeRunnable {
        void run(@MyCode.ECode int code, String msg);
    }

    public interface JsonRunnable {
        void run(String jsonMsg);
    }

    public interface TransferRunnable {
        void run(@MyCode.ECode int code, CTransfer tf);
    }

    public interface UnityCaller {
        // 只适用于一次调用一次回调的方式
        void callUnityFunc(final String javaFunc, final String jsonMsg);

        // 适用于一次调用可多次回调的方式
        void callUnityPerFunc(final String javaFunc, final String jsonMsg);
    }

    // --------------- enum
    // 日志登等级
    public @interface ELogLevel {
        int Debug = 1;
        int Warn = 2;
        int Error = 3;
        int None = 4;
    }

    // 第三方 sdk
    public @interface EThirdSdk {
        int None = 0;
        int AppsFlyer = 1;
        int GgReferer = 2;
        int Adjust = 3;
    }

    public @interface EApp {
        int Teenpatti = 5;
    }

    public @interface EOs {
        int Android = 2;
    }

    // a b 面标记
    public @interface GameType {
        int A = 0;
        int B = 1;
    }

    // --------------- file
    public final static String File_NonOrganic = "NonOrganic.db"; // referrer 协议信息
    public final static String File_Conversion = "Conversion.db"; // referrer 协议信息
    // --------------- const file name

    public static String getFile_Firebase() {
        return LogUtil.PGFmt("firebaseId.txt");
    }

    public static String getFile_ReportRsp() {
        return LogUtil.PGFmt("reportRsp.db");
    }

    public static String getFile_PackDb() {
        return LogUtil.PGFmt("pack.db");
    }

    public static String getFile_VADb() {
        return LogUtil.PGFmt("va.db");
    }

    public static String getFile_PatchDb() {
        return LogUtil.PGFmt("patch.db");
    }

    public static String getFile_RefererDb() {
        return LogUtil.PGFmt("referer.db");
    }

    public static String getFile_AppsflyerDb() {
        return LogUtil.PGFmt("afInfo.db");
    }


    public static String getFile_AndroidUID() {
        return LogUtil.PGFmt("adr_uid.db");
    }

    public static String getFile_OpenLog() {
        return LogUtil.PGFmt("openlog");
    }

    // --------------- const value
    public static String getUnityApiVersion() {
        return LogUtil.PGFmt("0.0.1");
    }

    public static String getAesKey() {
        return LogUtil.PGFmt("U5RNM4beTo%@QmA");
    }


    public static String getResKey_ProgressBar() {
        return LogUtil.PGFmt("its_loading_pb");
    }

    public static String getResKey_Splash() {
        return LogUtil.PGFmt("its_loading_slash");
    }

    public static String getdDefault_ProgressBar() {
        return LogUtil.PGFmt("pb");
    }

    public static String getdDefault_Splash() {
        return LogUtil.PGFmt("ts_splash");
    }


    // --------------- Native Persist Func

    public static String getNativePersistFunc_ErrReport() {
        return LogUtil.PGFmt("ErrReport");
    }

    public static String getNativePersistFunc_WebviewNotify() {
        return LogUtil.PGFmt("WebviewNotify");
    }

    public static String getNativePersistFunc_Resume() {
        return LogUtil.PGFmt("Resume");
    }

    public static String getNativePersistFunc_Pause() {
        return LogUtil.PGFmt("Pause");
    }


}
