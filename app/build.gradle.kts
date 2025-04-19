plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.uiappfastfood"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.uiappfastfood"
        minSdk = 24
        targetSdk = 35
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

    implementation ("com.google.android.gms:play-services-auth:21.0.0")

    implementation ("com.google.android.gms:play-services-maps:17.0.1")
    implementation ("com.google.android.gms:play-services-location:17.0.0")
    implementation ("com.google.android.libraries.places:places:3.3.0")


    implementation ("androidx.recyclerview:recyclerview:1.3.1")
    implementation(files("libs/goong_map_sdk-release.aar"))

    implementation(libs.circleImageView)
    implementation(libs.retrofit)
    implementation(libs.gson)
    implementation(libs.okhttp)
    implementation(libs.glide)
    annotationProcessor(libs.compiler)
    implementation(libs.firebase)
    implementation("com.google.firebase:firebase-core:21.1.1")
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.android.gms:play-services-location:21.0.1")
}

