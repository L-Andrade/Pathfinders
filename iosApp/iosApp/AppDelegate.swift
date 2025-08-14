//
// Created by Luis Miguel Antonio Andrade on 30/07/2025.
//

import Foundation
import UIKit
import shared
import FirebaseAuth
import FirebaseAuthUI
import FirebaseEmailAuthUI
import FirebaseGoogleAuthUI
import GoogleSignIn

class AppDelegate: NSObject, UIApplicationDelegate, FUIAuthDelegate {

    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        KoinInitializerKt.doInitKoin()
        FirebaseInitializerKt.onDidFinishLaunchingWithOptions()
        
        let actionCodeSettings = ActionCodeSettings()
        actionCodeSettings.handleCodeInApp = true
        let authUI = FUIAuth.defaultAuthUI()!
        let provider = FUIEmailAuth(authAuthUI: authUI,signInMethod: EmailPasswordAuthSignInMethod,forceSameDevice: false,allowNewEmailAccounts: true,actionCodeSetting: actionCodeSettings)
        authUI.providers = [provider, FUIGoogleAuth.init(authUI: authUI)]
        authUI.delegate = self
        return true
    }
    
    func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
        let sourceApplication = options[UIApplication.OpenURLOptionsKey.sourceApplication] as? String
        if FUIAuth.defaultAuthUI()?.handleOpen(url, sourceApplication: sourceApplication) ?? false {
            return true
        }
        if GIDSignIn.sharedInstance.handle(url) {
            return true
        }

        return false
    }
    
    func authUI(_ authUI: FUIAuth, didSignInWith authDataResult: AuthDataResult?, error: Error?) {
        UserSessionHelper().userSession.updateUser()
    }
}
