package com.andradel.pathfinders

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.andradel.pathfinders.features.NavGraphs
import com.andradel.pathfinders.ui.theme.PathfindersTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setContent {
            PathfindersTheme {
                DestinationsNavHost(navGraph = NavGraphs.root)
            }
        }
    }
}