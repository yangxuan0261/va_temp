package com.example.testapp.page.misc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.testapp.R;
import com.example.testapp.page.PageFragment;
import com.example.testapp.page.PageMain;
import com.yang.androidaar.ActivityMgr;
import com.yang.androidaar.FileTool;
import com.yang.androidaar.LogUtil;
import com.yang.androidaar.MiscFuncApi;
import com.yang.androidaar.VATool;
import com.yang.androidaar.other.AppsflyerHelper;
import com.yang.androidaar.other.JsonSerializer;
import com.yang.androidaar.tool.HttpHelper;

/*
 * 测试
 * */

public class PageTest extends PageFragment {

    public PageTest() {
        super();
        setPageInfo(R.layout.page_test, "test"); // 设置 pageview 及 tab
    }

    @Override
    public void onViewCreated(@NonNull View pageView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(pageView, savedInstanceState);

        pageView.findViewById(R.id.fab1).setOnClickListener(view -> {
            PageMain.log("--- fab1");
//            ReferrerMgr rm = new ReferrerMgr();
//            rm.IsHotFixCashOn();
//            rm.Report();

            com.yang.androidaar.MiscFuncApi.Start(ActivityMgr.getIns().getActivity(), null);

//            String url = "https://game.birkinteenpt.com";
//            Map<String, Object> m1 = new HashMap<>();
//            m1.put("url", url);
//            WebviewHelper.showWebview(ActivityMgr.getIns().getActivity(), new JSONObject(m1).toString(), null);
//            LoginHelper.getIns().signFacebook("hellos");

//            String url = "hello01://world01/wolegequ01?goodsId=10011002";
//            PageMain.log("--- url: %s", url);
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//            ActivityMgr.getIns().getActivity().startActivity(intent);

//            String url = "http://192.168.1.200:59090/public/pay.html";
//            Tools.openUrl(ActivityMgr.getIns().getActivity(), url, null);

//            TestLoading();
//            new Upgrade().Check();
        });
        pageView.findViewById(R.id.fab2).setOnClickListener(view -> {
//            PageMain.log("--- fab2");
//            LoginHelper.getIns().logoutFacebook("hellos");


        });
        pageView.findViewById(R.id.fab3).setOnClickListener(view -> {
            PageMain.log("--- fab3");
            Intent intent = new Intent("com.example.ACTION_CUSTOM_BROADCAST_B");
            intent.setPackage("aaa.bbb.ddd"); // 设置接收广播的应用包名
            intent.putExtra("message", "A say world");
            this.getActivity().sendBroadcast(intent);
        });
        pageView.findViewById(R.id.fab4).setOnClickListener(view -> {
//            PageMain.log("--- fab4");
//            TestDownload();
//            VATool.getBDataDirInaA(ActivityMgr.getIns().getActivity(), "com.aaa.bbb");
//            MiscFuncApi.VARun(ActivityMgr.getIns().getActivity());
//            Exception ex = FileTool.CopyAssetsFile(ActivityMgr.getIns().getActivity(), "aaa.txt", "bbb.txt");
        });
    }


    static void TestAf01() {
        String afkey = "UEsd5zkrVzcxt6DER5jcMT";
        AppsflyerHelper.getIns().init(ActivityMgr.getIns().getActivity(), false, afkey, (code, transfer) -> { // Appsflyer sdk 接入
            LogUtil.D("--- AppsflyerHelper Conversion ok");
        });
    }

    static void TestAf02() {
        JsonSerializer.CEventInfo ei = new JsonSerializer.CEventInfo();
        ei.name = "TestEvent001";
        AppsflyerHelper.getIns().logEvent(ActivityMgr.getIns().getActivity(), ei, null);
    }

    static void TestDownload() {
        String url = "https://pic04.wilker.cn/20200301002058-1.png";
        String apkPath = String.format("%s/va_demo/demo.png", ActivityMgr.getIns().getActivity().getFilesDir());
        LogUtil.D("--- apkPath: %s", apkPath);

        HttpHelper.okhttpDownloadAsync(url, apkPath, (code, msg) -> {
            LogUtil.D("--- code: %d, msg: %s", code, msg);
        }, null);
    }
}
