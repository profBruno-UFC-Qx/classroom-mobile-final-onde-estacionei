package io.github.rlimapro.ondeestacionei.ui

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object History : Screen("history")
    object Map : Screen("map")
}