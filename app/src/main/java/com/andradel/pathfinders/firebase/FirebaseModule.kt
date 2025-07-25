package com.andradel.pathfinders.firebase

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.functions
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.andradel.pathfinders.firebase**")
class FirebaseModule {
    @Single
    fun providesFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Single
    fun providesFirebaseDb(): FirebaseDatabase = Firebase.database

    @Single
    fun providesFirebaseFunctions(): FirebaseFunctions = Firebase.functions
}