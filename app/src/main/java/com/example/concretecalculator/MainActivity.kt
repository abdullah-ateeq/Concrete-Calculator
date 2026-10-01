package com.example.concretecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.concretecalculator.ui.components.AppBottomBar
import com.example.concretecalculator.ui.navigation.Screen
import com.example.concretecalculator.ui.screens.about.AboutScreen
import com.example.concretecalculator.ui.screens.calculator.CalculatorScreen
import com.example.concretecalculator.ui.screens.history.HistoryScreen
import com.example.concretecalculator.ui.screens.result.ResultScreen
import com.example.concretecalculator.ui.screens.settings.SettingsScreen
import com.example.concretecalculator.ui.screens.splash.SplashScreen
import com.example.concretecalculator.ui.theme.ConcreteCalculatorTheme
import com.example.concretecalculator.ui.viewmodel.ConcreteCalculatorViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: ConcreteCalculatorViewModel = viewModel()
            val settings by viewModel.appSettings.collectAsState()

            ConcreteCalculatorTheme(
                themeMode = settings.themeMode,
                dynamicColor = settings.dynamicColor
            ) {
                ConcreteCalculatorApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ConcreteCalculatorApp(viewModel: ConcreteCalculatorViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Splash and Result must NOT show the bottom navigation bar
    val showBottomBar = currentRoute in listOf(
        Screen.Calculator.route,
        Screen.History.route,
        Screen.Settings.route,
        Screen.About.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AppBottomBar(
                    currentRoute = currentRoute,
                    onNavigateToRoute = { targetRoute ->
                        navController.navigate(targetRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onSplashFinished = {
                        navController.navigate(Screen.Calculator.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Calculator.route) {
                CalculatorScreen(
                    viewModel = viewModel,
                    onNavigateToResult = {
                        navController.navigate(Screen.Result.route)
                    }
                )
            }

            composable(Screen.Result.route) {
                ResultScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.History.route) {
                HistoryScreen(
                    viewModel = viewModel,
                    onNavigateToResult = {
                        navController.navigate(Screen.Result.route)
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(viewModel = viewModel)
            }

            composable(Screen.About.route) {
                AboutScreen()
            }
        }
    }
}