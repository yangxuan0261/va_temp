package com.yang.androidaar;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.icu.util.TimeZone;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;

import com.yang.androidaar.Define.EThirdSdk;
import com.yang.androidaar.other.JsonSerializer;
import com.yang.androidaar.other.JsonSerializer.CPhoneInfo;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 系统工具类 - https://blog.csdn.net/zhuwentao2150/article/details/51946387
 */
public class SystemInfoUtil {

    private static String TAG = LogUtil.PGFmt("--- SystemInfoUtil");

    public static int ThirdSdk = EThirdSdk.None;

    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public static String getSystemCountry() {
        return Locale.getDefault().getCountry();
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return 语言列表
     */
    public static Locale[] getSystemLanguageList() {
        return Locale.getAvailableLocales();
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static int getSystemSdk() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取支持的 cpu 架构
     */
    public static String getAbis() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return TextUtils.join(",", Arrays.asList(Build.SUPPORTED_ABIS));
        } else {
            return String.format("%s,%s", Build.CPU_ABI, Build.CPU_ABI2);
        }
    }

    /**
     * 获取运营商
     */
    public static String getNetworkOperatorName() {
        try {
            TelephonyManager tm = (TelephonyManager) ActivityMgr.getIns().getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            String operator = tm.getSimOperator();

            // No sim
            if (TextUtils.isEmpty(operator)) {
                return "";
            }
//        if ("46001".equals(operator) || "46006".equals(operator) || "46009".equals(operator)) {
//            opeType = "中国联通";
//        } else if ("46000".equals(operator) || "46002".equals(operator) || "46004".equals(operator) || "46007".equals(operator)) {
//            opeType = "中国移动";
//
//        } else if ("46003".equals(operator) || "46005".equals(operator) || "46011".equals(operator)) {
//            opeType = "中国电信";
//        } else {
//            opeType = "";
//        }
            return operator;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取时区 时间
     */
    public static String getTimeZone() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT);
        } else {
            return "";
        }
    }

    /**
     * 获取时区 id
     */
    public static String getTimeId() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return TimeZone.getDefault().getID();
        } else {
            return "";
        }
    }

    public static String getPackgeName(Context ctx) {
        return ctx.getPackageName();
    }


    public static String getSchemeInfo(Context ctx) {
        Map<String, Object> m1 = new HashMap<>();
        m1.put("scheme_name", Tools.GetStringVaule(ctx, "scheme_name"));
        m1.put("scheme_host", Tools.GetStringVaule(ctx, "scheme_host"));
        m1.put("scheme_path", Tools.GetStringVaule(ctx, "scheme_path"));
        return new JSONObject(m1).toString();
    }

    // versionCode 应用内版本号
    public static int getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        String versionCode = "";
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // versionName 显示给用户看的版本号
    public static String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        String versionName = "";
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static CPhoneInfo getSysInfo(Context context) {
        CPhoneInfo pi = new CPhoneInfo();
        pi.SystemLanguage = getSystemLanguage();
        pi.SystemVersion = getSystemVersion();
        pi.SystemSdk = getSystemSdk();
        pi.SystemModel = getSystemModel();
        pi.DeviceBrand = getDeviceBrand();
        pi.PackgeName = getPackgeName(context);
        pi.DeviceID = Tools.getDeviceId(context);
        pi.ThirdSdk = ThirdSdk;
        pi.ApiVersion = Define.getUnityApiVersion();

        pi.SystemCountry = getSystemCountry();
        pi.SystemAbis = getAbis();
        pi.NetworkOperator = getNetworkOperatorName();
        pi.TimeZone = getTimeZone();
        pi.TimeId = getTimeId();

        // api 更新
        pi.IsWebviewIntercept = true;
        pi.IsCancelIntentCheck = true;
        pi.IsFeature = true;
//        pi.FeatureFlag01 = Feature.getFeature01();
        pi.SchemeInfo = getSchemeInfo(context);
        pi.VersionCode = getVersionCode(context);
        pi.VersionName = getVersionName(context);
        return pi;
    }

//    /**
//     * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
//     *
//     * @return 手机IMEI
//     */
//    public static String getIMEI(Context ctx) {
//        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
//        if (tm != null) {
//            return tm.getDeviceId();
//        }
//        return null;
//    }
}