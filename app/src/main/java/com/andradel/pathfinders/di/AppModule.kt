package com.andradel.pathfinders.di

import com.andradel.pathfinders.data.DataHolder
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
class AppModule {
    @Factory
    fun dataHolder(): DataHolder = DataHolder("Hello, Koin!")
}