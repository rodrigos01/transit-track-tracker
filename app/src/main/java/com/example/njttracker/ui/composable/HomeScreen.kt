package com.example.njttracker.ui.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.njttracker.common.ui.theme.NJTTrackerTheme
import com.example.njttracker.stations.ui.StationsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text("NY Penn NJT Tracker")
            })
        }) { innerPadding ->
        StationsScreen(navController, modifier = Modifier.padding(innerPadding))
    }
}

@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    NJTTrackerTheme {
        HomeScreen(rememberNavController())
    }
}