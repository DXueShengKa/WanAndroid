package com.luwei.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class LwBaseFragment<P extends IPresent>
        extends Fragment implements IView<P> {
    protected FragmentActivity hostActivity;
    private P p;
    protected View mRootView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.i(LwBaseActivity.TAG, "current fragment: "+this.getClass().getSimpleName());
        this.mRootView = inflater.inflate(this.getLayoutId(), container, false);
        initView(savedInstanceState);
        initEvent();
        initData();
        return mRootView;
    }

    public abstract void initView(Bundle savedInstanceState);

    public abstract void initData();

    public abstract void initEvent();

    public abstract int getLayoutId();

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
    public void onDestroy() {
        if (p != null) {
            p.detachV();
        }
        p = null;
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.hostActivity = (FragmentActivity) context;
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        hostActivity = null;
    }

    public void showLoading() {

    }


    public void hideLoading() {

    }

}
