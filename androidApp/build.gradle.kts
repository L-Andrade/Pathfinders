plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("kotlinx-serialization")
}

val projectVersionCode: String by project
val projectVersionName: String by project

android {
    namespace = "com.andradel.pathfinders"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.andradel.pathfinders"
        minSdk = 26
        targetSdk = 35
        versionCode = projectVersionCode.toIntOrNull()
        versionName = projectVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            storeFile = rootProject.file("release.jks")
            storePassword = if (project.hasProperty("storePassword")) project.property("storePassword").toString() else System.getenv("PATHFINDERS_STORE_PASSWD")
            keyAlias = "key0"
            keyPassword = if (project.hasProperty("keyPassword")) project.property("keyPassword").toString() else System.getenv("PATHFINDERS_KEY_PASSWD")
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }
        release {
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
    kotlinOptions {
        jvmTarget = "17"
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
    implementation(project(":shared"))

    implementation(libs.koin.android)
    implementation(libs.koin.annotations)
    implementation(libs.koin.androidx.compose)
    ksp(libs.koin.ksp)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.material3)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.preview)
    debugImplementation(libs.compose.ui.manifest)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.ui.auth)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.messaging)
}