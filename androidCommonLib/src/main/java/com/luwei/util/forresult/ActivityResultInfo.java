package com.luwei.util.forresult;

import android.content.Intent;

/**
 * @Author: chenjianrun
 * @Time: 2018/12/7
 * @Description:
 */
public final class ActivityResultInfo {

    private int resultCode;
    private Intent data;

    public ActivityResultInfo(int resultCode, Intent data) {
        this.resultCode = resultCode;
        this.data = data;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public Intent getData() {
        return data;
    }

    public void setData(Intent data) {
        this.data = data;
    }
}
