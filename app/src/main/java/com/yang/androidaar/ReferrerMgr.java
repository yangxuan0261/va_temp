//package com.yang.androidaar;
//
//import static android.app.Activity.RESULT_OK;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.text.TextUtils;
//
//import com.github.lzyzsd.jsbridge.BridgeHandler;
//import com.github.lzyzsd.jsbridge.CallBackFunction;
//import com.yang.androidaar.Define.GameType;
//import com.yang.androidaar.action.ActionGoogle;
//import com.yang.androidaar.other.AppsflyerHelper;
//import com.yang.androidaar.other.JsonSerializer.CPackDB;
//import com.yang.androidaar.other.JsonSerializer.CPhoneInfo;
//import com.yang.androidaar.timer.TimerMgr;
//import com.yang.androidaar.tool.HttpHelper;
//import com.yang.androidaar.tool.HttpHelper.SHttp;
//import com.yang.androidaar.webview.WebviewHelper;
//
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Consumer;
//
//public class ReferrerMgr {
//    public @interface ECashStatus {
//        int Open = 0;
//        int Condition = 1;
//        int Close = 2;
//    }
//
//    public @interface ReferrerStatus {
//        int NoCheck = 0;
//        int NonOrganic = 1;
//        int Organic = 2;
//    }
//
//    private static ReferrerMgr instance = null;
//
//    private CPhoneInfo phoneInfo;
//    private CPackDB packDB;
//
//    private final String FlagConvFile = LogUtil.PGFmt("Conversion.db");
//    private final String FlagLogFile = LogUtil.PGFmt("openlog");
//    private final String ResVer = LogUtil.PGFmt("v1001");
//    private final int TryConvMaxCnt = 5;
//    private final boolean IsDev = true;  // 测试开关
//
//
//    public static ReferrerMgr getIns() {
//        if (instance == null) {
//            instance = new ReferrerMgr();
//        }
//        return instance;
//    }
//
//    public void Report(final Consumer<Integer> task) {
//        // 开始走归因逻辑
//        Tools.runOnUiThread(() -> {
//            phoneInfo = SystemInfoUtil.getSysInfo(ActivityMgr.getIns().getActivity());
//            packDB = MiscFuncApi.packDB;
//
//            // 打开日志
//            if (IsDev || FileTool.isFileExists(ActivityMgr.getIns().getActivity(), FlagLogFile)) {
//                LogUtil.SetLv(Define.ELogLevel.Debug);
//            } else {
//                LogUtil.SetLv(Define.ELogLevel.Error);
//            }
//
//            new Thread(() -> InnerReport(task)).start();
//        });
//    }
//
//    private void InnerReport(final Consumer<Integer> task) {
//        final Define.CodeRunnable cbWrap = (code, msg) -> {
//            this.clear();
//            LogUtil.D("--- Report result, code: %d, msg: %s", code, msg);
//
//            if (code == GameType.B) {
////                // 拼接参数
////                Map<String, Object> m1 = new HashMap<>();
////                m1.put("isWebview", true);
////                m1.put("deviceId", phoneInfo.DeviceID);
////                m1.put("afUid", AppsflyerHelper.getIns().GetAfUid(ActivityMgr.getIns().getActivity()));
////                m1.put("pkgName", phoneInfo.PackgeName);
////                m1.put("platId", packDB.PlatId);
////                m1.put("debug", packDB.DBG);
////                m1.put("os", Define.EOs.Android);
////                m1.put("appId", Define.EApp.Teenpatti);
////                m1.put("apkVersion", packDB.Version);
////                m1.put("thirdId", packDB.ThirdId);
////                m1.put("pkgId", packDB.PkgId);
////                m1.put("hotfixUrl", GetUrlByRoute(packDB.ReportUrl, "hotupdate"));
////                m1.put("logLevel", LogUtil.GetLv());
////                m1.put("resVersion", packDB.ResVersion);
////                List<String> argArr = new ArrayList<>();
////                for (Map.Entry<String, Object> entry : m1.entrySet()) {
////                    argArr.add(String.format("%s=%s", entry.getKey(), entry.getValue()));
////                }
////                final String finalUrl = String.format("%s?%s", packDB.UrlB, TextUtils.join("&", argArr));
////                LogUtil.D("--- finalUrl: %s", finalUrl);
////                OpenUrl(finalUrl);
//            }
//
//            Tools.runOnUiThread(() -> {
//                if (task != null)
//                    task.accept(code);
//            });
//        };
//
//        // 第一步激活及拿到 af key, 已经归因成功不需要再上报
//        String afkey = "";
//        if (!FileTool.isFileExists(ActivityMgr.getIns().getActivity(), FlagConvFile)) {
//            afkey = GetAfKey();
//            LogUtil.A(afkey != null && afkey.length() > 0, "--- afkey invalid: %s", afkey);
//        }
//
//        // 热更开关不开真金
//        if (!IsHotFixCashOn()) {
//            cbWrap.run(GameType.A, "HotFixCash off");
//            return;
//        }
//
//        // 已经归因成功的不需要再额外判定
//        if (FileTool.isFileExists(ActivityMgr.getIns().getActivity(), FlagConvFile)) {
//            cbWrap.run(GameType.B, "IsNonOrganic");
//            return;
//        }
//
//        LogUtil.D("--- af key: %s", afkey);
//        AppsflyerHelper.getIns().init(ActivityMgr.getIns().getActivity(), IsDev, afkey, (code, transfer) -> {
//            LogUtil.A(code == MyCode.ECode.Ok, "--- init AppsflyerHelper fail");
//
//            int flagGame = TryReferrer(transfer.AfJson, transfer.GgJson);
//            LogUtil.D("--- referrer flagGame: %d", flagGame);
//
//            // 归因不成功, 尝试轮询服务器
//            if (flagGame == GameType.A) {
//                TimerMgr.getIns().setTimeout(5 * 1000, () -> {
//                    TryUpdateConversion(TryConvMaxCnt, cbWrap);
//                });
//            } else {
//                cbWrap.run(GameType.B, "referrer success");
//            }
//        });
//    }
//
//    // 获取真金开关
//    public boolean IsHotFixCashOn() {
//        try {
//            Map<String, Object> m1 = new HashMap<>();
//            m1.put("Plat", packDB.PlatId);
//            m1.put("Os", Define.EOs.Android);
//            m1.put("Appid", Define.EApp.Teenpatti);
//            m1.put("Version", "0.0.0.1");
//            m1.put("Deviceid", phoneInfo.DeviceID);
//            m1.put("ThirdId", "0");
//            m1.put("PkgName", phoneInfo.PackgeName);
//            m1.put("ResVer", ResVer);
//            m1.put("ThirdUid", "0000-0000");
//            m1.put("PackageID", 0);
//
//            String url = GetUrlByRoute(packDB.ReportUrl, "hotupdate");
//            String json = new JSONObject(m1).toString();
//            LogUtil.D("--- hotfix url: %s, json: %s", url, json);
//
//            SHttp rsp = HttpHelper.okhttpPostSync(url, json);
//            LogUtil.D("--- hotfix code: %d, msg: %s", rsp.code, rsp.msg);
//
//            LogUtil.A(rsp.code == 200, "--- hotfix req fail, code: %d, msg: %s", rsp.code, rsp.msg);
//
//            JSONObject jo = new JSONObject(rsp.msg);
//
//            // 用热更下发的版本号
//            packDB.Version = jo.optString("Version", "0.0.0.1");
//
//            int flagCash = jo.optInt("CashOnOff", 0);
//            return flagCash == ECashStatus.Open;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    // 归因判断
//    public int TryReferrer(String Afdata, String Ggdata) {
//        try {
//            LogUtil.D("--- TryReferrer");
//            Map<String, Object> m1 = new HashMap<>();
//            m1.put("Plat", packDB.PlatId);
//            m1.put("Os", Define.EOs.Android);
//            m1.put("Appid", Define.EApp.Teenpatti);
//            m1.put("Deviceid", phoneInfo.DeviceID);
//            m1.put("PkgName", phoneInfo.PackgeName);
//            m1.put("ResVer", ResVer);
//
//            m1.put("AppsflyerId", AppsflyerHelper.getIns().GetAfUid(ActivityMgr.getIns().getActivity()));
//            m1.put("Ggdata", Ggdata);
//            m1.put("Afdata", Afdata);
//
//            String url = GetUrlByRoute(packDB.ReportUrl, "referrer");
//            String json = new JSONObject(m1).toString();
//            LogUtil.D("--- referrer url: %s, json: %s", url, json);
//
//            SHttp rsp = HttpHelper.okhttpPostSync(url, json);
//            LogUtil.D("--- referrer code: %d, msg: %s", rsp.code, rsp.msg);
//
//            LogUtil.A(rsp.code == 200, "--- referrer req fail, code: %d, msg: %s", rsp.code, rsp.msg);
//            ;
//            return GetRspGameType(rsp.msg);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return GameType.A;
//        }
//    }
//
//    public int GetRspGameType(String rspMsg) {
//        try {
//            JSONObject jo = new JSONObject(rspMsg);
//            int flagConv = jo.optInt("Conversion", ReferrerStatus.Organic);
//            if (flagConv == ReferrerStatus.NonOrganic || flagConv == ReferrerStatus.NoCheck) {
//                if (flagConv == ReferrerStatus.NonOrganic) {
//                    FileTool.writeFileEncrypt(ActivityMgr.getIns().getActivity(), FlagConvFile, "NonOrganic");
//                }
//                return GameType.B;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return GameType.A;
//    }
//
//
//    // 归因轮询
//    public void TryUpdateConversion(int cnt, final Define.CodeRunnable task) {
//        if (cnt == 0) {
//            task.run(GameType.A, String.format("--- all try conversion fail, max cnt: %d", TryConvMaxCnt));
//            return;
//        }
//
//        try {
//            LogUtil.D("--- TryUpdateConversion, cnt: %d", cnt);
//            Map<String, Object> m1 = new HashMap<>();
//            m1.put("Plat", packDB.PlatId);
//            m1.put("PkgName", phoneInfo.PackgeName);
//            m1.put("AppsflyerId", AppsflyerHelper.getIns().GetAfUid(ActivityMgr.getIns().getActivity()));
//
//            String url = GetUrlByRoute(packDB.ReportUrl, "conversion");
//            String json = new JSONObject(m1).toString();
//            LogUtil.D("--- conversion url: %s, json: %s", url, json);
//
//            SHttp rsp = HttpHelper.okhttpPostSync(url, json);
//            LogUtil.D("--- conversion code: %d, msg: %s", rsp.code, rsp.msg);
//
//            LogUtil.A(rsp.code == 200, "--- conversion req fail, code: %d, msg: %s", rsp.code, rsp.msg);
//            ;
//            int flagGame = GetRspGameType(rsp.msg);
//            if (flagGame == GameType.A) {
//                TimerMgr.getIns().setTimeout(5 * 1000, () -> {
//                    TryUpdateConversion(cnt - 1, task);
//                });
//            } else {
//                task.run(GameType.B, "conversion success");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            task.run(GameType.A, String.format("conversion exception", TryConvMaxCnt));
//        }
//    }
//
//    public String GetAfKey() {
//        try {
//            LogUtil.D("--- get key");
//            Map<String, Object> m1 = new HashMap<>();
//            m1.put("Plat", packDB.PlatId);
//            m1.put("Os", Define.EOs.Android);
//            m1.put("Appid", Define.EApp.Teenpatti);
//            m1.put("Deviceid", phoneInfo.DeviceID);
//            m1.put("PkgName", SystemInfoUtil.getPackgeName(ActivityMgr.getIns().getActivity()));
//
//            String url = GetUrlByRoute(packDB.ReportUrl, "launch");
//            String json = new JSONObject(m1).toString();
//            LogUtil.D("--- launch url: %s, json: %s", url, json);
//
//            SHttp rsp = HttpHelper.okhttpPostSync(url, json);
//            LogUtil.D("--- launch code: %d, msg: %s", rsp.code, rsp.msg);
//
//            LogUtil.A(rsp.code == 200, "--- launch req fail, code: %d, msg: %s", rsp.code, rsp.msg);
//
//            JSONObject jo = new JSONObject(rsp.msg);
//            return jo.optString("AppsflyerKey", null);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    public String GetUrlByRoute(String url, String router) {
//        return url.replaceAll("(^http.*/)(\\w+)$", "$1" + router); // 占位符
//    }
//
//    private void clear() {
//
//    }
//
//    public static void OpenUrl(String url) {
//        final Map<String, Object> m1 = new HashMap<>();
//        m1.put(LogUtil.PGFmt("url"), url);
//        Tools.runOnUiThread(() -> {
//            WebviewHelper.showWebview(ActivityMgr.getIns().getActivity(), new JSONObject(m1).toString(), null);
//        });
//    }
//
//    private static String GetFuncKey(final String jsonMsg) {
//        String funcKey = "";
//        try {
//            JSONObject rec = new JSONObject(jsonMsg);
//            funcKey = rec.getString("funcName");
//        } catch (Exception e) {
//            LogUtil.E("--- GetFuncKey Json Fail");
//            e.printStackTrace();
//        }
//        return funcKey;
//    }
//
//
//    private static void GGLogin(final String jsonMsg, final String funcKey, final CallBackFunction cb) {
//        ActionGoogle.getIns().checkLogin(funcKey, loginInfo -> {
//            try {
//                JSONObject data = new JSONObject();
//                data.put("code", loginInfo.code);
//                data.put("id", loginInfo.id);
//
//                data.put("idToken", loginInfo.idToken);
//                // 回调给js
//                cb.onCallBack(data.toString());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//    }
//
//    private static void GGLogout(final String jsonMsg, final String funcKey, final CallBackFunction cb) {
//        ActionGoogle.getIns().checkLogout(funcKey, loginInfo -> {
//            cb.onCallBack("logout ok");
//        });
//    }
//}
