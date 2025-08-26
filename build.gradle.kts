buildscript {
    dependencies {
        classpath(libs.google.services)
        classpath(libs.firebase.crashlytics.gradle)
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.compose.multiplatform).apply(false)
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.kotlin.android).apply(false)
    alias(libs.plugins.google.ksp).apply(false)
    alias(libs.plugins.kotlin.serialization).apply(false)
    id("org.jlleitschuh.gradle.ktlint").version("12.1.1")
    alias(libs.plugins.compose.compiler).apply(false)
    alias(libs.plugins.kotlin.multiplatform).apply(false)
    alias(libs.plugins.android.kotlin.multiplatform.library).apply(false)
    alias(libs.plugins.kotlin.cocoapods).apply(false)
}
subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    ktlint {
        version = "1.0.1"
        android = true
        verbose = true
        outputToConsole = true
        outputColorName = "RED"
        filter {
            exclude { element ->
                val path = element.file.path
                path.contains("\\generated\\") || path.contains("/generated/")
            }
        }
    }
}