package com.yang.androidaar.other;


import android.content.Context;
import android.os.RemoteException;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerClient.InstallReferrerResponse;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.yang.androidaar.Define;
import com.yang.androidaar.FileTool;
import com.yang.androidaar.LogUtil;
import com.yang.androidaar.MyCode.ECode;
import com.yang.androidaar.Tools;

public class GoogleReferrerHelper {

    private static GoogleReferrerHelper instance = null;

    public static GoogleReferrerHelper getIns() {
        if (instance == null) {
            instance = new GoogleReferrerHelper();
        }
        return instance;
    }

    private static final String TAG = LogUtil.PGFmt("--- ReferrerHelper");
    private InstallReferrerClient mReferrerClient;

    public void start(Context context, Define.CodeRunnable task) {
        // 1. 先读本地, 有则返回
        String localMsg = FileTool.readFileEncrypt(context, Define.getFile_RefererDb());
        if (!Tools.isEmpty(localMsg)) {
            LogUtil.TD(TAG, LogUtil.PGFmt("--- referer local msg: %s"), localMsg);
            task.run(ECode.Ok, localMsg);
            return;
        }

        // 2. 从 InstallReferrerClient 获取, 暂时不加重试机制
        innerTry(context, (code, msg) -> {
            if (code == ECode.Ok) { // 获取成功, 写入到本地
                LogUtil.TD(TAG, "--- referer remote msg: %s", msg);
                FileTool.writeFileEncrypt(context, Define.getFile_RefererDb(), msg);
            }
            task.run(code, msg);
        });
    }

    private void innerTry(Context context, Define.CodeRunnable task) {
        end();
        mReferrerClient = InstallReferrerClient.newBuilder(context).build();
        mReferrerClient.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                int code = ECode.NoIntentError;
                String msg = "";
                switch (responseCode) {
                    case InstallReferrerResponse.OK:
                        try {
                            ReferrerDetails response = mReferrerClient.getInstallReferrer();
                            code = ECode.Ok;
                            msg = response.getInstallReferrer();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                            code = ECode.LoginError;
                            msg = e.getMessage();
                        }
                        break;
                    case InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        code = ECode.SupportError;
                        msg = LogUtil.PGFmt("--- InstallReferrerResponse.FEATURE_NOT_SUPPORTED");
                        break;
                    case InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        code = ECode.TaskError;
                        msg = LogUtil.PGFmt("--- InstallReferrerResponse.SERVICE_UNAVAILABLE");
                        break;
                }
                end();
                task.run(code, msg);
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
                end();
                task.run(ECode.Unknown, "--- onInstallReferrerServiceDisconnected");
            }
        });
    }

    public void end() {
        if (mReferrerClient != null) {
            mReferrerClient.endConnection();
            mReferrerClient = null;
        }
    }
}