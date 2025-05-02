package com.example.plotpot.utils

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Feed
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.plotpot.R


sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val labelResId: Int,
    val selectedIcon: ImageVector? = null
) {
    data object Home : BottomNavItem(
        route = "home",
        icon = Icons.Default.Home,
        labelResId = R.string.home,
        selectedIcon = Icons.Default.Home
    )

    data object Profile : BottomNavItem(
        route = "profile",
        icon = Icons.Default.Person,
        labelResId = R.string.profile,
        selectedIcon = Icons.Default.Person
    )

    data object Feeds : BottomNavItem(
        route = "feeds",
        icon = Icons.AutoMirrored.Filled.Feed,
        labelResId = R.string.feeds,
        selectedIcon = Icons.AutoMirrored.Filled.Feed
    )

    data object Create : BottomNavItem(
        route = "create",
        icon = Icons.Default.Create,
        labelResId = R.string.create,
        selectedIcon = Icons.Default.Create
    )

    companion object {
        val items = listOf(Home, Feeds, Create, Profile)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = when (currentRoute) {
        "home", "feeds", "create", "profile" -> true
        else -> false
    }

    if (showBottomBar) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.secondary,
            tonalElevation = 8.dp
        ) {
            BottomNavItem.items.forEach { item ->
                val selected = currentRoute == item.route

                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AnimatedContent(
                                targetState = selected,
                                transitionSpec = {
                                    if (targetState) {
                                        (scaleIn(animationSpec = tween(220, delayMillis = 90)) +
                                                fadeIn(
                                                    animationSpec = tween(
                                                        220,
                                                        delayMillis = 90
                                                    )
                                                ))
                                            .togetherWith(
                                                scaleOut(animationSpec = tween(90)) +
                                                        fadeOut(animationSpec = tween(90))
                                            )
                                    } else {
                                        (scaleIn(animationSpec = tween(220, delayMillis = 90)) +
                                                fadeIn(
                                                    animationSpec = tween(
                                                        220,
                                                        delayMillis = 90
                                                    )
                                                ))
                                            .togetherWith(
                                                scaleOut(animationSpec = tween(90)) +
                                                        fadeOut(animationSpec = tween(90))
                                            )
                                    }
                                },
                                label = "icon_animation"
                            ) { isSelected ->
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon
                                        ?: item.icon else item.icon,
                                    contentDescription = stringResource(id = item.labelResId),
                                    modifier = Modifier.size(24.dp),
                                    tint = if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onTertiary
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = stringResource(id = item.labelResId),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (selected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onTertiary
                                },
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}