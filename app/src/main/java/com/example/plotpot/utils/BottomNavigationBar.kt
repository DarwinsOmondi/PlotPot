package com.example.plotpot.utils

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Feed
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    data object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    data object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)
    data object Feeds : BottomNavItem("feeds", "Feeds", Icons.AutoMirrored.Filled.Feed)
    data object Create : BottomNavItem("create", "Create", Icons.Default.Create)
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Profile,
    BottomNavItem.Feeds,
    BottomNavItem.Create
)

@Composable
fun BottomNavBar(navHostController: NavHostController) {
    val currentRoute = navHostController.currentBackStackEntry?.destination?.route
    NavigationBar {
        bottomNavItems.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    navHostController.navigate(screen.route) {
                        navHostController.graph.startDestinationRoute?.let { startRoute ->
                            popUpTo(startRoute) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title,
                        modifier = Modifier.size(24.dp),
                        tint = if (currentRoute == screen.route) {
                            Color.Black
                        } else {
                            MaterialTheme.colorScheme.background.copy(alpha = 0.7f)
                        }
                    )
                }
            )
        }
    }
}
