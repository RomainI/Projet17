import com.android.build.gradle.BaseExtension
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("org.sonarqube") version "6.0.1.5171"
    id("jacoco")

    id("com.google.firebase.appdistribution")
}

tasks.withType<Test> {
    extensions.configure(JacocoTaskExtension::class) {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

val keystorePropertiesFile = rootProject.file("app/keystore.properties")
val keystoreProperties = Properties()

if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

//sonar {
//    properties {
//        property("sonar.projectKey", "RomainI_Projet17")
//        property("sonar.organization", "romaini")
//        property("sonar.host.url", "https://sonarcloud.io")
//    }
//}
android {
    namespace = "com.openclassrooms.rebonnte"
    compileSdk = 34

    signingConfigs {
        create("release") {
            if (keystoreProperties["storeFile"] != null) {
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
            }


        }
    }
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
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            firebaseAppDistribution {
                serviceCredentialsFile = project.rootProject.file("app/firebase-adminsdk.json").toString()
                appId ="1:11455656299:android:f8adb157aecd7cf2751c2e"
                releaseNotes = "Release notes for full version"
                testers = "romain.ilardi@gmail.com"
            }
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

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
var SONAR_API_KEY : String
if (localPropertiesFile.exists()) {
    FileInputStream(localPropertiesFile).use { stream ->
        localProperties.load(stream)
    }
    SONAR_API_KEY = localProperties["sonar.login"]?.toString().toString()
} else {
    SONAR_API_KEY =""
}

val androidExtension = extensions.getByType<BaseExtension>()

val jacocoTestReport by tasks.registering(JacocoReport::class) {
    dependsOn("testDebugUnitTest", "createDebugCoverageReport")
    group = "Reporting"
    description = "Generate Jacoco coverage reports"

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    val debugTree = fileTree("${buildDir}/tmp/kotlin-classes/debug")
    val mainSrc = androidExtension.sourceSets.getByName("main").java.srcDirs

    classDirectories.setFrom(debugTree)
    sourceDirectories.setFrom(files(mainSrc))
    executionData.setFrom(fileTree(buildDir) {
        include("**/*.exec", "**/*.ec")
    })
}

sonarqube {
    properties {
        property("sonar.projectKey", "RomainI_Projet17")
        property("sonar.organization", "romaini")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.login", SONAR_API_KEY)

        property("sonar.sources", "src/main/java")
        property("sonar.tests", "src/test/java")
        property("sonar.kotlin.detekt.reportPaths", "$buildDir/reports/detekt/detekt-report.xml") // Si vous utilisez Detekt
        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.coverage.jacoco.xmlReportPaths", "$buildDir/reports/jacoco/testDebugUnitTestCoverage/testDebugUnitTestCoverage.xml")
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.appcompat:appcompat:1.6.1")



    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
//    implementation("androidx.compose.material3:material3:1.1.0-alpha05")
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
    implementation(libs.androidx.material3.android)
//    implementation(libs.firebase.storage.ktx)
    ksp("com.google.dagger:hilt-compiler:2.48")

    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("io.coil-kt:coil-compose:2.7.0")


    //Camera X to capture barcode

    implementation ("androidx.camera:camera-core:1.4.1")
    implementation ("androidx.camera:camera-camera2:1.4.1")
    implementation ("androidx.camera:camera-lifecycle:1.4.1")
    implementation ("androidx.camera:camera-view:1.4.1")

    // JUnit
    testImplementation ("junit:junit:4.13.2")
    testImplementation ("org.junit.jupiter:junit-jupiter:5.8.1")

    // Coroutines Test
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")

    // MockK pour mocker les dépendances
    testImplementation ("io.mockk:mockk:1.12.0")


//    //distribution
//    implementation("com.google.firebase:firebase-appdistribution:17.0.1")

}