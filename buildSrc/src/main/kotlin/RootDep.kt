import dependencies.Dep

object RootDep {
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Dep.Kotlin.version}"
    const val kotlinSerialization = "org.jetbrains.kotlin:kotlin-serialization:${Dep.Kotlin.version}"
    const val hiltAndroidGradlePlugin = "com.google.dagger:hilt-android-gradle-plugin:${Dep.Hilt.version}"
    const val kspGradlePlugin = "com.google.devtools.ksp:symbol-processing-gradle-plugin:${Dep.Ksp.version}"

    const val isTest = true
}