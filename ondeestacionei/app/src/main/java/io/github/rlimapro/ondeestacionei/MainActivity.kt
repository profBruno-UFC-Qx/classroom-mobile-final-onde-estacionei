package io.github.rlimapro.ondeestacionei

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.rlimapro.ondeestacionei.ui.ParkingViewModel
import io.github.rlimapro.ondeestacionei.ui.Screen
import io.github.rlimapro.ondeestacionei.ui.screen.HistoryScreen
import io.github.rlimapro.ondeestacionei.ui.screen.MainScreen
import io.github.rlimapro.ondeestacionei.ui.screen.MapScreen
import io.github.rlimapro.ondeestacionei.ui.theme.OndeEstacioneiTheme
import androidx.compose.runtime.LaunchedEffect

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            OndeEstacioneiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ParkingApp(navigateTo = intent.getStringExtra("navigate_to"))
                }
            }
        }
    }
}

@Composable
fun ParkingApp(navigateTo: String? = null) {
    val navController = rememberNavController()
    val viewModel: ParkingViewModel = viewModel()

    LaunchedEffect(navigateTo) {
        if (navigateTo == "map") {
            navController.navigate(Screen.Map.route) {
                popUpTo(Screen.Main.route) { inclusive = false }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Main.route,
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) },
        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left) },
        popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right) },
        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right) }
    ) {
        composable(Screen.Main.route) {
            MainScreen(
                viewModel = viewModel,
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onNavigateToMap = {
                    navController.navigate(Screen.Map.route)
                }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Map.route) {
            MapScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
fun AnimatedContentTransitionScope<*>.slideIntoContainer(
    direction: AnimatedContentTransitionScope.SlideDirection,
    animationSpec: FiniteAnimationSpec<IntOffset> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
): EnterTransition {
    return slideIntoContainer(towards = direction, animationSpec = animationSpec)
}

@OptIn(ExperimentalAnimationApi::class)
fun AnimatedContentTransitionScope<*>.slideOutOfContainer(
    direction: AnimatedContentTransitionScope.SlideDirection,
    animationSpec: FiniteAnimationSpec<IntOffset> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )): ExitTransition {
    return slideOutOfContainer(towards = direction, animationSpec = animationSpec)
}
