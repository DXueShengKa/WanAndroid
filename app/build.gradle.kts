import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import dependencies.Dep

plugins {
    id("com.android.application")
    id("dagger.hilt.android.plugin")

    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization")

    id("com.google.devtools.ksp")
    id("com.google.protobuf") version "0.8.17"
}

android {
    compileSdk = Dep.Android.compileSdkVersion
    buildToolsVersion = Dep.Android.buildToolsVersion

    defaultConfig {
        applicationId = "com.hc.wanandroid"
        minSdk = Dep.Android.minSdkVersion
        targetSdk = Dep.Android.targetSdkVersion
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        /* externalNativeBuild {
             cmake {
                 cppFlags += "-std=c++17"
             }
         }*/

        javaCompileOptions {
            annotationProcessorOptions {
                arguments(
                    mapOf(
                        "room.schemaLocation" to File(projectDir, "数据库结构").toString(),
                        "room.incremental" to "true",
                        "room.expandProjection" to "true"
                    )
                )
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

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

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf(
            "-Xopt-in=kotlin.RequiresOptIn"
        )
    }

    /*externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.10.2"
        }
    }*/

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Dep.Compose.version
    }

//    kotlin{
//        sourceSets.main {
//            kotlin.srcDir("build/generated/ksp/main/kotlin")
//            kotlin.srcDir("build/generated/ksp/debug/resources")
//        }
//    }
}

protobuf {

    protoc {
        artifact = Dep.Protobuf.protoc
    }

    generateProtoTasks {
        all().forEach {
            it.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }

}

dependencies {
    debugImplementation(Dep.Kotlin.reflect)
    implementation(Dep.Kotlin.serializationJson)
    implementation(Dep.Kotlin.serializationProtobufJvm)

    Dep.Compose.dependency.forEach { implementation(it) }
    debugImplementation(Dep.Compose.uiTooling)

    implementation(Dep.Hilt.hiltAndroid)
    kapt(Dep.Hilt.hiltAndroidCompiler)
    implementation(Dep.Hilt.Jetpack.hiltNavigationCompose)
    kapt(Dep.Hilt.Jetpack.hiltCompiler)

    implementation(Dep.AndroidX.startup)
    implementation(Dep.AndroidX.Camera.camera2)
    implementation(Dep.AndroidX.Camera.lifecycle)
    implementation(Dep.AndroidX.Camera.view)

    implementation(Dep.AndroidX.splashscreen)
    implementation(Dep.AndroidX.Room.roomKtx)
    implementation(Dep.AndroidX.Room.paging)
    ksp(Dep.AndroidX.Room.compiler)

    implementation(Dep.Accompanist.insets)
    implementation(Dep.Accompanist.pager)
    implementation(Dep.Accompanist.flowLayout)
    implementation(Dep.Accompanist.swiperefresh)
//    implementation(project(":androidCommonLib"))
    implementation(project(":accompanist"))
    implementation(Dep.coil)

    implementation(Dep.OkHttp.okhttp3)
    implementation(Dep.OkHttp.logging)
    implementation(Dep.Retrofit.retrofit2)
    implementation(Dep.Protobuf.javalite)

    implementation(Dep.jsoup)
    implementation(Dep.zxing)
    implementation(Dep.exoplayer)
    implementation(Dep.webrtc)


//    implementation(project(":wanKsp"))
    ksp(project(":wanKsp"))


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}