//
//  iosAppApp.swift
//  iosApp
//
//  Created by Luis Miguel Antonio Andrade on 28/07/2025.
//

import SwiftUI
import shared

@main
struct iosAppApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
