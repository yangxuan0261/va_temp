package com.yang.androidaar.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.yang.androidaar.ActivityMgr;
import com.yang.androidaar.Define;
import com.yang.androidaar.LogUtil;
import com.yang.androidaar.Tools;
import com.yang.androidaar.timer.TimerMgr;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyProgressBar {

    public static class CArg {
        public CArg() {
        }

        public CArg(String m, long t) {
            this.msg = m;
            this.second = t;
        }

        long second = 1; // 完成周期
        boolean isSpeedup = false;
        String msg;
        int preventVal = 0; // 拦截值, 防止下载速度小于进度条
        Runnable task;

        public String toString() {
            return String.format("arg, second:%f, msg:%s, preventVal:%d\n", second, msg, preventVal);
        }
    }

    public interface IMathListener {
        int doMathOperator(int start, int plusValue);
    }

    private static String TAG = LogUtil.PGFmt("--- MyProgressBar");
    private Activity mCtx;
    private ProgressBar mProgressBar;
    private TextView mBarText;
    private TextView mTipText;
    private List<CArg> mLaunchBarList = new ArrayList<>();
    private List<CArg> mLaunchTipsList = new ArrayList<>();
    private int mLaunchBarTmrId = 0;
    private int mLaunchTipTmrId = 0;
    private int mProgress = 0;
    private RelativeLayout rootView;
    public static final int PB_MAX = 10000;
    public final int kFps = 60;
    private float mDtCnt; //  累计增量, 用于 加速/减速
    private long mDtTime; // 定时器周期增量
    private float kSpeedup = 10f; // 加速倍率
    private List<CArg> mArgList = new ArrayList<>();
    private Handler mHandler;
    private Handler mHandlerTips;

    public MyProgressBar(FrameLayout player, Activity context, boolean showText) {
        mCtx = context;
        CreatePBar(showText);
        SuitLayout(player);

        mDtTime = PB_MAX / kFps;

        mProgressBar.setVisibility(View.VISIBLE);
        if (mBarText != null) {
            mBarText.setVisibility(View.VISIBLE);
        }

        initProgress();
        startProgress();
    }

    public void initProgress() {
        mLaunchBarList.add(new CArg("Init APP…", 5));
        mLaunchBarList.add(new CArg("Loading data…", 9));

        mLaunchTipsList.add(new CArg("Loading latest version…", 3));
        mLaunchTipsList.add(new CArg("Reducing network traffic…", 2));
        mLaunchTipsList.add(new CArg("Starting internally stored program…", 3));
        mLaunchTipsList.add(new CArg("Reducing resource consumption…", 1));
    }

    public void startProgress() {
        if (mHandler == null) {
            mHandler = new Handler() {
                public void handleMessage(Message msg) {
                    int progress = msg.what > PB_MAX ? PB_MAX : msg.what;
                    String showMsg = (String) msg.obj;
                    if (mProgressBar != null) {
                        mProgressBar.setProgress(progress);
                        mBarText.setText(String.format("%s (%d%%)", showMsg, Math.round(progress / 100)));
                    }

                }
            };
        }

        Runnable progressFn = new Runnable() { // 进度
            @Override
            public void run() {
                if (mArgList.size() == 0) {
                    return;
                }

                CArg currArg = mArgList.get(0);
                if (mProgress < currArg.preventVal) {
                    mProgress += mDtCnt;
                    Message msg = mHandler.obtainMessage(mProgress, currArg.msg);
                    mHandler.sendMessage(msg);
                }
//                    LogUtil.TD(TAG, String.format("--- mProgress:%d,mDtCnt:%f, arg:%s", mProgress, mDtCnt, currArg));
                if (mProgress > PB_MAX) {
                    onComplete();
                }
            }
        };
        TimerMgr.getIns().addTimer(0, progressFn); // 表示每一帧的意思

        showNextBar();
        showNextTip();
    }

    public void showNextBar() {
        CArg arg = popList(mLaunchBarList);
        if (arg != null) {
            setArg(arg); // 跑新的进度

            mLaunchBarTmrId = TimerMgr.getIns().setTimeout(arg.second * 1000, new Runnable() {
                @Override
                public void run() {
                    showNextBar(); // 递归执行完所有
                }
            });
        }
    }

    public void showNextTip() {
        CArg arg = popList(mLaunchTipsList);
        if (arg != null) {
            setTips(arg.msg);

            mLaunchTipTmrId = TimerMgr.getIns().setTimeout(arg.second * 1000, new Runnable() {
                @Override
                public void run() {
                    showNextTip(); // 递归执行完所有
                }
            });
        }
    }

    private <T> T popList(List<T> lst) {
        if (lst != null && lst.size() > 0) {
            return lst.remove(0);
        }
        return null;
    }

    // lua 接口
    public void setProgress(final String jsonMsg) {
        try {
            JSONObject jsonObject = new JSONObject(jsonMsg);
            CArg arg = new CArg();
            arg.second = jsonObject.getInt("second");
            arg.msg = jsonObject.getString("msg");
            setArg(arg);

            if (mLaunchBarTmrId > 0) {
                TimerMgr.getIns().removeTimer(mLaunchBarTmrId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setArg(final CArg arg) {
        if (arg.second == 0) {
            arg.second = 1; // 防止除 0
        } else if (arg.second > 160) {
            arg.second = 160; // 超过 16 秒, mDtCnt 会小于1, mProgress += mDtCnt 时会强转成 0, 导致一直为 0
        }

        arg.preventVal = (int) (8500 + Math.round(Math.random() * 1400.0));
        if (mArgList.size() > 0) { // 老进度, 加速, 拦截放行
            CArg currArg = mArgList.get(0);
            currArg.preventVal = Integer.MAX_VALUE;
            if (!currArg.isSpeedup) { // 当前不在加速情况下才加速
                currArg.isSpeedup = true;
                mDtCnt *= kSpeedup;
            }
        } else {
            mDtCnt = mDtTime / arg.second; // 正常速度
        }
        mArgList.add(arg);
    }

    public void doFinish(Runnable task) {
        for (int i = mArgList.size(); i > 1; i--) { // 移除掉所有的任务, 只保留最后一个
            mArgList.remove(0);
        }

        int len = mArgList.size();
        for (int i = 0; i < len; i++) {
            CArg arg = mArgList.get(i);
            arg.preventVal = Integer.MAX_VALUE;
            if (i == (len - 1)) { // 最后一个才是 hideSplash 的回调
                arg.task = task;
            }
            if (i == 0 && !arg.isSpeedup) {
                arg.isSpeedup = true;
                mDtCnt *= kSpeedup;
            }
        }
    }

    private void onComplete() {
        if (mArgList.size() == 0) {
            return;
        }
        if (mArgList.size() > 1) { // 有未跑完的进度
            mArgList.remove(0);
            CArg nextArg = mArgList.get(0);
            CArg lastArg = mArgList.get(mArgList.size() - 1);

            mProgress = 0;
            mDtCnt = mDtTime / nextArg.second;

            if (mArgList.size() > 1 || lastArg.task != null) { // 移除完一个还有两个以上 或者是 hideSplash, 加速
                mDtCnt *= kSpeedup;
                nextArg.isSpeedup = true;
                nextArg.preventVal = Integer.MAX_VALUE;
            }
        } else {
            CArg lastArg = mArgList.get(0);
            mArgList.remove(0);
            if (lastArg.task != null) {
                lastArg.task.run();
            }
        }
    }

    // 创建进度条
    @SuppressLint({"NewApi", "UseValueOf"})
    private void CreatePBar(boolean showText) {
        mProgressBar = new ProgressBar(mCtx);
        //通过反射设置progressBar横向展示
        BeanUtils.setFieldValue(mProgressBar, "mOnlyIndeterminate", new Boolean(false));
        mProgressBar.setIndeterminate(false);

        // 混淆文件名
        String pbValue = Tools.GetStringVaule(ActivityMgr.getIns().getActivity(), Define.getResKey_ProgressBar());
        String pbName = pbValue != null ? pbValue : Define.getdDefault_ProgressBar();

        int pb_drawable_id = mCtx.getResources().getIdentifier(pbName, "drawable", mCtx.getPackageName());
        Drawable customDrawable = mCtx.getResources().getDrawable(pb_drawable_id);
        //设置进度图层
        mProgressBar.setProgressDrawable(customDrawable);
        mProgressBar.setIndeterminateDrawable(mCtx.getResources().getDrawable(android.R.drawable.progress_indeterminate_horizontal));
        mProgressBar.setMax(PB_MAX);

        if (showText) {
            mBarText = new TextView(mCtx);
            mBarText.setTextColor(Color.WHITE);
            mBarText.setGravity(Gravity.CENTER);
            mBarText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            mBarText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            // 投影
            mBarText.setShadowLayer(1, 1, 1, Color.BLACK);
            mBarText.setText("");
        }
        if (mTipText == null) {
            mTipText = new TextView(mCtx);
            mTipText.setTextColor(Color.WHITE);
            mTipText.setGravity(Gravity.CENTER);
            mTipText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            mTipText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            // 投影
            mTipText.setShadowLayer(1, 1, 1, Color.BLACK);
            mTipText.setText("");
        }
    }

    public void setTips(final String str) {
        if (mLaunchTipTmrId > 0) {
            TimerMgr.getIns().removeTimer(mLaunchTipTmrId);
        }

        if (mTipText != null) {
            if (mHandlerTips == null) {
                mHandlerTips = new Handler(Looper.getMainLooper()) { // 只能在 主循环 中干 ui 的事
                    public void handleMessage(Message msg) {
                        String showMsg = (String) msg.obj;
                        if (showMsg != null && mTipText != null) {
                            mTipText.setText(showMsg);
                        }
                    }
                };
            }
            Message msg = mHandlerTips.obtainMessage(0, str);
            mHandlerTips.sendMessage(msg);
        }
    }

    // 视图布局
    private void SuitLayout(FrameLayout player) {
        int s_w = SplashHelper.instance.ScreenWidth;
        int s_h = SplashHelper.instance.ScreenHeight;
        float scale = ((float) s_h / 720);
        /*if (scale < 1) {
            scale = 1;
        }*/
        // 进度条的父节点
        RelativeLayout bRootLY = new RelativeLayout(mCtx);
        RelativeLayout.LayoutParams bRootLP = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        // 进度条的布局
        RelativeLayout.LayoutParams barRelativeLP = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        barRelativeLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
        barRelativeLP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        // 分辨率适配
        barRelativeLP.height = (int) ((float) 30 * scale);
        // 解决低版本 API 九宫图不正常的问题
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            barRelativeLP.height = (int) ((float) 35 * scale);
        }
        barRelativeLP.width = (int) ((float) 490 * scale);
        barRelativeLP.bottomMargin = (int) ((float) 100 * scale);
//        barRelativeLP.rightMargin = 100;
//        barRelativeLP.leftMargin = 100;
        mProgressBar.setLayoutParams(barRelativeLP);
        bRootLY.addView(mProgressBar);

        if (mBarText != null) {
            // 进度条文字
            RelativeLayout.LayoutParams txtRelativeLP = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            txtRelativeLP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            txtRelativeLP.bottomMargin = (int) ((float) 103 * scale);
            mBarText.setLayoutParams(txtRelativeLP);
            bRootLY.addView(mBarText);
        }
        if (mTipText != null) {
            // 进度条文字
            RelativeLayout.LayoutParams tipsRelativeLP = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            tipsRelativeLP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            tipsRelativeLP.bottomMargin = (int) ((float) 60 * scale);
            mTipText.setLayoutParams(tipsRelativeLP);
            bRootLY.addView(mTipText);
        }
        player.addView(bRootLY, bRootLP);
        this.rootView = bRootLY;
    }

    public static LayerDrawable getProgressBarLayerDrawable() {
        //记住一句话，所有xml能实现的都能找到对应的对象，用java代码来实现
        GradientDrawable shapeDrawable = new GradientDrawable();
        shapeDrawable.setCornerRadius(10);
        shapeDrawable.setColor(Color.rgb(172, 76, 169));
        ClipDrawable clipDrawable = new ClipDrawable(shapeDrawable, ClipDrawable.VERTICAL, ClipDrawable.HORIZONTAL);
        ClipDrawable[] drawables = new ClipDrawable[]{clipDrawable};
        LayerDrawable layerDrawable = new LayerDrawable(drawables);
        return layerDrawable;
    }

    public void Destroy(FrameLayout player) {
        if (this.rootView != null) {
            player.removeView(this.rootView);
            this.rootView = null;
        }
        mCtx = null;
        mProgressBar = null;
        mBarText = null;
        mTipText = null;
        mHandler = null;
        mArgList.clear();
        TimerMgr.getIns().destroy();
    }
}