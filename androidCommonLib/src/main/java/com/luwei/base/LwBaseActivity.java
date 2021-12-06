package com.luwei.base;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.AdaptScreenUtils;


public abstract class LwBaseActivity<P extends IPresent> extends AppCompatActivity implements IView<P> {

    public static final String TAG = "LwBaseActivity";

    private P p;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "current activity: " + getLocalClassName());
        adaptScreen();
        setContentView(getLayoutId());
        initView(savedInstanceState);
        initEvent();
        initData();
        if (isSetStatus()) {
            tranDecorView();
        }
    }

    public abstract void initView(Bundle savedInstanceState);

    public abstract void initData();

    public abstract void initEvent();

    public abstract int getLayoutId();


    public void tranDecorView() {
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    @Override
    public P getP() {
        if (p == null) {
            p = newP();
            if (p != null) {
                p.attachV(this);
            }
        }
        return p;
    }


    @Override
    protected void onDestroy() {
        if (p != null) {
            p.detachV();
        }

        p = null;
        super.onDestroy();
    }


    public boolean isSetStatus() {
        return false;
    }

    private void adaptScreen() {
        int size = 0;
        if ((size = getAdaptSizeVertical()) > 0) {
            AdaptScreenUtils.adaptWidth(getResources(), size);
        } else if ((size = getAdaptSizeHorizontal()) > 0) {
            AdaptScreenUtils.adaptHeight(getResources(), size);
        }
    }

    protected int getAdaptSizeVertical() {
        return 0;
    }

    protected int getAdaptSizeHorizontal() {
        return 0;
    }


    public void showLoading() {

    }


    public void hideLoading() {

    }
}
