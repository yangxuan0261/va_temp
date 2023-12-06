package com.yang.androidaar.views;

import android.app.Activity;
import android.widget.FrameLayout;

public interface SplashInter {
    void init(Activity act, FrameLayout player);

    void showSplash();

    void setProgress(final String jsonMsg);

    void setTips(String str);

    public void hideSplash();
}
