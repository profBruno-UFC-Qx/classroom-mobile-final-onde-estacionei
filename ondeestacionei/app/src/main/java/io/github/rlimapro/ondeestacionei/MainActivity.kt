package io.github.rlimapro.ondeestacionei

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OndeEstacioneiTheme {
                val navController = rememberNavController()
                val viewModel: ParkingViewModel = viewModel()

                NavHost(navController = navController, startDestination = Screen.Main.route) {
                    composable(Screen.Main.route) {
                        MainScreen(
                            viewModel = viewModel,
                            onNavigateToHistory = { navController.navigate(Screen.History.route) },
                            onNavigateToMap = { navController.navigate(Screen.Map.route) }
                        )
                    }

                    composable(Screen.History.route) {
                        HistoryScreen(viewModel = viewModel)
                    }

                    composable(Screen.Map.route) {
                        MapScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
