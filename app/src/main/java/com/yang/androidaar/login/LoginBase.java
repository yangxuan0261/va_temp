package com.yang.androidaar.login;

import com.yang.androidaar.LogUtil;
import com.yang.androidaar.MyCode.ECode;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginBase {

    public static class CLoginInfo {

        // 登录必要信息
        public @ECode
        int code = ECode.LoginError;
        public String id = "";
        public String idToken = "";
        public String javaFunc = "";

        public String otherInfo = "";

        public void dump(String flag) {
            String log = String.format(LogUtil.PGFmt("--- %s\njavaFunc:%s\ncode:%d\nid:%s\nidToken:%s"), flag, javaFunc, code, id, idToken);
            LogUtil.TD("CLoginInfo", log);
        }

        public void dump() {
            dump(LogUtil.PGFmt("--- CLoginInfo"));
        }

        public String toJsonStr() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("code", code);
                jsonObject.put("id", id);
                jsonObject.put("idToken", idToken);
                jsonObject.put("otherInfo", otherInfo);
                return jsonObject.toString();
            } catch (Exception e) {
                return "";
            }
        }
    }

    public interface LoginRunnable {
        void run(CLoginInfo loginInfo);
    }

    public interface LoginListener {

        void onLogin(CLoginInfo loginInfo);

        void onLogout(CLoginInfo loginInfo);
    }

    protected LoginListener mListener;
    protected CLoginInfo mLoginInfo = null;

    public LoginBase() {
    }


    public void setArgs(String javaFunc, LoginListener listener) {
        mListener = listener;
        mLoginInfo = new CLoginInfo();
        mLoginInfo.javaFunc = javaFunc;
    }

    public void logout() {
    }

    public void login() {
    }

}
