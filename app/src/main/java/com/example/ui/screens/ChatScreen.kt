package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.ChatMessage
import com.example.service.ChatbotRole
import com.example.service.GeminiChatService
import com.example.ui.MainUiState
import com.example.ui.MainViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    uiState: MainUiState,
    viewModel: MainViewModel
) {
    var selectedRole by remember { mutableStateOf(ChatbotRole.GENERAL_DOULA) }
    var chatInput by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    text = "Namaste! I am Shakthi.ai clinical assistant. How can I support your maternal health and well-being today?",
                    isUser = false,
                    role = ChatbotRole.GENERAL_DOULA
                )
            )
        )
    }

    val quickPrompts = listOf(
        "Find 24/7 NICU maternity hospitals near me (Maps)" to ChatbotRole.MAPS_HOSPITAL_NAVIGATOR,
        "Is severe headache and blurred vision normal?" to ChatbotRole.COMPLEX_CLINICAL_SPECIALIST,
        "Sudden lower abdominal cramps what to do?" to ChatbotRole.FAST_TRIAGE_CHECKER,
        "What are iron-rich foods for Trimester 2?" to ChatbotRole.GENERAL_DOULA
    )

    fun sendUserMessage(text: String) {
        if (text.isBlank() || isSending) return
        val userMsg = ChatMessage(text = text, isUser = true, role = selectedRole)
        messages = messages + userMsg
        chatInput = ""
        isSending = true

        scope.launch {
            listState.animateScrollToItem(messages.size - 1)
            val reply = GeminiChatService.sendMessage(
                history = messages,
                newMessage = text,
                role = selectedRole
            )
            messages = messages + reply
            isSending = false
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 76.dp)
            .testTag("chat_screen_container")
    ) {
        // Top Role Switcher Strip with Model Disclosures
        Surface(
            color = HeraSurfaceContainerLowest,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "CHOOSE CLINICAL AI SPECIALIST",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = HeraPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ChatbotRole.values().forEach { role ->
                        val isSelected = selectedRole == role
                        Surface(
                            color = if (isSelected) HeraPrimary else HeraSurfaceContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .clickable { selectedRole = role }
                                .testTag("role_${role.name}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = role.emoji, fontSize = 14.sp)
                                Column {
                                    Text(
                                        text = role.title,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) HeraOnPrimary else HeraOnSurface
                                    )
                                    Text(
                                        text = role.model,
                                        fontSize = 9.sp,
                                        color = if (isSelected) HeraPrimaryFixed else HeraOnSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Prompt Suggestions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            quickPrompts.forEach { (prompt, role) ->
                Surface(
                    color = HeraSurfaceContainerLow,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.clickable {
                        selectedRole = role
                        sendUserMessage(prompt)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = HeraPrimary, modifier = Modifier.size(12.dp))
                        Text(text = prompt, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = HeraOnSurface)
                    }
                }
            }
        }

        // Scrollable Message Thread
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 10.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatBubble(msg)
            }

            if (isSending) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = HeraPrimary)
                        Text(
                            text = "${selectedRole.title} is responding with ${selectedRole.model}...",
                            fontSize = 11.sp,
                            color = HeraOnSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Bottom Input Dock
        Surface(
            color = HeraSurfaceContainerLowest,
            shadowElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = chatInput,
                    onValueChange = { chatInput = it },
                    placeholder = { Text("Ask about symptoms, hospitals, pregnancy...", fontSize = 12.sp) },
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_text_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = HeraOnSurface,
                        unfocusedTextColor = HeraOnSurface,
                        focusedContainerColor = HeraSurfaceContainerLowest,
                        unfocusedContainerColor = HeraSurfaceContainerLowest,
                        focusedBorderColor = HeraPrimary,
                        unfocusedBorderColor = HeraOutlineVariant
                    ),
                    singleLine = true
                )

                IconButton(
                    onClick = {
                        val sample = "Find nearby 24/7 NICU maternity hospitals using Google Maps"
                        chatInput = sample
                        sendUserMessage(sample)
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(HeraSurfaceContainer)
                ) {
                    Icon(Icons.Default.PinDrop, contentDescription = "Maps Query", tint = HeraSecondary)
                }

                IconButton(
                    onClick = { sendUserMessage(chatInput) },
                    enabled = chatInput.isNotBlank() && !isSending,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (chatInput.isNotBlank()) HeraPrimary else HeraSurfaceContainerLow)
                        .testTag("chat_send_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (chatInput.isNotBlank()) HeraOnPrimary else HeraOutline
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(msg: ChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (msg.isUser) Alignment.End else Alignment.Start
    ) {
        if (!msg.isUser) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(bottom = 2.dp, start = 4.dp)
            ) {
                Text(text = msg.role.emoji, fontSize = 12.sp)
                Text(
                    text = "${msg.role.title} • ${msg.role.model}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = HeraPrimary
                )
            }
        }

        Surface(
            color = if (msg.isUser) HeraPrimary else HeraSurfaceContainerLow,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (msg.isUser) 16.dp else 4.dp,
                bottomEnd = if (msg.isUser) 4.dp else 16.dp
            ),
            shadowElevation = 1.dp,
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = msg.text,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = if (msg.isUser) HeraOnPrimary else HeraOnSurface
                )

                if (msg.mapsGroundingSources.isNotEmpty()) {
                    Surface(
                        color = HeraSurfaceContainerHighest,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = HeraSecondary, modifier = Modifier.size(12.dp))
                            Text(
                                text = "Grounded via Google Maps",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = HeraSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
