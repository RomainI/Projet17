import com.android.build.gradle.BaseExtension
import org.gradle.api.tasks.testing.Test
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("org.sonarqube")
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
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            firebaseAppDistribution {
                serviceCredentialsFile = project.rootProject.file("app/firebase-adminsdk.json").toString()
                appId = "1:11455656299:android:f8adb157aecd7cf2751c2e"
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

    // Configuration de Lint pour générer un rapport XML à un emplacement spécifique
//    lint {
//        xmlOutput = file("$buildDir/reports/lint-results.xml")
//    }
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
var SONAR_API_KEY: String
if (localPropertiesFile.exists()) {
    FileInputStream(localPropertiesFile).use { stream ->
        localProperties.load(stream)
    }
    SONAR_API_KEY = localProperties["sonar.login"]?.toString().toString()
} else {
    SONAR_API_KEY = ""
}

val androidExtension = extensions.getByType<BaseExtension>()

fun getJacocoExecutionData(): FileCollection {
    return files(
        project.tasks.withType<Test>().mapNotNull { task ->
            task.extensions.findByType(JacocoTaskExtension::class.java)?.destinationFile
        }
    )
}
jacoco {
    toolVersion = "0.8.8" //force une autre version. Mettre une version plus vielle, puis tester, puis une plus récente
}

val jacocoTestReport by tasks.registering(JacocoReport::class) {
    dependsOn(tasks.withType<Test>()) // Dépend de TOUTES les tâches de test
    group = "Reporting"
    description = "Generate Jacoco coverage reports"

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    classDirectories.setFrom(
        fileTree("$buildDir/tmp/kotlin-classes/debug") {
            // Optionnel : exclure les classes générées automatiquement si nécessaire
            exclude("**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*")
        }
    )
    sourceDirectories.setFrom(files(androidExtension.sourceSets.getByName("main").java.srcDirs))
    executionData.setFrom(getJacocoExecutionData()) // Utilise la fonction pour les fichiers d'exécution

}

sonarqube {
    properties {
        property("sonar.projectKey", "RomainI_Projet17")
        property("sonar.organization", "romaini")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.token", SONAR_API_KEY) // Utilisez sonar.token au lieu de sonar.login
        property("sonar.sources", "src/main/java")
        property("sonar.tests", "src/test/java")
        //  property("sonar.kotlin.detekt.reportPaths", "$buildDir/reports/detekt/detekt-report.xml") // Si vous utilisez Detekt
        //property("sonar.androidLint.reportPaths", "$buildDir/reports/lint-results.xml") // Utilisez le chemin configuré pour Lint

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
    implementation("androidx.navigation:navigation-compose:2.7.0")
    implementation("androidx.compose.material:material:1.7.7")

    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation(libs.google.firebase.firestore.ktx)
    implementation(libs.google.firebase.auth.ktx)
    implementation(libs.firebase.ui.auth)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.firestore.ktx)

    implementation("com.google.dagger:hilt-android:2.48")
    implementation(libs.firebase.storage.ktx)
    implementation(libs.androidx.material3.android)
    ksp("com.google.dagger:hilt-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("io.coil-kt:coil-compose:2.7.0")

    //Camera X to capture barcode
    implementation("androidx.camera:camera-core:1.4.1")
    implementation("androidx.camera:camera-camera2:1.4.1")
    implementation("androidx.camera:camera-lifecycle:1.4.1")
    implementation("androidx.camera:camera-view:1.4.1")

    // JUnit  (Gardez LES DEUX)
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("junit:junit:4.13.2")


    // Coroutines Test
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")

    // MockK pour mocker les dépendances
    testImplementation("io.mockk:mockk:1.12.0")
}
configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.bouncycastle") {
            useVersion("1.70")
        }
    }
}