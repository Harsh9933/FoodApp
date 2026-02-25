package com.foodai.app.ui.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.foodai.app.data.model.CalorieEstimate
import com.foodai.app.data.model.ChatMessage
import com.foodai.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    onNavigateToPreferences: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val messages = viewModel.messages
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            ChatTopBar(onMenuClick = {}, onSettingsClick = onNavigateToPreferences)
        },
        bottomBar = {
            Column {
                // Quick action chips (only show when no messages)
                if (messages.isEmpty()) {
                    QuickActionRow(
                        onQuickSuggestions = { viewModel.onQuickSuggestions() },
                        onScanFood = {}
                    )
                }
                ChatInputBar(
                    value = uiState.inputText,
                    onValueChange = { viewModel.onInputChanged(it) },
                    onSend = { viewModel.sendMessage() },
                    onCameraClick = {},
                    isLoading = uiState.isLoading,
                    showCamera = messages.isNotEmpty()
                )
                BottomNavigationBar()
            }
        }
    ) { padding ->
        if (messages.isEmpty()) {
            // ── Empty State (matches Stitch "Chat Empty State" design) ──
            EmptyStateContent(modifier = Modifier.padding(padding))
        } else {
            // ── Active Conversation (matches Stitch "Active Conversation" design) ──
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { 20 })
                    ) {
                        if (message.calorieEstimate != null) {
                            CalorieCard(estimate = message.calorieEstimate)
                        } else {
                            MessageBubble(message = message)
                        }
                    }
                }
            }
        }
    }
}

// ─── Top App Bar ───
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatTopBar(onMenuClick: () -> Unit, onSettingsClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                "FoodAI",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = OnBackground
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Background,
            titleContentColor = OnBackground
        )
    )
}

// ─── Empty State Content (centered hero) ───
@Composable
private fun EmptyStateContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // FoodAI badge
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SurfaceVariant,
            border = BorderStroke(1.dp, Outline)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("🍲", fontSize = 14.sp)
                Text(
                    "FoodAI",
                    style = MaterialTheme.typography.labelMedium,
                    color = OnSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Bold headline
        Text(
            text = "What would you like\nto eat today?",
            style = MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Center,
            lineHeight = 38.sp,
            fontWeight = FontWeight.Bold,
            color = OnBackground
        )

        Spacer(Modifier.height(12.dp))

        // Subtitle
        Text(
            text = "Personalized food suggestions\npowered by AI",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = OnSurfaceVariant
        )

        Spacer(Modifier.height(80.dp))
    }
}

// ─── Quick Action Chips ───
@Composable
private fun QuickActionRow(onQuickSuggestions: () -> Unit, onScanFood: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AssistChip(
            onClick = onQuickSuggestions,
            label = { Text("Quick suggestions", style = MaterialTheme.typography.labelMedium) },
            leadingIcon = {
                Icon(
                    Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Primary
                )
            },
            shape = RoundedCornerShape(20.dp),
            border = AssistChipDefaults.assistChipBorder(borderColor = Outline, enabled = true),
            colors = AssistChipDefaults.assistChipColors(
                containerColor = Surface,
                labelColor = OnSurfaceVariant
            )
        )
        Spacer(Modifier.width(8.dp))
        AssistChip(
            onClick = onScanFood,
            label = { Text("Scan food", style = MaterialTheme.typography.labelMedium) },
            leadingIcon = {
                Icon(
                    Icons.Outlined.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Primary
                )
            },
            shape = RoundedCornerShape(20.dp),
            border = AssistChipDefaults.assistChipBorder(borderColor = Outline, enabled = true),
            colors = AssistChipDefaults.assistChipColors(
                containerColor = Surface,
                labelColor = OnSurfaceVariant
            )
        )
    }
}

