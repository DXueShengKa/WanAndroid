extern crate jni;
// extern crate ndk_sys;


use std::os::raw::{c_char};
// use std::ffi::{CString};

use jni::JNIEnv;
use jni::objects::{JClass, JObject, JString, JValue};

use jni::sys::{jint, jstring};

use rand::prelude::*;


/*pub type Callback = unsafe extern "C" fn(*const c_char) -> ();

#[no_mangle]
#[allow(non_snake_case)]
pub extern "C" fn invokeCallbackViaJNA(callback: Callback) {
    let s = CString::new(hello::greetings_from_rust()).unwrap();
    unsafe { callback(s.as_ptr()); }
}

#[no_mangle]
#[allow(non_snake_case)]
pub extern "C" fn Java_com_rc_rustspike_myapplication_MainActivity_invokeCallbackViaJNI(
    env: JNIEnv,
    _class: JClass,
    callback: JObject
) {
    let s = String::from(hello::greetings_from_rust());
    let response = env.new_string(&s)
        .expect("Couldn't create java string!");
    env.call_method(callback, "callback", "(Ljava/lang/String;)V",
                    &[JValue::from(JObject::from(response))]).unwrap();
}*/


// #[cfg_attr(target_os = "android", ndk_glue::main(logger(level = "debug", tag = "my-tag")))]
#[no_mangle]
#[allow(non_snake_case)]
pub extern "C" fn Java_com_hc_wanandroid_RustJni_addJNI(
    env: JNIEnv,
    _class: JClass,
    a: jint,
    b: jint,
)->jstring {



        // __android_log_print(3,"jni".as_ptr,"22".as_ptr);
    // let s:String = env.get_string(string).expect("获取string失败").into();
    // println!("jni --- {} {}",a,b);

    let response = env.new_string(format!("a + b = {}",a+b))
        .expect("无法创建 java string!");

    response.into_inner()
}

#[no_mangle]
#[allow(non_snake_case)]
pub extern "C" fn Java_com_hc_wanandroid_RustJni_randomInt(
    env: JNIEnv,
    _class: JClass,
)->jint {
    random()
}


