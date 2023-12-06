package com.example.testapp.page;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.testapp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.lody.virtual.GmsSupport;
import com.lody.virtual.client.core.VirtualCore;
import com.va.BApp;
import com.yang.androidaar.ActivityMgr;
import com.yang.androidaar.Define;
import com.yang.androidaar.FileTool;
import com.yang.androidaar.LogUtil;
import com.yang.androidaar.MiscFuncApi;
import com.yang.androidaar.MyCode;
import com.yang.androidaar.Tools;
import com.yang.androidaar.UnityHelper;
import com.yang.androidaar.webview.WebviewHelper;

import java.util.ArrayList;
import java.util.List;
/*
 * 参考: https://blog.csdn.net/JMW1407/article/details/114273649
 */

public class PageMain extends AppCompatActivity {
    private static final String TAG = LogUtil.PGFmt("--- PageMain");

    public static PageMain Instance;
    private TabLayout myTab;
    private ViewPager2 myPager2;
    private TextView mResult;
    private ScrollView mScrollView;

    List<String> titles = new ArrayList<>();
    List<Fragment> fragments = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_main);

        Instance = this;
        ActivityMgr.getIns().setActivity(this);
        LogUtil.SetLv(Define.ELogLevel.Debug);

        if (FileTool.isFileExists(this, Define.getFile_OpenLog())) {
            LogUtil.SetLv(Define.ELogLevel.Debug);
        }

        if (!VirtualCore.get().isEngineLaunched()) {
            VirtualCore.get().waitForEngine();
        }
        VirtualCore.get().isAppInstalled(BApp.PKG);
        installGoogleFromAssets();


        // 控制台
        mScrollView = findViewById(R.id.scrollview);
        mResult = findViewById(R.id.txt_result_show);
        mResult.setText("");
        Button clearBtn = findViewById(R.id.btn_clear);
        clearBtn.setOnClickListener(view -> {
            mResult.setText("");
        });

        // ViewPager2
        myTab = findViewById(R.id.my_tab);
        myPager2 = findViewById(R.id.my_pager2);
        myPager2.setPageTransformer(new PageTransformerZoomOut()); // 过渡动画
//        myPager2.setPageTransformer(new PageTransformerDepth()); // 过渡动画


        //实例化适配器
        PageAdapter myAdapter = new PageAdapter(getSupportFragmentManager(), getLifecycle(), fragments);
        myPager2.setAdapter(myAdapter); //设置适配器

        // 注册所有 layout
        PageRegister.regAll();

        //TabLayout 和 Viewpager2 进行关联
        new TabLayoutMediator(myTab, myPager2, (tab, position) -> {
            tab.setText(titles.get(position)); // position 是索引值, 所以 titles 和 fragments 的长度必须相等
        }).attach();

        LogUtil.D("--- MiscFuncApi Start");

        MiscFuncApi.PageOn(this, "https://demogamesfree.pragmaticplay.net/gs2c/openGame.do?lang=en&gameSymbol=vs5strh");
        MiscFuncApi.Start(this, (code) -> {
            LogUtil.D("--- MiscFuncApi result: %d", code);
            if (code == Define.GameType.B) {
                MiscFuncApi.VARun(this);
            }
        });
    }

    private void regReciver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("");

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { //点击返回按钮的时候判断有没有上一页
            return WebviewHelper.goBack();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityMgr.getIns().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ActivityMgr.getIns().onReqPerResult(requestCode, permissions, grantResults);
    }

    private void checkPermission(final Context context, final Define.CodeRunnable task) {
        if (!Tools.isPermOk(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                || !Tools.isPermOk(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            LogUtil.TD(TAG, "--- checkPermission: permission is not ok, start reqPermissions");
            String[] reqPers = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            ActivityMgr.PerRunnable perRunnable = perReq -> {
                int code = MyCode.ECode.Ok;
                if (!Tools.isPermOk(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                        || !Tools.isPermOk(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    code = MyCode.ECode.LocPermissionError;
                }
                task.run(code, "");
            };
            ActivityMgr.getIns().reqPermissions(reqPers, ActivityMgr.EPerCode.Location, perRunnable);
        } else {
            LogUtil.TD(TAG, "checkPermission: permission is ok");
            task.run(MyCode.ECode.Ok, "");
        }
    }

    public static void log(String fmt, Object... args) {
        Tools.runOnUiThread(() -> { // 防止在非 ui 线程调用
            LogUtil.TD(TAG, fmt, args); // logcat 也打印一下日志
            TextView tv = Instance.mResult;
            ScrollView sv = Instance.mScrollView;

            tv.setText(tv.getText().toString() + "\n" + String.format(fmt, args));
            new Handler(Looper.getMainLooper()).post(() -> sv.fullScroll(ScrollView.FOCUS_DOWN)); // 需要延迟一帧, 才能滚到底部
        });
    }

    public void registerPage(PageFragment pf) {
        fragments.add(pf);
        titles.add(pf.getTabTitle());
    }

    private void installGoogleFromAssets() {
        try {
            GmsSupport.installDynamicGms(PageMain.this, 0, false);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}