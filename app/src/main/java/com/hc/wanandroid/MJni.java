package com.hc.wanandroid;

import java.util.stream.Stream;

public final class MJni {
    static {
        System.loadLibrary("cppdemo");
    }

    public native static String stringFromJNI();


    public native static String[] getUrls(int size);

    public  static void a(){
        var a = Stream.of("").toArray(String[]::new);

        String[] s = new String[6];
    }


}
