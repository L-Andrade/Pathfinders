plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.application")
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.dagger.hilt.android")
    id("kotlinx-serialization")
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
    sourceSets {
        commonMain.dependencies {
            implementation(compose.ui)
            implementation(compose.material3)
            // implementation(libs.compose.backhandler)
            // implementation(libs.androidx.lifecycle.runtime.ktx)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            // implementation(libs.androidx.hilt.navigation.compose)

            // Serialization
            implementation(libs.kotlinx.serialization.json)
        }
        androidMain.dependencies {
            implementation(compose.ui)
            implementation(compose.material3)
            implementation(libs.compose.backhandler)
            implementation(libs.androidx.lifecycle.runtime.ktx)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.hilt.navigation.compose)

            // Serialization
            implementation(libs.kotlinx.serialization.json)

            // Android specific?
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.activity.compose)
            implementation(libs.lottie.compose)

            // Firebase
            implementation(libs.firebase.ui.auth)
            implementation(libs.firebase.analytics)
            implementation(libs.firebase.database)
            implementation(libs.firebase.crashlytics)
            implementation(libs.firebase.functions)
            implementation(libs.firebase.messaging)

            // Nav
            implementation(libs.compose.destinations.animations)

            // Hilt
            implementation(libs.hilt.android)
        }
    }
}

android {
    namespace = "com.andradel.pathfinders"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.andradel.pathfinders"
        minSdk = 26
        targetSdk = 35
        versionCode = 4
        versionName = "2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            storeFile = rootProject.file("release.jks")
            storePassword = if(project.hasProperty("storePassword")) project.property("storePassword") as String else System.getenv("PATHFINDERS_STORE_PASSWD")
            keyAlias = "key0"
            keyPassword = if(project.hasProperty("keyPassword")) project.property("keyPassword") as String else System.getenv("PATHFINDERS_KEY_PASSWD")
        }
    }
    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
        }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }
    flavorDimensions += "version"
    productFlavors {
        create("pathfinders") {
            dimension = "version"
            versionNameSuffix = "-pathfinders"
        }
        create("unit") {
            dimension = "version"
            applicationIdSuffix = ".unit"
            versionNameSuffix = "-unit"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(platform(libs.firebase.bom))
    ksp(libs.compose.destinations.ksp)
    ksp(libs.hilt.compiler)
}