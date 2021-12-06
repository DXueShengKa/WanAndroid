package dependencies

object Dep {
    object Version {

        const val okhttp = "4.9.0"
    }

    const val desugarJdkLibs = "com.android.tools:desugar_jdk_libs:1.1.5"

    object Kotlin {
        const val version = "1.6.0"

        /**
         * 协程
         */
        private const val coroutinesVersion = "1.5.2"
        private const val serializationVersion = "1.3.1"

        const val stdlibJdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${version}"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:${version}"

        const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${coroutinesVersion}"
        const val coroutinesRx2 = "org.jetbrains.kotlinx:kotlinx-coroutines-rx2:${coroutinesVersion}"
        const val coroutinesJdk8 = "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${coroutinesVersion}"

        const val serializationJson = "org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion"
        const val serializationProtobufJvm = "org.jetbrains.kotlinx:kotlinx-serialization-protobuf-jvm:$serializationVersion"
    }

    object Android {
        const val compileSdkVersion = 31
        const val minSdkVersion = 26
        const val targetSdkVersion = 31
        const val buildToolsVersion = "31.0.0"
    }

    object Test {
        const val junit = "junit:junit:4.13.2"
    }

    object AndroidX {
        /**
         * 兼容包
         */
        const val appcompat = "androidx.appcompat:appcompat:1.3.1"
        const val material = "com.google.android.material:material:1.2.1"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.4"
        const val cardView = "androidx.cardview:cardview:1.0.0"
        const val recyclerView = "androidx.recyclerview:recyclerview:1.2.0"
        const val fragmentKtx = "androidx.fragment:fragment-ktx:1.3.3"
        const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"
        const val annotation = "androidx.annotation:annotation:1.1.0"
        const val paging = "androidx.paging:paging-runtime-ktx:3.1.0-alpha04"
        const val core = "androidx.core:core:1.7.0"
        const val dynamicAnimation = "androidx.dynamicanimation:dynamicanimation-ktx:1.0.0"
        const val startup = "androidx.startup:startup-runtime:1.0.0"
        const val splashscreen = "androidx.core:core-splashscreen:1.0.0-alpha02"


        object Room {
            private const val version = "2.4.0-rc01"
            const val roomKtx = "androidx.room:room-ktx:$version"
            const val paging = "androidx.room:room-paging:$version"
            const val compiler = "androidx.room:room-compiler:$version"
            const val testing = "androidx.room:room-testing:$version"
        }

        object Camera {
            private const val version = "1.1.0-alpha10"
            const val camera2 = "androidx.camera:camera-camera2:$version"
            const val lifecycle = "androidx.camera:camera-lifecycle:$version"
            const val view = "androidx.camera:camera-view:1.0.0-alpha27"
        }

        object Test {
            const val runner = "androidx.test:runner:1.3.0"
            const val extJunit = "androidx.test.ext:junit:1.1.2"
            const val espresso = "androidx.test.espresso:espresso-core:3.3.0"
        }
    }

    object Compose {
        const val version = "1.1.0-beta04"
        const val runtimeLiveData = "androidx.compose.runtime:runtime-livedata:$version"
        const val material = "androidx.compose.material:material:$version"
        const val ui = "androidx.compose.ui:ui:$version"
        const val foundation = "androidx.compose.foundation:foundation:$version"
        const val uiTooling = "androidx.compose.ui:ui-tooling:$version"
        const val uiUtil = "androidx.compose.ui:ui-util:$version"
        const val material3 = "androidx.compose.material3:material3:1.0.0-alpha02"

        const val pagingCompose = "androidx.paging:paging-compose:1.0.0-alpha14"
        const val navigationCompose = "androidx.navigation:navigation-compose:2.4.0-beta02"

        const val constraintLayoutCompose = "androidx.constraintlayout:constraintlayout-compose:1.0.0-rc02"
        const val activityCompose = "androidx.activity:activity-compose:1.4.0"

        const val viewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:2.4.0"

        @JvmField
        val dependency = arrayOf(
            runtimeLiveData, material, ui, foundation, uiUtil, material3,
            pagingCompose, navigationCompose, constraintLayoutCompose, activityCompose,
            viewModelCompose
        )
    }


    object Accompanist {
        private const val version = "0.21.4-beta"
        const val flowLayout = "com.google.accompanist:accompanist-flowlayout:$version"
        const val pager = "com.google.accompanist:accompanist-pager:$version"
        const val insets = "com.google.accompanist:accompanist-insets:$version"
        const val swiperefresh = "com.google.accompanist:accompanist-swiperefresh:$version"
    }

    object OkHttp {
        const val okhttp3 = "com.squareup.okhttp3:okhttp:${Version.okhttp}"
        const val logging = "com.squareup.okhttp3:logging-interceptor:${Version.okhttp}"
    }


    object Retrofit {
        const val version = "2.9.0"
        const val retrofit2 = "com.squareup.retrofit2:retrofit:${version}"
        const val gsonConverter = "com.squareup.retrofit2:converter-gson:${version}"
    }

    object Hilt {
        const val version = "2.40.3"
        const val hiltAndroid = "com.google.dagger:hilt-android:$version"
        const val hiltAndroidCompiler = "com.google.dagger:hilt-android-compiler:$version"

        object Jetpack {
            private const val version = "1.0.0"
            const val hiltCompiler = "androidx.hilt:hilt-compiler:$version"
            const val hiltWork = "androidx.hilt:hilt-work:$version"
            const val hiltNavigation = "androidx.hilt:hilt-navigation:$version"
            const val hiltNavigationCompose = "androidx.hilt:hilt-navigation-compose:1.0.0-beta01"
        }
    }

    object Protobuf {
        const val version = "3.17.3"
        const val protoc = "com.google.protobuf:protoc:$version"
        const val javalite = "com.google.protobuf:protobuf-javalite:$version"
        const val javaUtil = "com.google.protobuf:protobuf-java-util:$version"
        const val kotlinLite = "com.google.protobuf:protobuf-kotlin-lite:$version"
    }

    object Ksp {
        const val version = "${Kotlin.version}-1.0.1"
        const val api = "com.google.devtools.ksp:symbol-processing-api:$version"
    }

    const val jsoup = "org.jsoup:jsoup:1.14.2"

    const val zxing = "com.google.zxing:core:3.4.1"
    const val exoplayer = "com.google.android.exoplayer:exoplayer:2.16.0"
    const val webrtc = "org.webrtc:google-webrtc:1.0.32006"
    const val gson = "com.google.code.gson:gson:2.8.5"
    const val coil = "io.coil-kt:coil-compose:1.4.0"

}