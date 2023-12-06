package com.yang.androidaar.action;

import android.util.Log;

import com.yang.androidaar.LogUtil;
import com.yang.androidaar.login.LoginBase;
import com.yang.androidaar.login.LoginBase.LoginRunnable;

import static com.yang.androidaar.login.LoginBase.CLoginInfo;
import static com.yang.androidaar.login.LoginBase.LoginListener;

public class ActionBase implements LoginListener {

    String TAG = LogUtil.PGFmt("--- ActionBase");

    private LoginBase loginIns;
    private LoginRunnable runnableIns = null;

    public void checkLogin(final String javaFunc, final LoginRunnable task) {
        startCheckLogin(javaFunc, task);
    }

    private void startCheckLogin(final String javaFunc, LoginRunnable task) {
        runnableIns = task;
        loginIns = createLoginIns();
        loginIns.setArgs(javaFunc, this);
        loginIns.login();
    }

    public void checkLogout(final String javaFunc, final LoginRunnable task) {
        runnableIns = task;
        loginIns = createLoginIns();
        loginIns.setArgs(javaFunc, this);
        loginIns.logout();
    }

    @Override
    public void onLogin(CLoginInfo loginInfo) {
        if (runnableIns != null) {
            runnableIns.run(loginInfo);
            runnableIns = null;
        }
        loginIns = null; // 登录完设置为 null
    }

    @Override
    public void onLogout(CLoginInfo loginInfo) {
        if (runnableIns != null) {
            runnableIns.run(loginInfo);
            runnableIns = null;
        }
        loginIns = null; // 登录完设置为 null
    }

    public LoginBase createLoginIns() {
        Log.e(TAG, "createLoginIns assert false");
        return null;
    }
}
