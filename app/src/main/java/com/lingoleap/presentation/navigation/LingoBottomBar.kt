package com.lingoleap.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions

private data class BottomDestination(
    val route: LingoRoute,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private val destinations = listOf(
    BottomDestination(LingoRoute.Home, "Home", Icons.Default.Home),
    BottomDestination(LingoRoute.Lessons, "Learn", Icons.Default.School),
    BottomDestination(LingoRoute.Practice, "Practice", Icons.Default.SportsEsports),
    BottomDestination(LingoRoute.Profile, "Profile", Icons.Default.Person),
)

@Composable
fun LingoBottomBar(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    NavigationBar {
        destinations.forEach { destination ->
            NavigationBarItem(
                selected = currentRoute == destination.route.path,
                onClick = {
                    val destinationPath =
                        if (destination.route == LingoRoute.Practice) LingoRoute.Practice.create("en-hi-greetings") else destination.route.path
                    navController.navigate(destinationPath, navOptions {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(LingoRoute.Home.path) { saveState = true }
                    })
                },
                icon = { Icon(destination.icon, contentDescription = destination.label) },
                label = { Text(destination.label) },
            )
        }
    }
}
