plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.openclassrooms.rebonnte"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.openclassrooms.rebonnte"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    packaging.resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.appcompat:appcompat:1.6.1")



    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.1.0-alpha05")
    implementation("androidx.navigation:navigation-compose:2.7.0")
    implementation ("androidx.compose.material:material:1.7.7")



    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))

    implementation(libs.google.firebase.firestore.ktx)

    implementation(libs.google.firebase.auth.ktx)

    implementation (libs.firebase.ui.auth)

    implementation(libs.firebase.analytics.ktx)

//    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
//    implementation("com.google.firebase:firebase-analytics")
    implementation(libs.firebase.firestore.ktx)
//    implementation (libs.firebase.ui.auth)
//    implementation(libs.firebase.ui.storage)
//    implementation(libs.firebase.ui.firestore)
//    implementation(libs.firebase.analytics)
//    implementation(libs.firebase.messaging)


    implementation("com.google.dagger:hilt-android:2.48")
    implementation(libs.firebase.storage.ktx)
//    implementation(libs.firebase.storage.ktx)
    ksp("com.google.dagger:hilt-compiler:2.48")

    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("io.coil-kt:coil-compose:2.7.0")

}