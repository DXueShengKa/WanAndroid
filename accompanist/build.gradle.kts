import dependencies.Dep

plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    compileSdk = Dep.Android.compileSdkVersion
    buildToolsVersion = Dep.Android.buildToolsVersion

    defaultConfig {
        minSdk = Dep.Android.minSdkVersion
        targetSdk = Dep.Android.targetSdkVersion
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
    }

    buildTypes {
        release {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Dep.Compose.version
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}


dependencies {
    implementation(Dep.AndroidX.core)
    implementation(Dep.Compose.material)
    implementation(Dep.Compose.uiUtil)
    implementation(Dep.Accompanist.flowLayout)
    implementation(Dep.Accompanist.pager)
    implementation(Dep.Accompanist.insets)
    implementation(Dep.Accompanist.swiperefresh)
}