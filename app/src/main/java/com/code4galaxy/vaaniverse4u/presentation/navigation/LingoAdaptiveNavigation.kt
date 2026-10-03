package com.code4galaxy.vaaniverse4u.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

data class LingoTopLevelDestination(
    val route: LingoRoute,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
)

val lingoTopLevelDestinations = listOf(
    LingoTopLevelDestination(LingoRoute.Home, "Home", Icons.Default.Home),
    LingoTopLevelDestination(LingoRoute.Lessons, "Learn", Icons.Default.School),
    LingoTopLevelDestination(LingoRoute.PracticeHub, "Practice", Icons.Default.SportsEsports),
    LingoTopLevelDestination(LingoRoute.Profile, "Profile", Icons.Default.Person),
)

/** Phone bottom bar, tablet rail, and large-screen drawer with identical state restoration. */
@Composable
fun LingoAdaptiveNavigation(
    navController: NavHostController,
    showNavigation: Boolean,
    content: @Composable (PaddingValues) -> Unit,
) {
    if (!showNavigation) {
        content(PaddingValues())
        return
    }

    when {
        LocalConfiguration.current.screenWidthDp >= 840 -> {
            PermanentNavigationDrawer(
                drawerContent = {
                    PermanentDrawerSheet {
                        lingoTopLevelDestinations.forEach { destination ->
                            LingoDrawerItem(destination, navController)
                        }
                    }
                },
            ) { content(PaddingValues()) }
        }

        LocalConfiguration.current.screenWidthDp >= 600 -> {
            Row(Modifier.fillMaxSize()) {
                NavigationRail { lingoTopLevelDestinations.forEach { destination -> LingoRailItem(destination, navController) } }
                content(PaddingValues())
            }
        }

        else -> Scaffold(bottomBar = { LingoBottomBar(navController) }) { padding -> content(padding) }
    }
}

@Composable
private fun LingoRailItem(destination: LingoTopLevelDestination, navController: NavHostController) {
    NavigationRailItem(
        selected = navController.isSelected(destination),
        onClick = { navController.navigateToTopLevel(destination) },
        icon = { Icon(destination.icon, contentDescription = destination.label) },
        label = { Text(destination.label) },
    )
}

@Composable
private fun LingoDrawerItem(destination: LingoTopLevelDestination, navController: NavHostController) {
    NavigationDrawerItem(
        selected = navController.isSelected(destination),
        onClick = { navController.navigateToTopLevel(destination) },
        icon = { Icon(destination.icon, contentDescription = destination.label) },
        label = { Text(destination.label) },
    )
}

@Composable
fun NavHostController.isSelected(destination: LingoTopLevelDestination): Boolean =
    currentBackStackEntryAsState().value?.destination?.route == destination.route.path

fun NavHostController.navigateToTopLevel(destination: LingoTopLevelDestination) {
    navigate(destination.route.path) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
