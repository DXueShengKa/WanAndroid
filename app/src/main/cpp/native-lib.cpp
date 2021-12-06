#include <jni.h>
#include <string>
#include <vector>
#include "Us.h"

using namespace std;

extern "C" jstring
Java_com_yzz_appdemo_MJni_stringFromJNI(
        JNIEnv *env, jclass clazz
) {

    string hello = "Hello from C++ 2";
    return env->NewStringUTF(hello.c_str());
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_yzz_appdemo_MJni_addJNI(
        JNIEnv *env, jclass clazz, jint a, jint b
) {
    jint i = a + b;

    Us us = {};

    us.age = i;
    us.s = '2';

    return env->NewStringUTF(us.toString().c_str());
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_yzz_appdemo_MJni_getUrls(JNIEnv *env, jclass clazz) {
    jclass elementClass = env->FindClass("java/lang/String");

    vector<jstring> is;
    int size = 5;

    is.reserve(size);

    for (int i = 0; i <size; ++i)
        is.push_back(env->NewStringUTF(
                "https://t7.baidu.com/it/u=2291349828,4144427007&fm=193&f=GIF"
                ));

    jobjectArray objectArray = env->NewObjectArray(size, elementClass, nullptr);


    int i = 0;

    for (auto & iterator : is) {

        env->SetObjectArrayElement(objectArray,i++,iterator);
    }


    return objectArray;
}