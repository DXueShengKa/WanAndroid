dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.aliyun.com/repository/public")
    }
}

pluginManagement{
    repositories {
        gradlePluginPortal()
    }
}

rootProject.name = "WanAndroid"
include(":app",/*":androidCommonLib",*/":accompanist")
include(":wanKsp")
