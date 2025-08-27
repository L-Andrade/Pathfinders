package com.andradel.pathfinders.shared.di

import com.andradel.pathfinders.shared.auth.AuthModule
import com.andradel.pathfinders.shared.firebase.FirebaseModule
import com.andradel.pathfinders.shared.scope.CoroutinesScopesModule
import com.andradel.pathfinders.shared.serialization.SerializationModule
import com.andradel.pathfinders.shared.user.UserModule
import com.andradel.pathfinders.shared.validation.ValidationModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.ksp.generated.defaultModule
import org.koin.ksp.generated.module

fun initKoin(appDeclaration: KoinAppDeclaration? = null) {
    startKoin {
        appDeclaration?.invoke(this)
        modules(
            defaultModule,
            FirebaseModule().module,
            CoroutinesScopesModule().module,
            SerializationModule().module,
            UserModule().module,
            ValidationModule().module,
            AuthModule().module,
        )
    }
}