
package com.agritrack.native

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.agritrack.native.data.database.AppDatabase
import com.agritrack.native.ui.screens.detail.HarvestDetailScreen
import com.agritrack.native.ui.screens.detail.HarvestDetailViewModel
import com.agritrack.native.ui.screens.home.HomeScreen
import com.agritrack.native.ui.screens.home.MainViewModel
import com.agritrack.native.ui.theme.AgriTrackNativeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización de la base de datos y el DAO
        val database = AppDatabase.getDatabase(applicationContext)
        val harvestDao = database.harvestDao()

        setContent {
            AgriTrackNativeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(harvestDao = harvestDao)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(harvestDao: com.agritrack.native.data.database.HarvestDao) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        // Ruta para la pantalla principal (Lista de Cosechas)
        composable("home") {
            val mainViewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(harvestDao)
            )
            HomeScreen(
                viewModel = mainViewModel,
                onHarvestClick = { harvestId ->
                    navController.navigate("harvestDetail/$harvestId")
                }
            )
        }

        // Ruta para la pantalla de detalles de una cosecha
        composable(
            route = "harvestDetail/{harvestId}",
            arguments = listOf(navArgument("harvestId") { type = NavType.IntType })
        ) { backStackEntry ->
            val harvestId = backStackEntry.arguments?.getInt("harvestId") ?: 0
            val detailViewModel: HarvestDetailViewModel = viewModel(
                factory = HarvestDetailViewModelFactory(harvestDao, harvestId)
            )
            HarvestDetailScreen(
                viewModel = detailViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
