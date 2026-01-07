package com.andradel.pathfinders.shared.nav

import androidx.navigation3.runtime.NavKey

class Navigator(val state: NavigationState, val resultStore: ResultStore) {
    fun navigate(route: NavKey) {
        state.backStack.add(route)
    }

    fun goBack() {
        if (state.backStack.size > 1) {
            state.backStack.removeLast()
        }
    }
}