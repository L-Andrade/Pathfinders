package com.andradel.pathfinders.shared.firebase

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module
@ComponentScan("com.andradel.pathfinders.firebase**")
class FirebaseModule {
    // TODO
//     @Single
//     fun providesFirebaseAuth(): FirebaseAuth = Firebase.auth
//
//     @Single
//     fun providesFirebaseDb(): FirebaseDatabase = Firebase.database
//
//     @Single
//     fun providesFirebaseFunctions(): FirebaseFunctions = Firebase.functions
}