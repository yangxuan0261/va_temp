package com.yang.androidaar;

import static java.lang.String.format;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.yang.androidaar.MyCode.ECode;
import com.yang.androidaar.other.JsonSerializer;
import com.yang.androidaar.other.JsonSerializer.CTips;
import com.yang.androidaar.other.MyUID;
import com.yang.androidaar.views.SplashHelper;
import com.yang.androidaar.views.SplashInter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Tools {

    private static final String TAG = LogUtil.PGFmt("--- Tools");

    public static Random rdm = new Random(System.currentTimeMillis());

    /**
     * 主线程跑任务
     */
    public static void runOnUiThread(Runnable action) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            action.run();
        } else {
            new Handler(Looper.getMainLooper()).post(action);
        }
    }


    // Android 12+ 不可用
//    public static boolean checkAppInstalled(Context context, String pkgName) {
//        if (pkgName == null || pkgName.isEmpty()) {
//            return false;
//        }
//        PackageInfo packageInfo;
//        try {
//            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
//        } catch (Exception e) {
//            packageInfo = null;
//            e.printStackTrace();
//        }
//        if (packageInfo == null) {
//            return false;
//        } else {
//            return true;//true为安装了，false为未安装
//        }
//    }

    public static String GetStringVaule(Context context, String key) {
        Resources res = context.getResources();
        int id = res.getIdentifier(key, "string", context.getApplicationInfo().packageName);
        return id != 0 ? res.getString(id) : null;
    }

    public static Integer GetIntegerVaule(Context context, String key) {
        Resources res = context.getResources();
        int id = res.getIdentifier(key, "integer", context.getApplicationInfo().packageName);
        return id != 0 ? res.getInteger(id) : null;
    }

    public static Boolean GetBooleanVaule(Context context, String key) {
        Resources res = context.getResources();
        int id = res.getIdentifier(key, "integer", context.getApplicationInfo().packageName);
        return id != 0 ? res.getBoolean(id) : null;
    }

    public static boolean isEmpty(String txt) {
        return txt == null || txt.length() == 0;
    }

    public static String urlDecode(String url) {
        try {
            return URLDecoder.decode(url, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return url;
        }
    }

    /**
     * yes tips
     */
    public static void tips01(Context context, String jsonMsg, final Runnable task) {
//        CTips tps = JsonTool.toObject(jsonMsg, CTips.class);
        CTips tps = JsonSerializer.deserializeCTips(jsonMsg);
        tips02(context, tps, task);
    }

    public static void tips02(Context context, CTips tps, final Runnable task) {
        runOnUiThread(() -> {
            final AlertDialog.Builder normalDialog =
                    new AlertDialog.Builder(context);
            normalDialog.setTitle(tps.title);
            normalDialog.setMessage(tps.msg);
            normalDialog.setPositiveButton(tps.yes,
                    (dialog, which) -> {
                        if (task != null) {
                            task.run();
                        }
                    });
            normalDialog.setCancelable(false);
            normalDialog.show();
        });
    }

    /**
     * yes or no tips
     */
    public static void tips03(Context context, String jsonMsg, final Define.BoolRunnable task) {
//        CTips tps = JsonTool.toObject(jsonMsg, CTips.class);
        CTips tps = JsonSerializer.deserializeCTips(jsonMsg);
        tips03(context, tps, task);
    }

    public static void tips03(Context context, CTips tps, final Define.BoolRunnable task) {
        runOnUiThread(() -> {
            final AlertDialog.Builder normalDialog =
                    new AlertDialog.Builder(context);
            normalDialog.setTitle(tps.title);
            normalDialog.setMessage(tps.msg);
            normalDialog.setPositiveButton(tps.yes,
                    (dialog, which) -> {
                        if (task != null) {
                            task.run(true);
                        }
                    });
            normalDialog.setNegativeButton(tps.no,
                    (dialog, which) -> {
                        if (task != null) {
                            task.run(false);
                        }
                    });
            normalDialog.setCancelable(false);
            normalDialog.show();
        });
    }

    /**
     * 复制到剪贴板
     */
    public static void CopyToClipboard(final Activity activity, final String text) {
        runOnUiThread(() -> {
            ClipboardManager clipboardManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("playerId", text);
            clipboardManager.setPrimaryClip(clipData);
        });
    }

    /**
     * 获取剪切板上的内容
     */
    public static String getFromClipboard(Context context) {
        try {
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData data = cm.getPrimaryClip();
            if (data != null && data.getItemCount() > 0) {
                ClipData.Item item = data.getItemAt(0);
                if (item != null) {
                    CharSequence sequence = item.coerceToText(context);
                    if (sequence != null) {
                        return sequence.toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }

    // 跳转到 位置信息, 开启高进度 gps
    public static void openLocSettions(Define.CodeRunnable cb) {
        ActivityMgr.ActRunnable task = actReq -> {
            if (cb != null) {
                cb.run(ECode.Ok, "");
            }
        };
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        ActivityMgr.getIns().startActForResult(intent, ActivityMgr.EActCode.LocSettings, task);
    }

    // unity 的 systeminfo.deviceuniqueidentifier 接口就是这样实现的
    public static String getDeviceId(Context context) {
        return MyUID.get(context);
    }

//    // >= 安卓 11 该接口 intent.resolveActivity 返回 null
//    public static boolean isIntentExist(Context context, Intent intent) {
//        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R || intent.resolveActivity(context.getPackageManager()) != null;
//    }

    public static void openUrl(Context context, String url, final Define.BoolRunnable callback) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        ActivityMgr.ActRunnable task = actReq -> {
            if (callback != null) {
                callback.run(actReq.isJumpOk);
            }
        };
        ActivityMgr.getIns().startActForResult(intent, ActivityMgr.EActCode.GotoAction, task);
    }


    /**
     * 跳转 相关页面
     */
    public static void gotoAction(Context context, final String jsonMsg, final Define.BoolRunnable callback) {
        String action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS;
        String uri = "";
        String type = "";
        try {
            JSONObject jsonObject = new JSONObject(jsonMsg);
            action = jsonObject.getString("action");
            uri = jsonObject.getString("uri");
            type = jsonObject.getString("type");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(action);
        if (uri != null && uri.length() > 0) {
            intent.setData(Uri.parse(uri));
        }
        if (type != null && type.length() > 0) {
            intent.setType(type);
        }

        ActivityMgr.ActRunnable task = actReq -> {
            LogUtil.TD(TAG, "gotoAction result: " + actReq.toString());
            if (callback != null) {
                callback.run(actReq.isJumpOk);
            }
        };
        ActivityMgr.getIns().startActForResult(intent, ActivityMgr.EActCode.GotoAction, task);
    }

    /**
     * toast 显示, 会跑在 ui 线程
     */
    public static void toast(final Activity activity, final String jsonMsg) {
        String msg = LogUtil.PGFmt("hello");
        boolean isShort = true;
        try {
            JSONObject jsonObject = new JSONObject(jsonMsg);
            msg = jsonObject.getString("msg");
            isShort = jsonObject.getBoolean("isShort");
        } catch (Exception e) {
            e.printStackTrace();
        }

        final int duration = isShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG;
        final String content = msg;
        activity.runOnUiThread(() -> Toast.makeText(activity, content, duration).show());
    }

    /**
     * 检测是否授权
     */
    public static boolean isPermOk(final Context context, final String permission) {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 请求权限
     */
    public static void reqPermissions(final Context context, final String jsonPerms, final Define.JsonRunnable task) {
        List<String> reqPermLst = new ArrayList<>();
        try {
            JSONArray ja2 = new JSONArray(jsonPerms);
            for (int i = 0; i < ja2.length(); i++) {
                reqPermLst.add(ja2.getString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (task != null) {
                task.run(format("--- json decode err:%s", e.getMessage()));
            }
            return;
        }

        if (reqPermLst.size() == 0) {
            if (task != null) {
                task.run(format("--- no perms req"));
            }
            return;
        }

        ActivityMgr.PerRunnable perRunnable = perReq -> {
            if (task != null) {
                try {
                    JSONArray permJa = new JSONArray(perReq.permissions);
                    JSONArray resJa = new JSONArray(perReq.grantResults);
                    JSONObject jo = new JSONObject();
                    jo.put("permissions", permJa);
                    jo.put("grantResults", resJa);
                    task.run(jo.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    task.run(format("--- json encode err:%s", e.getMessage()));
                }
            }
        };
        ActivityMgr.getIns().reqPermissions(reqPermLst.toArray(new String[0]), ActivityMgr.EPerCode.ReqPerms, perRunnable);
    }


//    public static <T> T parseUrl2Object(String url, Class<T> classOfT) {
//        String jstr = parseUrl2Json(url);
//        return jstr == null ? null : JsonTool.toObject(jstr, classOfT);
//    }

    public static String parseUrl2Json(String url) {
        Map<String, Object> params = parseUrl2Map(url);
        return params == null ? null : new JSONObject(params).toString();
    }

    public static Map<String, Object> parseUrl2Map(String url) {
        try {
            url = URLDecoder.decode(url, StandardCharsets.UTF_8.name());
            String[] partArr = url.split("[?]");
            if (partArr.length == 1) { // 如果没有路径前缀, 拼一个自定义前缀, 因为 url 解析需要这个前缀
                url = LogUtil.PGFmt("https://www.bbb.com/ccc?") + url;
            }

            Uri uri = Uri.parse(url);
            Map<String, Object> params = new HashMap<>();
            Set<String> names = uri.getQueryParameterNames();
            for (String name : names) {
                params.put(name, uri.getQueryParameter(name));
            }
            return params;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int randomRange(int start, int end) {
        return start + rdm.nextInt(end - start);
    }

    public static void RestartApplication(Activity activity, int timeout) {
        try {
            Intent restartIntent = activity.getPackageManager().getLaunchIntentForPackage(activity.getPackageName());
            PendingIntent intent = PendingIntent.getActivity(activity, 0, restartIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager manager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
            manager.set(AlarmManager.RTC, System.currentTimeMillis() + timeout, intent);
            activity.finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Google 服务检测
     */
    public static boolean checkIsSupportGoogle() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(ActivityMgr.getIns().getActivity());
        return resultCode == ConnectionResult.SUCCESS;
    }

    // ------------------ convert variable
    public static int ConvStr2Int(String str, int fallback) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            e.printStackTrace();
            return fallback;
        }
    }

    public static String ExceptionStack(Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public static SplashInter GetSplashIns() {
        return SplashHelper.getIns();

//        File file = FileTool.getFile(ActivityMgr.instance.getActivity(), "show_loading.db");
//        return file.exists() ? SplashHelper.getIns() : SplashNoBarHelper.getIns();
    }

    public static void SetLandscape(Activity activity, boolean isLandscape) {
        activity.setRequestedOrientation(isLandscape ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}