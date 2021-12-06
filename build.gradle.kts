// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        maven ("https://jitpack.io")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.3")
        classpath(RootDep.kotlinGradlePlugin)
        classpath(RootDep.kotlinSerialization)

        classpath(RootDep.kspGradlePlugin)
        classpath(RootDep.hiltAndroidGradlePlugin)
    }
}


tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}