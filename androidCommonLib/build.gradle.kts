import dependencies.Dep

plugins {
    id("com.android.library")
}

android {
    compileSdk = Dep.Android.compileSdkVersion
    buildToolsVersion = Dep.Android.buildToolsVersion

    defaultConfig {

        minSdk = Dep.Android.minSdkVersion
        targetSdk = Dep.Android.targetSdkVersion


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}

dependencies {

    implementation(Dep.RxJava.rxAndroid)
    implementation(Dep.RxJava.rxJava)

    implementation(Dep.AndroidX.fragmentKtx)
    implementation(Dep.AndroidX.appcompat)
    implementation(Dep.AndroidX.recyclerView)
    implementation(Dep.utilCodeX)

    implementation(Dep.OkHttp.okhttp3)
    implementation(Dep.Retrofit.retrofit2)
    implementation(Dep.Retrofit.gsonConverter)
    implementation(Dep.Retrofit.rxJava2Adapter)

    implementation(Dep.glide)


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}