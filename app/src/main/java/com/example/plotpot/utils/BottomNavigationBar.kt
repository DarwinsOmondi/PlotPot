package com.example.plotpot.utils

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Feed
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.plotpot.R
import com.example.plotpot.ui.theme.PlotPotTheme

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
    val hapticFeedback = LocalHapticFeedback.current

    val showBottomBar = currentRoute in setOf("home", "feeds", "create", "profile")

    PlotPotTheme {
        if (showBottomBar) {
            NavigationBar(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.secondary,
                                MaterialTheme.colorScheme.primary,
                            )
                        )
                    ),
                containerColor = Color.Transparent,
                tonalElevation = 8.dp
            ) {
                BottomNavItem.items.forEach { item ->
                    val selected = currentRoute == item.route
                    val interactionSource = remember { MutableInteractionSource() }
                    val scale by animateFloatAsState(
                        targetValue = if (selected) 1.2f else 1f,
                        animationSpec = spring(dampingRatio = 0.8f, stiffness = 1000f),
                        label = "icon_scale"
                    )

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                AnimatedContent(
                                    targetState = selected,
                                    transitionSpec = {
                                        (scaleIn(animationSpec = tween(200)) + fadeIn(
                                            animationSpec = tween(
                                                200
                                            )
                                        ))
                                            .togetherWith(
                                                scaleOut(animationSpec = tween(200)) + fadeOut(
                                                    animationSpec = tween(200)
                                                )
                                            )
                                    },
                                    label = "icon_animation"
                                ) { isSelected ->
                                    Icon(
                                        imageVector = item.selectedIcon ?: item.icon,
                                        contentDescription = stringResource(id = item.labelResId),
                                        modifier = Modifier
                                            .size(24.dp)
                                            .scale(scale),
                                        tint = if (isSelected) {
                                            MaterialTheme.colorScheme.onPrimary
                                        } else {
                                            MaterialTheme.colorScheme.onTertiary
                                        }
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(id = item.labelResId),
                                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                                    color = if (selected) {
                                        MaterialTheme.colorScheme.onPrimary
                                    } else {
                                        MaterialTheme.colorScheme.onTertiary
                                    },
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            unselectedIconColor = MaterialTheme.colorScheme.onTertiary,
                            unselectedTextColor = MaterialTheme.colorScheme.onTertiary
                        ),
                        interactionSource = interactionSource
                    )
                }
            }
        }
    }
}