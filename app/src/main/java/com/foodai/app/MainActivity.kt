package com.foodai.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.foodai.app.ui.navigation.FoodAINavHost
import com.foodai.app.ui.theme.FoodAITheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodAITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = com.foodai.app.ui.theme.Background
                ) {
                    FoodAINavHost()
                }
            }
        }
    }
}
