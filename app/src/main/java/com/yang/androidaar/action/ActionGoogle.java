package com.yang.androidaar.action;

import com.yang.androidaar.LogUtil;
import com.yang.androidaar.MyCode.ECode;
import com.yang.androidaar.UnityHelper;
import com.yang.androidaar.login.LoginBase;
import com.yang.androidaar.login.LoginGoogle;

import org.json.JSONException;
import org.json.JSONObject;

public class ActionGoogle extends ActionBase {

    static ActionGoogle instance = null;

    public static ActionGoogle getIns() {
        if (instance == null) {
            instance = new ActionGoogle();
        }
        return instance;
    }

    static String TAG = LogUtil.PGFmt("--- ActionGoogle");

    @Override
    public LoginBase createLoginIns() {
        return new LoginGoogle();
    }

    protected void onAction(@ECode int errCode, final String javaFunc, final String msg) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", errCode);
            jsonObject.put("msg", msg);
            UnityHelper.callUnityFunc(javaFunc, jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getUserInfo(final String jsonMsg, final String javaFunc) {
        checkLogin(javaFunc, loginInfo -> {
            if (loginInfo.code != ECode.Ok) {
                onAction(loginInfo.code, javaFunc, loginInfo.idToken);
                return;
            }

            onAction(ECode.Ok, javaFunc, loginInfo.toJsonStr());
        });
    }
}
