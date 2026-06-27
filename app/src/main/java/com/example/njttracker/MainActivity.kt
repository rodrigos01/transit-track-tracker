package com.example.njttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.njttracker.common.navigation.NavDestination
import com.example.njttracker.common.ui.theme.NJTTrackerTheme
import com.example.njttracker.departures.ui.DeparturesScreen
import com.example.njttracker.ui.composable.HomeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NJTTrackerTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = NavDestination.Home) {
                    // NavHost overrides the LocalViewModelStoreOwner so we need to be creative here
                    composable<NavDestination.Home> { HomeScreen(navController) }
                    composable<NavDestination.Departures> { backStackEntry ->
                        val args: NavDestination.Departures = backStackEntry.toRoute()
                        DeparturesScreen(args.stationId, navController)
                    }
                }
            }
        }
    }
}