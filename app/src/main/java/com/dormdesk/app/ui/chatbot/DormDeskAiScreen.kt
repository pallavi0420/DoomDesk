package com.dormdesk.app.ui.chatbot

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dormdesk.app.data.model.Announcement
import com.dormdesk.app.data.model.ChatMessage
import com.dormdesk.app.data.model.ChatSender
import com.dormdesk.app.data.model.Complaint
import com.dormdesk.app.data.model.User
import com.dormdesk.app.ui.components.DormDeskCard
import com.dormdesk.app.ui.components.DormDeskTopBar
import com.dormdesk.app.ui.theme.*
import com.dormdesk.app.viewmodel.ChatbotViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DormDeskAiScreen(
    currentUser: User,
    complaints: List<Complaint>,
    announcements: List<Announcement>,
    chatbotViewModel: ChatbotViewModel,
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val messages by chatbotViewModel.messages.collectAsState()
    val inputText by chatbotViewModel.inputText.collectAsState()
    val isTyping by chatbotViewModel.isTyping.collectAsState()
    val backendStatus by chatbotViewModel.backendStatus.collectAsState()
    val suggestedQuestions = chatbotViewModel.suggestedQuestions

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val dateFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    // Auto scroll to bottom when a new message arrives
    LaunchedEffect(messages.size, isTyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(EspressoBrown),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.SmartToy, contentDescription = null, tint = SoftWhite, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "DormDesk AI",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(SageGreen)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = backendStatus,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { chatbotViewModel.clearHistory() }) {
                        Icon(Icons.Outlined.DeleteSweep, contentDescription = "Clear Chat", tint = WarmTaupe)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Chat Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages, key = { it.messageId }) { msg ->
                    ChatMessageBubble(
                        message = msg,
                        dateFormat = dateFormat,
                        onActionClick = { route ->
                            onNavigateToRoute(route)
                        }
                    )
                }

                if (isTyping) {
                    item {
                        TypingIndicator()
                    }
                }
            }

            // Suggested Question Chips
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    suggestedQuestions.forEach { question ->
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .border(BorderStroke(1.dp, WarmTaupe.copy(alpha = 0.4f)), RoundedCornerShape(16.dp))
                                .clickable {
                                    chatbotViewModel.sendMessage(
                                        currentUser = currentUser,
                                        userComplaints = complaints,
                                        announcements = announcements,
                                        customMessage = question
                                    )
                                },
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                text = question,
                                style = MaterialTheme.typography.labelMedium,
                                color = EspressoBrown,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Input Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { chatbotViewModel.setInputText(it) },
                        placeholder = { Text("Ask about complaints, mess, notices...", fontSize = 13.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EspressoBrown,
                            unfocusedBorderColor = BorderLight
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            chatbotViewModel.sendMessage(
                                currentUser = currentUser,
                                userComplaints = complaints,
                                announcements = announcements
                            )
                        },
                        enabled = inputText.isNotBlank() && !isTyping,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (inputText.isNotBlank() && !isTyping) EspressoBrown else WarmTaupe.copy(alpha = 0.3f))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = SoftWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    dateFormat: SimpleDateFormat,
    onActionClick: (String) -> Unit
) {
    val isUser = message.senderType == ChatSender.USER

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) EspressoBrown else SoftWhite,
            border = if (!isUser) BorderStroke(1.dp, BorderLight) else null,
            shadowElevation = 0.dp,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                // Header badge for Assistant
                if (!isUser) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (message.isGeminiAi) "DormDesk AI (Gemini)" else "DormDesk Assistant",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (message.isGeminiAi) MutedTerracotta else StatusAssigned,
                            fontSize = 10.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = message.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUser) SoftWhite else MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp
                )

                // Optional Action Button inside assistant bubble
                if (!isUser && message.suggestedActionRoute != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    val actionLabel = when (message.suggestedActionRoute) {
                        "student_complaint_raise" -> "Raise Complaint Now"
                        "student_complaints" -> "View My Complaints"
                        "student_notices" -> "Open Notice Board"
                        "student_feedback" -> "Submit Feedback"
                        else -> "View Details"
                    }

                    Button(
                        onClick = { onActionClick(message.suggestedActionRoute) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EspressoBrown),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(actionLabel, fontSize = 11.sp, color = SoftWhite)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = dateFormat.format(Date(message.timestamp)),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isUser) SoftWhite.copy(alpha = 0.7f) else WarmTaupe,
                    fontSize = 9.sp,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    val alpha1 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(600), repeatMode = RepeatMode.Reverse), label = "a1"
    )
    val alpha2 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(600, delayMillis = 200), repeatMode = RepeatMode.Reverse), label = "a2"
    )
    val alpha3 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(600, delayMillis = 400), repeatMode = RepeatMode.Reverse), label = "a3"
    )

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SoftWhite,
        border = BorderStroke(1.dp, BorderLight),
        modifier = Modifier.padding(start = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(EspressoBrown.copy(alpha = alpha1)))
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(EspressoBrown.copy(alpha = alpha2)))
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(EspressoBrown.copy(alpha = alpha3)))
        }
    }
}
