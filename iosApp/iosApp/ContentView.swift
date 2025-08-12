//
//  ContentView.swift
//  iosApp
//
//  Created by Luis Miguel Antonio Andrade on 28/07/2025.
//

import UIKit
import SwiftUI
import shared
import FirebaseAuthUI

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        let mainVC = MainViewControllerKt.MainViewController(onSignInClick: {
            if let rootVC = context.coordinator.rootViewController {
                let authUI = FUIAuth.defaultAuthUI()!
                let newVC = authUI.authViewController()
                rootVC.present(newVC, animated: true, completion: nil)
            }
        })
        context.coordinator.rootViewController = mainVC
        return mainVC
    }
    
    func makeCoordinator() -> Coordinator {
        Coordinator()
    }
    
    class Coordinator {
        var rootViewController: UIViewController?
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea()
    }
}
