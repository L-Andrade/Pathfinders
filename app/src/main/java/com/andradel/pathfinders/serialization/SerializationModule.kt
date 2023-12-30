package com.andradel.pathfinders.serialization

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SerializationModule {
    @OptIn(ExperimentalSerializationApi::class)
    @Singleton
    @Provides
    fun providesJson(): Json = Json {
        explicitNulls = false
        ignoreUnknownKeys = true
        decodeEnumsCaseInsensitive = true
    }
}