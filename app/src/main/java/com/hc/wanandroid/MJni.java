package com.hc.wanandroid;

import java.util.stream.Stream;

public final class MJni {
  /*  static {
        System.loadLibrary("cppdemo");
    }

    public native static String stringFromJNI();

    public native static String addJNI(int a,int b);

    public native static String[] getUrls();*/

    public  static void a(){
        var a = Stream.of("").toArray(String[]::new);

        String[] s = new String[6];
    }


}
