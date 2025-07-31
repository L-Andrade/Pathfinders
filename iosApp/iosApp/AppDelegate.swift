//
// Created by Luis Miguel Antonio Andrade on 30/07/2025.
//

import Foundation
import UIKit
import shared

class AppDelegate: NSObject, UIApplicationDelegate {

    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        KoinInitializerKt.doInitKoin()
        FirebaseInitializerKt.onDidFinishLaunchingWithOptions()
        return true
    }
}
