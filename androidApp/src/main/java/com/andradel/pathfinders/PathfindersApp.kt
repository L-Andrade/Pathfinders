package com.andradel.pathfinders

import android.app.Application
import com.andradel.pathfinders.shared.di.initKoin
import org.koin.android.ext.koin.androidContext

class PathfindersApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@PathfindersApp)
        }
    }
}