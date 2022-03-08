package com.hc.wanandroid;

public abstract class RustJni {
    static {
        System.loadLibrary("wanrust");
    }

    public native static String addJNI(int a,int b);

    public native static int randomInt();
}
