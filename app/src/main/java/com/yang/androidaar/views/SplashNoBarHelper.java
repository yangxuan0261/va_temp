package com.yang.androidaar.views;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.yang.androidaar.LogUtil;

import java.lang.reflect.Method;

public class SplashNoBarHelper implements SplashInter {
    static SplashNoBarHelper instance = null;

    public static SplashNoBarHelper getIns() {
        if (instance == null) {
            instance = new SplashNoBarHelper();
        }
        return instance;
    }

    private final String TAG = LogUtil.PGFmt("--- SplashHelper");

    private Activity mActivity;
    private ImageView logoView;
    private Animation.AnimationListener animListener;
    private boolean firstStep = true;
    private ImageView splashView;
    private FrameLayout mUnityPlayer;
    // 屏幕宽高
    public int ScreenWidth = 1280;
    public int ScreenHeight = 720;

//    private MyProgressBar mProgressBar;

    public void init(Activity act, FrameLayout player) {
        mActivity = act;
        mUnityPlayer = player;
        initScreenSize();
    }

    private void initScreenSize() {
        try {
            Resources r = mActivity.getResources();
            Activity curActivity = mActivity;
            ScreenWidth = r.getDisplayMetrics().widthPixels;
            ScreenHeight = r.getDisplayMetrics().heightPixels;
            LogUtil.TD(LogUtil.PGFmt("MainActivty"), LogUtil.PGFmt("dwidth0 = ") + ScreenWidth + LogUtil.PGFmt(", dheight0 = ") + ScreenHeight);
            final int VERSION = Build.VERSION.SDK_INT;
            LogUtil.TD(LogUtil.PGFmt("MainActivty"), LogUtil.PGFmt("Build.VERSION.SDK_INT = ") + VERSION);
            // 解決有有虚拟键盘不计算虚拟键盘高度的问题
            if (VERSION < 17) {
                DisplayMetrics dm = new DisplayMetrics();
                curActivity.getWindowManager().getDefaultDisplay().getRealMetrics(dm);
                ScreenWidth = dm.widthPixels;
                ScreenHeight = dm.heightPixels;
            } else {  // API 17之前通过反射获取
                Display display = mActivity.getWindowManager().getDefaultDisplay();
                DisplayMetrics dm = new DisplayMetrics();
                @SuppressWarnings("rawtypes")
                Class c;
                try {
                    c = Class.forName("android.view.Display");
                    @SuppressWarnings("unchecked")
                    Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
                    method.invoke(display, dm);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ScreenWidth = dm.widthPixels;
                ScreenHeight = dm.heightPixels;
            }
            LogUtil.TD(LogUtil.PGFmt("MainActivty"), LogUtil.PGFmt("dwidth1 = ") + ScreenWidth + LogUtil.PGFmt(", dheight1 = ") + ScreenHeight);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setProgress(final String jsonMsg) {
//        if (mProgressBar == null) {
//            return;
//        }
//        mProgressBar.setProgress(jsonMsg);
    }

    public void setProgress(final MyProgressBar.CArg arg) {
//        if (mProgressBar == null) {
//            return;
//        }
//        mProgressBar.setArg(arg);
    }

    public void showSplash() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createView();
            }
        });
    }

    public void showLogoSplash() {

    }

    public void createView() {
        if (splashView != null) {
            return;
        }
        try {
            splashView = addFullScreenImage("ts_splash");
            // 开始显示闪屏
            showProgressBar(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ImageView addFullScreenImage(String imgName) {
        LogUtil.TD(LogUtil.PGFmt("MainActivty"), LogUtil.PGFmt("imgName = ") + imgName);
        ImageView imgView = new ImageView(mActivity);
        int bg_id = mActivity.getResources().getIdentifier(imgName, "drawable", mActivity.getPackageName());
        if (bg_id != 0) {
            imgView.setImageResource(bg_id);
            imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mUnityPlayer.addView(imgView, ScreenWidth, ScreenHeight);
            return imgView;
        } else {
            return null;
        }
    }

    public void hideSplash() {
        if (splashView == null) {
            return;
        }
        Runnable closeSplashFn = new Runnable() {
            @Override
            public void run() {
                LogUtil.TD(TAG, "--- run: closeSplashFn");
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (logoView != null) {
                                logoView.clearAnimation();
                                mUnityPlayer.removeView(logoView);
                                logoView = null;
                            }
                            hideProgressBar();
                            mUnityPlayer.removeView(splashView);
                            splashView = null;
                        } catch (Exception e) {
                            Log.e(TAG, "--- run: closeSplashFn, err:" + e.getMessage());
                        }
                    }
                });
            }
        };

//        mProgressBar.doFinish(closeSplashFn);
        closeSplashFn.run();
    }

    public void showProgressBar(boolean showText) {
//        if (mProgressBar == null) {
//            mProgressBar = new MyProgressBar(mUnityPlayer, mActivity, showText);
//        }
    }

    public void hideProgressBar() {
//        if (mProgressBar != null) {
//            mProgressBar.Destroy(mUnityPlayer);
//            mProgressBar = null;
//        }
    }

    public void setTips(String str) {
//        if (mProgressBar != null) {
//            mProgressBar.setTips(str);
//        }
    }
}
