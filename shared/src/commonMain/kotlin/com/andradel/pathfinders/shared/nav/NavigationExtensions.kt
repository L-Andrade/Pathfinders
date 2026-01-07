package com.andradel.pathfinders.shared.nav

inline fun <reified T> Navigator.navigateBackWithResult(key: String, value: T) {
    resultStore.setResult(key, value)
    goBack()
}