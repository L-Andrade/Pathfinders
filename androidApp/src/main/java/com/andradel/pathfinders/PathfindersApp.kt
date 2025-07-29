package com.andradel.pathfinders

import android.app.Application
import com.andradel.pathfinders.firebase.FirebaseModule
import com.andradel.pathfinders.scope.CoroutinesScopesModule
import com.andradel.pathfinders.serialization.SerializationModule
import com.andradel.pathfinders.user.UserModule
import com.andradel.pathfinders.validation.ValidationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.ksp.generated.defaultModule
import org.koin.ksp.generated.module

class PathfindersApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PathfindersApp)
            modules(
                defaultModule,
                FirebaseModule().module,
                CoroutinesScopesModule().module,
                SerializationModule().module,
                UserModule().module,
                ValidationModule().module,
            )
        }
    }
}