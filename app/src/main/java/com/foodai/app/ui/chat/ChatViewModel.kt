package com.foodai.app.ui.chat

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.foodai.app.data.model.CalorieEstimate
import com.foodai.app.data.model.ChatMessage
import com.foodai.app.data.model.ChatUiState
import com.foodai.app.data.model.FoodItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> get() = _messages

    fun onInputChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isEmpty()) return

        // Add user message
        val userMsg = ChatMessage(
            content = text,
            isFromUser = true,
            timestamp = System.currentTimeMillis()
        )
        _messages.add(userMsg)
        _uiState.update { it.copy(inputText = "", isLoading = true) }

        // Simulate AI response (replace with actual backend call)
        simulateAIResponse(text)
    }

    fun onQuickSuggestions() {
        _uiState.update { it.copy(inputText = "Suggest a healthy meal for dinner") }
        sendMessage()
    }

    private fun simulateAIResponse(userMessage: String) {
        // Demo: simulate a response with calorie info
        val aiMsg = ChatMessage(
            content = "Based on your preferences, here's a light dinner idea:\n\n• Grilled salmon with quinoa\n• Steamed broccoli\n• ~420 kcal",
            isFromUser = false,
            timestamp = System.currentTimeMillis()
        )
        _messages.add(aiMsg)

        // Add calorie card
        val calorieMsg = ChatMessage(
            content = "",
            isFromUser = false,
            timestamp = System.currentTimeMillis(),
            calorieEstimate = CalorieEstimate(
                foodItems = listOf(
                    FoodItem("Grilled Salmon", "150g", 280, 320),
                    FoodItem("Quinoa", "100g", 120, 140),
                    FoodItem("Steamed Broccoli", "80g", 25, 35)
                ),
                totalCalorieMin = 280,
                totalCalorieMax = 340,
                protein = 24f,
                carbs = 32f,
                fat = 9f,
                fiber = 4f,
                healthScore = 8,
                suggestion = "Add avocado for healthy fats"
            )
        )
        _messages.add(calorieMsg)
        _uiState.update { it.copy(isLoading = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
