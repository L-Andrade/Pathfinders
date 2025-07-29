package com.andradel.pathfinders.nav

import android.net.Uri
import androidx.navigation.NavType
import androidx.savedstate.SavedState
import kotlinx.serialization.json.Json

inline fun <reified T> customNavType(
    isNullableAllowed: Boolean = false,
) = object : NavType<T>(isNullableAllowed = isNullableAllowed) {
    override fun put(bundle: SavedState, key: String, value: T) {
        bundle.putString(key, Uri.encode(Json.encodeToString(value)))
    }

    override fun get(bundle: SavedState, key: String): T? {
        return Json.decodeFromString(Uri.decode(bundle.getString(key)) ?: return null)
    }

    override fun parseValue(value: String): T {
        return Json.decodeFromString<T>(Uri.decode(value))
    }

    override fun serializeAsValue(value: T): String {
        return Uri.encode(Json.encodeToString(value))
    }
}