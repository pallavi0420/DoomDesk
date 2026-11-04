package com.dormdesk.app.ui.chatbot

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dormdesk.app.data.model.ChatMessage
import com.dormdesk.app.data.model.SenderType
import com.dormdesk.app.ui.components.DormDeskTopBar
import com.dormdesk.app.ui.theme.*
import com.dormdesk.app.utils.DateUtils
import com.dormdesk.app.viewmodel.ChatbotViewModel
import kotlinx.coroutines.launch

@Composable
fun DormDeskAIChatScreen(
    chatbotViewModel: ChatbotViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToRoute: ((String) -> Unit)? = null
) {
    val messages by chatbotViewModel.messages.collectAsState()
    val uiState by chatbotViewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var inputText by remember { mutableStateOf("") }

    // Scroll to bottom when new messages arrive
    LaunchedEffect(messages.size, uiState.isTyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            DormDeskTopBar(
                title = "DormDesk AI",
                subtitle = if (uiState.isRemoteAiActive) "Groq LLM Active" else "Resident Assistant • 24/7",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(onClick = { chatbotViewModel.clearHistory() }) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Clear Chat",
                            tint = SubtleGray
                        )
                    }
                }
            )
        },
        containerColor = WarmIvory
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Engine indicator badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (uiState.isRemoteAiActive) SageContainer else TerracottaContainer)
                    .padding(vertical = 4.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (uiState.isRemoteAiActive) SageGreen else MutedTerracotta)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (uiState.isRemoteAiActive) "Connected to Groq AI Gateway" else "Operating via Local Hostel FAQ Engine (Zero API Dependency)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (uiState.isRemoteAiActive) SageGreen else EspressoBrown
                    )
                }
            }

            // Chat Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(messages, key = { it.messageId }) { msg ->
                    ChatMessageBubble(
                        message = msg,
                        onActionClick = { actionText ->
                            chatbotViewModel.sendMessage(actionText) { target ->
                                onNavigateToRoute?.invoke(target)
                            }
                        }
                    )
                }

                if (uiState.isTyping) {
                    item {
                        TypingIndicatorBubble()
                    }
                }
            }

            // Suggested Question Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(chatbotViewModel.suggestedQuestions) { question ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(SoftWhite)
                            .border(1.dp, NeutralBorder, RoundedCornerShape(16.dp))
                            .clickable {
                                chatbotViewModel.sendMessage(question) { target ->
                                    onNavigateToRoute?.invoke(target)
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = question,
                            fontSize = 12.sp,
                            color = EspressoBrown,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Input Bar
            Surface(
                color = SoftWhite,
                tonalElevation = 4.dp,
                modifier = Modifier.border(1.dp, NeutralBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Ask anything about your complaints or hostel...") },
                        maxLines = 3,
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EspressoBrown,
                            unfocusedBorderColor = NeutralBorder,
                            focusedContainerColor = NeutralSurface,
                            unfocusedContainerColor = NeutralSurface
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            val textToSend = inputText
                            inputText = ""
                            chatbotViewModel.sendMessage(textToSend) { target ->
                                onNavigateToRoute?.invoke(target)
                            }
                            coroutineScope.launch {
                                if (messages.isNotEmpty()) {
                                    listState.animateScrollToItem(messages.size - 1)
                                }
                            }
                        },
                        enabled = inputText.isNotBlank(),
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (inputText.isNotBlank()) EspressoBrown else NeutralBorder)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = if (inputText.isNotBlank()) SoftWhite else SubtleGray,
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
    onActionClick: (String) -> Unit
) {
    val isUser = message.senderType == SenderType.USER

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
        ) {
            if (!isUser) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(EspressoBrown),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = null,
                        tint = SoftWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        )
                    )
                    .background(if (isUser) EspressoBrown else SoftWhite)
                    .border(
                        1.dp,
                        if (isUser) EspressoBrown else NeutralBorder,
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Column {
                    Text(
                        text = message.message,
                        color = if (isUser) SoftWhite else DeepCharcoal,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = DateUtils.formatTimeOnly(message.timestamp),
                        color = if (isUser) WarmTaupe else SubtleGray,
                        fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }

        // Suggested Actions Chips attached to assistant message
        if (!isUser && message.suggestedActions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier.padding(start = 38.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                message.suggestedActions.forEach { action ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(TerracottaContainer)
                            .clickable { onActionClick(action) }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "➔ $action",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = EspressoBrown
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TypingIndicatorBubble() {
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(EspressoBrown),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SmartToy,
                contentDescription = null,
                tint = SoftWhite,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(SoftWhite)
                .border(1.dp, NeutralBorder, RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = "DormDesk AI is typing...",
                color = SubtleGray,
                fontSize = 12.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}
