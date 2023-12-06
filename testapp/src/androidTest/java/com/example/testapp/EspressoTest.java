package com.example.testapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import com.example.testapp.page.PageMain;
import com.yang.androidaar.ActivityMgr;
import com.yang.androidaar.Define;
import com.yang.androidaar.EncryptTool;
import com.yang.androidaar.FileTool;
import com.yang.androidaar.LogUtil;
import com.yang.androidaar.MiscFuncApi;
import com.yang.androidaar.Tools;
//import com.yang.androidaar.firebase.FirebaseHelper;
import com.yang.androidaar.other.JsonSerializer;
import com.yang.androidaar.other.MyUID;
import com.yang.androidaar.timer.TimerMgr;
import com.yang.androidaar.tool.HttpHelper;
import com.yang.androidaar.tool.HttpHelper.SHttp;
import com.yang.androidaar.webview.WebviewHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.function.Consumer;

import static java.lang.String.format;

@RunWith(AndroidJUnit4.class)
//@LargeTest
@MediumTest
public class EspressoTest {
    private static final String TAG = LogUtil.PGFmt("--- EspressoTest");

//    @Rule
//    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

//    @BeforeClass
//    public static void launchActivity() { }

//    @AfterClass
//    public static void suspendActivity() {}

    private static PageMain mActivity = null;

    @Before
    public void launchActivity() {
        ActivityScenario<PageMain> actSro = ActivityScenario.launch(PageMain.class);
        actSro.onActivity((activity) -> { // 在 activity 的 onCreate 方法之后回调
            LogUtil.TD(TAG, "--- EspressoTest, launchActivity: success");
            mActivity = activity;

            String kPlatId = Tools.GetStringVaule(mActivity, "kPlatId");
            LogUtil.TD(TAG, "--- PlatId: %s", kPlatId);
        });
    }

    @After
    public void suspendActivity() {
        try {
            Thread.sleep(1000 * 60 * 60 * 24); // 一天
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Test
//    public void Test_Referrer01() {
//        ReferrerMgr rm = new ReferrerMgr();
//        String srcUrl = "https://rmy.jhg8t.com:443/hotupdate";
//        String router = "launch";
//        String dstUrl = rm.GetUrlByRoute(srcUrl, router);
//        LogUtil.D("--- dstUrl: %s", dstUrl);
//    }

    @Test
    public void Test_Referrer02() {
        MiscFuncApi.Start(mActivity, (code)-> {
            LogUtil.D("--- referrer result, code: %d");
        });
    }

//    @Test
//    public void Test_webview() {
////        String url = "http://localhost:7458/";
////        String url = "https://wst2.fa4gi.com/web/fa4gi.com/";
////        String url = "https://game.birkinteenpt.com";
//        String url = "http://192.168.1.140:8080/res/?isWebview=true&platId=1042&apkVersion=0.2.3.110";
//        Map<String, Object> m1 = new HashMap<>();
//        m1.put("url", url);
//
//        ReferrerMgr.regWebviewHandler();
//        WebviewHelper.showWebview(ActivityMgr.getIns().getActivity(), new JSONObject(m1).toString(), null);
//    }


}
