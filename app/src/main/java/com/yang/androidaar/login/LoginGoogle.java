package com.yang.androidaar.login;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.yang.androidaar.ActivityMgr;
import com.yang.androidaar.LogUtil;
import com.yang.androidaar.MyCode.ECode;
import com.yang.androidaar.Tools;

import java.util.concurrent.Executor;

import static java.lang.String.format;

public class LoginGoogle extends LoginBase implements Executor {

    final String TAG = LogUtil.PGFmt("--- LoginGoogle");
    final static String GoogleAppIdKey = LogUtil.PGFmt("google_login_app_id");

    @Override
    public void login() {
        LogUtil.D("--- login.GoogleAppIdKey: %s", Tools.GetStringVaule(ActivityMgr.getIns().getActivity(), GoogleAppIdKey));
        try {
            if (!Tools.checkIsSupportGoogle()) {
                mLoginInfo.code = ECode.SupportError;
                mLoginInfo.idToken = LogUtil.PGFmt("--- checkIsSupportGoogle false");
                mListener.onLogin(mLoginInfo);
                return;
            }

            GoogleSignInAccount lastAccount = GoogleSignIn.getLastSignedInAccount(ActivityMgr.getIns().getActivity());
            if (lastAccount != null && !lastAccount.isExpired()) { // 已经授权过了
                formatUserInfo(lastAccount);
                mListener.onLogin(mLoginInfo);
            } else {
                //如果未授权则可以调用登录，mGoogleSignInClient为初始化好的Google登录实例，RC_SIGN_IN为随意唯一返回标识码，int即可。
                Intent signInIntent = getSignInClient().getSignInIntent();


                ActivityMgr.ActRunnable task = new ActivityMgr.ActRunnable() {
                    @Override
                    public void run(ActivityMgr.CActReq actReq) {
                        LogUtil.TD(TAG, "ActivityMgr result: " + actReq.toString());

                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(actReq.data);
                        handleSignResultGoogle(task);
                    }
                };

                ActivityMgr.getIns().startActForResult(signInIntent, ActivityMgr.EActCode.GoogleSignIn, task);
                LogUtil.TD(TAG, LogUtil.PGFmt("--- 开始登录"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.D("--- login err: ", e);
            mLoginInfo.code = ECode.LoginError;
            mLoginInfo.idToken = LogUtil.PGFmt("--- login Google fail:") + e.getMessage();
            mListener.onLogin(mLoginInfo);
        }
    }

    GoogleSignInClient getSignInClient() {
        //初始化gso，server_client_id为添加的客户端id
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.server_client_id)) // 错误的获取方式
                .requestIdToken(Tools.GetStringVaule(ActivityMgr.getIns().getActivity(), GoogleAppIdKey))
                .requestEmail()
                .build();
        return GoogleSignIn.getClient(ActivityMgr.getIns().getActivity(), gso);
    }

    private void formatUserInfo(GoogleSignInAccount account) {
        mLoginInfo.code = ECode.Ok;
        mLoginInfo.id = account.getId();
        mLoginInfo.idToken = account.getIdToken();
        LogUtil.TD(TAG, format(LogUtil.PGFmt("--- token, google:\nid: %s\nidToken: %s"), mLoginInfo.id, mLoginInfo.idToken));
    }

    @Override
    public void logout() {
        getSignInClient().signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mListener.onLogout(mLoginInfo);
            }
        });
    }

    @Override
    public void execute(Runnable command) {
        command.run();
    }

    public void handleSignResultGoogle(Task<GoogleSignInAccount> completedTask) {
        LogUtil.TD(TAG, LogUtil.PGFmt("--- 经登完成"));

        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account == null) {
                mLoginInfo.code = ECode.LoginError;
                mLoginInfo.idToken = LogUtil.PGFmt("handleSignResultGoogle, account is null");
            } else {
                formatUserInfo(account);
            }
        } catch (ApiException e) {
            // The ApiException mStatus code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            mLoginInfo.code = e.getStatusCode();
            mLoginInfo.idToken = LogUtil.PGFmt("signInResult:failed, ApiException code=") + e.getStatusCode();
        } finally {
            mListener.onLogin(mLoginInfo);
        }
    }
}
