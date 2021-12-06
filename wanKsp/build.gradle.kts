import dependencies.Dep

plugins {
    kotlin("jvm")
}

dependencies {
    implementation(Dep.Ksp.api)
    implementation("com.squareup:kotlinpoet:1.10.2")
//    implementation("com.squareup:javapoet:1.13.0")
}


sourceSets.main {
    java.srcDirs("src/main/kotlin")
}
