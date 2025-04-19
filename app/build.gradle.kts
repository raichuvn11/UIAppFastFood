plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.uiappfastfood"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.uiappfastfood"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
    buildFeatures {
        viewBinding = true
    }
}


dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("me.relex:circleindicator:2.1.6")
    implementation ("com.google.android.flexbox:flexbox:3.0.0")
    implementation ("com.tbuonomo:dotsindicator:4.3")
    implementation ("com.google.android.material:material:1.10.0")
    implementation ("androidx.cardview:cardview:1.0.0")
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")

    implementation ("com.squareup.okhttp3:okhttp:4.12.0")
    implementation ("com.squareup.retrofit2:retrofit:2.11.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.11.0")

    implementation ("com.google.android.gms:play-services-auth:21.0.0")

    implementation ("com.google.android.gms:play-services-maps:17.0.1")
    implementation ("com.google.android.gms:play-services-location:17.0.0")
    implementation ("com.google.android.libraries.places:places:3.3.0")


    implementation ("androidx.recyclerview:recyclerview:1.3.1")
    implementation(files("libs/goong_map_sdk-release.aar"))

}