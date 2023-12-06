package com.example.testapp.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yang.androidaar.LogUtil;

public abstract class PageFragment extends Fragment {

    protected @LayoutRes
    int mLayoutId = -1;
    protected String mTabTitle = "default";

    protected void setPageInfo(@LayoutRes int layoutId, String tabTitle) {
        mLayoutId = layoutId;
        mTabTitle = tabTitle;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.TA("--- PageFragment", mLayoutId != -1, "--- not setLayout");
        return inflater.inflate(mLayoutId, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public String getTabTitle() {
        return mTabTitle;
    }
}