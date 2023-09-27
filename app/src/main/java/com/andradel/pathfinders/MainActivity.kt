package com.andradel.pathfinders

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.unit.IntOffset
import com.andradel.pathfinders.features.NavGraphs
import com.andradel.pathfinders.ui.theme.PathfindersTheme
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setContent {
            PathfindersTheme {
                val animationSpec = spring(
                    stiffness = Spring.StiffnessMediumLow,
                    visibilityThreshold = IntOffset.VisibilityThreshold
                )
                DestinationsNavHost(
                    navGraph = NavGraphs.root,
                    engine = rememberAnimatedNavHostEngine(
                        rootDefaultAnimations = RootNavGraphDefaultAnimations(
                            enterTransition = { slideInHorizontally(animationSpec) { size -> size } },
                            exitTransition = { slideOutHorizontally(animationSpec) { size -> -size } },
                            popEnterTransition = { slideInHorizontally(animationSpec) { size -> -size } },
                            popExitTransition = { slideOutHorizontally(animationSpec) { size -> size } },
                        )
                    )
                )
            }
        }
    }
}