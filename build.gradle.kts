// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false

}

buildscript {
    repositories {
        google()  // Thêm repository Google
        mavenCentral()
        maven("https://jitpack.io")
        flatDir {
            dirs("libs")  // Đảm bảo thư mục chứa các .aar là "libs"
        }
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:7.0.4")
    }
}