// ─── Chat Input Bar ───
@Composable
private fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onCameraClick: () -> Unit,
    isLoading: Boolean,
    showCamera: Boolean
) {
    Surface(color = Background) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            if (showCamera) {
                IconButton(
                    onClick = onCameraClick,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        Icons.Outlined.CameraAlt,
                        contentDescription = "Attach photo",
                        tint = OnSurfaceVariant
                    )
                }
                Spacer(Modifier.width(4.dp))
            }

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        "Ask about healthy meals...",
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Surface,
                    focusedContainerColor = Surface,
                    unfocusedBorderColor = Outline,
                    focusedBorderColor = Primary,
                    cursorColor = Primary
                ),
                maxLines = 4,
                textStyle = MaterialTheme.typography.bodyMedium,
                trailingIcon = {
                    FilledIconButton(
                        onClick = onSend,
                        enabled = value.isNotBlank() && !isLoading,
                        modifier = Modifier.size(36.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Primary,
                            contentColor = OnPrimary,
                            disabledContainerColor = PrimaryContainer,
                            disabledContentColor = OnSurfaceVariant
                        ),
                        shape = CircleShape
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = OnPrimary
                            )
                        } else {
                            Icon(
                                Icons.Default.ArrowUpward,
                                contentDescription = "Send",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            )
        }
    }
}

// ─── Message Bubble ───
@Composable
fun MessageBubble(message: ChatMessage) {
    val isUser = message.isFromUser

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser) {
            // AI avatar
            Surface(
                modifier = Modifier.size(28.dp),
                shape = CircleShape,
                color = PrimaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🤖", fontSize = 14.sp)
                }
            }
            Spacer(Modifier.width(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isUser) SurfaceVariant else Surface,
            border = if (!isUser) BorderStroke(1.dp, Outline) else null,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(14.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = OnBackground
            )
        }

        if (isUser) {
            Spacer(Modifier.width(8.dp))
            Surface(
                modifier = Modifier.size(28.dp),
                shape = CircleShape,
                color = SurfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = OnSurfaceVariant
                    )
                }
            }
        }
    }
}

// ─── Calorie Estimate Card ───
@Composable
fun CalorieCard(estimate: CalorieEstimate) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        border = BorderStroke(1.dp, Outline),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with health score
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🔥", fontSize = 18.sp)
                Spacer(Modifier.width(6.dp))
                Text(
                    "Calorie Estimate",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SecondaryContainer
                ) {
                    Text(
                        "${estimate.healthScore}/10",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = HealthGreen
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Total calories (prominent)
            Text(
                "${estimate.totalCalorieMin}–${estimate.totalCalorieMax} kcal",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = CalorieAccent
            )

            Spacer(Modifier.height(12.dp))

            // Macros inline
            Text(
                "Protein ${estimate.protein.toInt()}g · Carbs ${estimate.carbs.toInt()}g · Fat ${estimate.fat.toInt()}g",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant
            )

            if (estimate.foodItems.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = OutlineVariant)
                Spacer(Modifier.height(12.dp))

                estimate.foodItems.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "${item.name} (${item.portion})",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnBackground
                        )
                        Text(
                            "${item.calorieMin}–${item.calorieMax} kcal",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }

            if (estimate.suggestion.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceVariant
                ) {
                    Text(
                        "💡 ${estimate.suggestion}",
                        modifier = Modifier.padding(10.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Footer
            Text(
                "POWERED BY FOODAI CORE",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

// ─── Bottom Navigation Bar ───
@Composable
private fun BottomNavigationBar() {
    NavigationBar(
        containerColor = Background,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = {
                Icon(
                    Icons.AutoMirrored.Filled.Chat,
                    contentDescription = "Chat"
                )
            },
            label = { Text("Chat", style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NavItemActive,
                selectedTextColor = NavItemActive,
                unselectedIconColor = NavItemInactive,
                unselectedTextColor = NavItemInactive,
                indicatorColor = PrimaryContainer
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = {
                Icon(
                    Icons.Outlined.RestaurantMenu,
                    contentDescription = "Recipes"
                )
            },
            label = { Text("Recipes", style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = NavItemInactive,
                unselectedTextColor = NavItemInactive
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = {
                Icon(
                    Icons.Outlined.QrCodeScanner,
                    contentDescription = "Scanner"
                )
            },
            label = { Text("Scanner", style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = NavItemInactive,
                unselectedTextColor = NavItemInactive
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = {
                Icon(
                    Icons.Outlined.Person,
                    contentDescription = "Profile"
                )
            },
            label = { Text("Profile", style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = NavItemInactive,
                unselectedTextColor = NavItemInactive
            )
        )
    }
}
