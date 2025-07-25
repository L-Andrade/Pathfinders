package com.andradel.pathfinders.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class SerializationModule {
    @OptIn(ExperimentalSerializationApi::class)
    @Single
    fun providesJson(): Json = Json {
        explicitNulls = false
        ignoreUnknownKeys = true
        decodeEnumsCaseInsensitive = true
    }
}