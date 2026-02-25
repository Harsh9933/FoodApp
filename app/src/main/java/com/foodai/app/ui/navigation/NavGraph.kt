package com.foodai.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.foodai.app.ui.chat.ChatScreen

@Composable
fun FoodAINavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "chat"
    ) {
        composable("chat") {
            ChatScreen(
                onNavigateToPreferences = {
                    // TODO: Navigate to preferences screen
                }
            )
        }
        // Future screens:
        // composable("preferences") { PreferencesScreen(...) }
        // composable("onboarding") { OnboardingScreen(...) }
        // composable("history") { HistoryScreen(...) }
    }
}
