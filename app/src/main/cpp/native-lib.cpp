#include <jni.h>
#include <string>
#include <vector>
#include <android/log.h>

using namespace std;

extern "C" jstring
Java_com_hc_wanandroid_MJni_stringFromJNI(
        JNIEnv *env, jclass clazz
) {

    string hello = "Hello from C++ 2";
    return env->NewStringUTF(hello.c_str());
}


extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_hc_wanandroid_MJni_getUrls(JNIEnv *env, jclass clazz,jint size) {

    __android_log_print(ANDROID_LOG_DEBUG,"jni---cpp","1ssss %d",size);

    jclass elementClass = env->FindClass("java/lang/String");

    vector<jstring> is;

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