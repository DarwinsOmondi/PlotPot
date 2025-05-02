package com.example.plotpot

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.plotpot.screens.create.CreateStoryScreen
import com.example.plotpot.screens.home.HomeScreen
import com.example.plotpot.screens.signin.SignInScreen
import com.example.plotpot.screens.signup.SignUpScreen
import com.example.plotpot.ui.theme.PlotPotTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlotPotTheme {
                val navController = rememberNavController()
                val startDestination = if (getUserLoggedInStates(this)) "home" else "signin"
                PlotPot(
                    navController = navController,
                    startDestination = startDestination
                )

            }
        }
    }
}

@Composable
fun PlotPot(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable("signup") {
            SignUpScreen(
                onSignUpSuccess = { navController.navigate("home") },
                onNavigateToLogin = { navController.navigate("signin") }
            )
        }
        composable("signin") {
            SignInScreen(
                onSignInSuccess = { navController.navigate("home") },
                onNavigateToSignUp = { navController.navigate("signup") }
            )
        }
        composable("home") { HomeScreen(navController) }
        composable("create") {
            CreateStoryScreen(navController)
        }
    }
}

fun getUserLoggedInStates(context: Context): Boolean {
    val sharedPreferences = context.getSharedPreferences("UrbanGo", Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean("isLoggedIn", false)
}