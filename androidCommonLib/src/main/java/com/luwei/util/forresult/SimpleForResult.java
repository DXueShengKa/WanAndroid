package com.luwei.util.forresult;

import android.content.Context;
import android.content.Intent;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * @Author: chenjianrun
 * @Time: 2018/12/7
 * @Description:   避免调用 startActivity 时，需要 onActivityResult 处理的类
 */
public final class SimpleForResult {
    private static final String TAG = "SimpleForResult";
    private final ActivityResultLauncher<Intent> resultLauncher;
    private Context context;
    private PublishSubject<ActivityResultInfo> subject;

    public SimpleForResult(FragmentActivity activity) {
        context = activity;
        resultLauncher = activity.getActivityResultRegistry()
                .register(TAG+System.currentTimeMillis(), new ActivityResultContracts.StartActivityForResult(), this::callback);
    }

    public SimpleForResult(Fragment fragment){
        context = fragment.requireActivity();
        resultLauncher = fragment.requireActivity().getActivityResultRegistry()
                .register(TAG+System.currentTimeMillis(), new ActivityResultContracts.StartActivityForResult(), this::callback);
    }

    private void callback(ActivityResult result){
        subject.onNext(new ActivityResultInfo(result.getResultCode(),result.getData()));
        subject.onComplete();
    }

    public Observable<ActivityResultInfo> startForResult(Intent intent) {
        resultLauncher.launch(intent);
        subject = PublishSubject.create();
        return subject;
    }

    public Observable<ActivityResultInfo> startForResult(Class<?> clazz) {
        return startForResult(new Intent(context,clazz));
    }

}
