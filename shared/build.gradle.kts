import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.ksp)
    id("kotlinx-serialization")
    alias(libs.plugins.kotlin.cocoapods)
}

kotlin {
    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
        namespace = "com.andradel.pathfinders.shared"
        compileSdk = 36
        minSdk = 26

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
        androidResources.enable = true
    }

    // For iOS targets, this is also where you should
    // configure native binary output. For more information, see:
    // https://kotlinlang.org/docs/multiplatform-build-native-binaries.html#build-xcframeworks

    // A step-by-step guide on how to include this library in an XCode
    // project can be found here:
    // https://developer.android.com/kotlin/multiplatform/migrate
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Pathfinders"
        version = "1.0"
        homepage = "https://github.com/L-Andrade/Pathfinders/"
        ios.deploymentTarget = "16.4"
        framework {
            baseName = "shared"
            isStatic = true
        }
        pod("FirebaseCore") {
            version = "~> 11.13"
            linkOnly = true
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
        pod("FirebaseFunctions") {
            version = "~> 11.13"
            linkOnly = true
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
        pod("FirebaseCrashlytics") {
            version = "~> 11.13"
            linkOnly = true
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
        pod("FirebaseUI") {
            version = "~> 15.1.0"
            linkOnly = true
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
        // Maps custom Xcode configuration to NativeBuildType
        xcodeConfigurationToNativeBuildType["CUSTOM_DEBUG"] = NativeBuildType.DEBUG
        xcodeConfigurationToNativeBuildType["CUSTOM_RELEASE"] = NativeBuildType.RELEASE
    }

    // Source set declarations.
    // Declaring a target automatically creates a source set with the same name. By default, the
    // Kotlin Gradle Plugin creates additional source sets that depend on each other, since it is
    // common to share sources between related targets.
    // See: https://kotlinlang.org/docs/multiplatform-hierarchy.html
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.koin.annotations)
                implementation(libs.koin.compose.viewmodel)

                // Serialization
                implementation(libs.kotlinx.serialization)

                implementation(libs.kotlinx.datetime)

                // Nav
                implementation(libs.compose.navigation)

                // TODO?
                // implementation(libs.lottie.compose)

                // Compose
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.materialIconsExtended)
                implementation(compose.components.uiToolingPreview)

                implementation(libs.ui.backhandler)

                // Firebase
                implementation(libs.firebase.database)
                implementation(libs.firebase.crashlytics)
                implementation(libs.firebase.functions)
                implementation(libs.firebase.messaging)
                implementation(libs.firebase.auth)

                implementation(libs.uri.kmp)

                implementation(compose.components.resources)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                // Add Android-specific dependencies here. Note that this source set depends on
                // commonMain by default and will correctly pull the Android artifacts of any KMP
                // dependencies declared in commonMain.
                implementation(libs.firebase.ui.auth)

                implementation(libs.koin.android)
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.junit)
            }
        }

        iosMain {
            dependencies {
                // Add iOS-specific dependencies here. This a source set created by Kotlin Gradle
                // Plugin (KGP) that each specific iOS target (e.g., iosX64) depends on as
                // part of KMP’s default source set hierarchy. Note that this source set depends
                // on common by default and will correctly pull the iOS artifacts of any
                // KMP dependencies declared in commonMain.
            }
        }
    }
    sourceSets.named("commonMain").configure {
        kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
    }
}

dependencies {
    add("kspCommonMainMetadata", libs.koin.ksp)
    // add("kspAndroid", libs.koin.ksp)
    // add("kspIosX64", libs.koin.ksp)
    // add("kspIosArm64", libs.koin.ksp)
    // add("kspIosSimulatorArm64", libs.koin.ksp)

    androidMainImplementation(platform(libs.firebase.bom))
}

project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
    if(name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

compose.resources {
    publicResClass = true
    generateResClass = always
}

ksp {
    arg("KOIN_USE_COMPOSE_VIEWMODEL", "true")
    arg("KOIN_CONFIG_CHECK", "true")
}