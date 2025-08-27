package com.andradel.pathfinders.shared.firebase

import cocoapods.FirebaseCore.FIRApp
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
fun onDidFinishLaunchingWithOptions() {
    FIRApp.configure()
}