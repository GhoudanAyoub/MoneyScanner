import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.exclude

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.com.hilt)
    kotlin("plugin.serialization") version "1.9.22"

}

android {
    namespace = "com.chari.money"
    compileSdk = 34

    defaultConfig {


        applicationId = "com.chari.money"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
        versionName = "2.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.hilt)
    annotationProcessor(libs.hilt.compiler)

    implementation(libs.kotlinx.serialization)
    implementation(libs.kotlinx.serialization.converter)
    implementation(libs.retrofit)
    implementation(libs.ok2curl)
    implementation(libs.logging.interceptor)
    implementation(libs.viewmodel)
    implementation(libs.liveData)
    implementation(libs.fragment)
    implementation(libs.ui)
    implementation(libs.feature)
    implementation(libs.coroutines)


    implementation("org.apache.httpcomponents:httpmime:4.3.6") {
        exclude(module = "httpclient")
    }
    implementation("org.bouncycastle:bcprov-jdk15on:1.65") {
        exclude(group = "org.bouncycastle", module = "bcprov-jdk15on")
    }
    implementation("org.apache.httpcomponents:httpclient-android:4.3.5")
    implementation("com.android.volley:volley:1.2.1")

    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation ("com.google.android.material:material:1.1.0")
    implementation ("androidx.constraintlayout:constraintlayout:1.1.3")

    implementation(files("libs/SkyIDSDK-1.8.1.0@Core-1.8.9.1@21-09-2023.aar"))








    implementation("androidx.lifecycle:lifecycle-viewmodel:2.4.0")


//    implementation("com.vove.sdk:vove-sdk:0.3.1")
    implementation("com.github.VOVE-ID:vove-id-android:0.3.4")

}
