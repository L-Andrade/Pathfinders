package com.andradel.pathfinders.shared.nav

import androidx.navigation.NavType
import androidx.savedstate.SavedState
import androidx.savedstate.read
import androidx.savedstate.write
import com.eygraber.uri.Uri
import kotlinx.serialization.json.Json

inline fun <reified T> customNavType(
    isNullableAllowed: Boolean = false,
) = object : NavType<T>(isNullableAllowed = isNullableAllowed) {
    override fun put(bundle: SavedState, key: String, value: T) {
        bundle.write { putString(key, Uri.encode(Json.encodeToString(value))) }
    }

    override fun get(bundle: SavedState, key: String): T? {
        return Json.decodeFromString(Uri.decode(bundle.read { getString(key) }))
    }

    override fun parseValue(value: String): T {
        return Json.decodeFromString<T>(Uri.decode(value))
    }

    override fun serializeAsValue(value: T): String {
        return Uri.encode(Json.encodeToString(value))
    }
}