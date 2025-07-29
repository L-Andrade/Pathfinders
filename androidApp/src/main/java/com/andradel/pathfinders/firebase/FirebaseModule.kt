package com.andradel.pathfinders.firebase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.database.FirebaseDatabase
import dev.gitlive.firebase.database.database
import dev.gitlive.firebase.functions.FirebaseFunctions
import dev.gitlive.firebase.functions.functions
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.andradel.pathfinders.firebase**")
class FirebaseModule {
    @Single
    fun providesFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Single
    fun providesFirebaseDb(): FirebaseDatabase = Firebase.database

    @Single
    fun providesFirebaseFunctions(): FirebaseFunctions = Firebase.functions
}