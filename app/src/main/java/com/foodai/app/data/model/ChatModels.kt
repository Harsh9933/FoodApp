package com.foodai.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val content: String,
    val isFromUser: Boolean,
    val imageUri: String? = null,
    val isStreaming: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val calorieEstimate: CalorieEstimate? = null
)

@Serializable
data class CalorieEstimate(
    val foodItems: List<FoodItem> = emptyList(),
    val totalCalorieMin: Int,
    val totalCalorieMax: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val fiber: Float = 0f,
    val healthScore: Int = 0,
    val suggestion: String = ""
)

@Serializable
data class FoodItem(
    val name: String,
    val portion: String = "",
    val calorieMin: Int = 0,
    val calorieMax: Int = 0
)

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedImageUri: String? = null
)
