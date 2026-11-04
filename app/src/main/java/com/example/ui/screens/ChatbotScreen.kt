package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.ui.MainViewModel
import com.example.ui.theme.BioCyanPrimary
import com.example.ui.theme.BioDarkBackground
import com.example.ui.theme.BioDarkBorder
import com.example.ui.theme.BioDarkSurface
import com.example.ui.theme.BioDarkSurfaceVariant
import com.example.ui.theme.BioEmeraldSecondary
import com.example.ui.theme.BioPurpleTertiary
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.SpaceGrotesk

@Composable
fun ChatbotScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.chatMessages.collectAsState()
    val selectedRole by viewModel.selectedRole.collectAsState()
    val isGenerating by viewModel.isChatGenerating.collectAsState()
    val useHighThinking by viewModel.useHighThinkingInChat.collectAsState()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val clipboardManager = LocalClipboardManager.current

    val roles = listOf(
        "Molecular Geneticist",
        "Structural Biologist",
        "Bioinformatics Lead",
        "Biosafety Officer"
    )

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = BioDarkBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(top = 16.dp, bottom = 80.dp)
        ) {
            // Header with High-Thinking Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(BioPurpleTertiary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "Chatbot",
                            tint = BioPurpleTertiary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Bioscience AI Chatbot",
                            style = MaterialTheme.typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (useHighThinking) "Gemini 4 Argon (Thinking: HIGH)" else "Gemini 3.8 Flash (General)",
                            fontFamily = JetBrainsMono,
                            fontSize = 10.sp,
                            color = if (useHighThinking) BioPurpleTertiary else BioCyanPrimary
                        )
                    }
                }

                // Thinking Mode Toggle
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "Thinking mode",
                        tint = if (useHighThinking) BioPurpleTertiary else Color(0xFF78909C),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Argon Thinking",
                        fontFamily = JetBrainsMono,
                        fontSize = 10.sp,
                        color = if (useHighThinking) BioPurpleTertiary else Color(0xFF78909C)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = useHighThinking,
                        onCheckedChange = { viewModel.toggleHighThinking(it) },
                        modifier = Modifier.testTag("chat_high_thinking_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BioPurpleTertiary,
                            checkedTrackColor = BioPurpleTertiary.copy(alpha = 0.3f),
                            uncheckedThumbColor = Color(0xFF90A4AE),
                            uncheckedTrackColor = BioDarkBorder
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Role Selector Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(roles) { role ->
                    val isSelected = selectedRole == role
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) BioCyanPrimary else BioDarkSurface)
                            .border(1.dp, if (isSelected) BioCyanPrimary else BioDarkBorder, RoundedCornerShape(8.dp))
                            .clickable { viewModel.setChatRole(role) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = role,
                            fontFamily = SpaceGrotesk,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) BioDarkBackground else Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Scrollable Thread
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { message ->
                    ChatMessageBubble(
                        message = message,
                        onCopy = { clipboardManager.setText(AnnotatedString(message.text)) },
                        onSpeak = { viewModel.voiceAssistant.speak(message.text) }
                    )
                }
            }

            // Chat Input Row
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = BioDarkSurface,
                shadowElevation = 8.dp
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
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        placeholder = {
                            Text(
                                "Ask $selectedRole...",
                                fontSize = 13.sp,
                                color = Color(0xFF78909C)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (useHighThinking) BioPurpleTertiary else BioCyanPrimary,
                            unfocusedBorderColor = BioDarkBorder
                        ),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank() && !isGenerating) {
                                val query = inputText
                                inputText = ""
                                viewModel.sendChatMessage(query)
                            }
                        },
                        enabled = inputText.isNotBlank() && !isGenerating,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (inputText.isNotBlank()) {
                                    if (useHighThinking) BioPurpleTertiary else BioCyanPrimary
                                } else BioDarkSurfaceVariant
                            )
                            .testTag("chat_send_button")
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                color = BioDarkBackground,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = if (inputText.isNotBlank()) BioDarkBackground else Color(0xFF546E7A)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatMessageBubble(
    message: ChatMessage,
    onCopy: () -> Unit,
    onSpeak: () -> Unit
) {
    val isUser = message.sender == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(if (isUser) 0.85f else 0.92f),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            // Model Badge
            if (!isUser) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(BioPurpleTertiary.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = message.modelTag,
                            fontFamily = JetBrainsMono,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = BioPurpleTertiary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    // Speak aloud button (TTS requirement)
                    IconButton(onClick = onSpeak, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Listen to answer", tint = Color(0xFF90A4AE), modifier = Modifier.size(14.dp))
                    }
                    IconButton(onClick = onCopy, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy text", tint = Color(0xFF90A4AE), modifier = Modifier.size(14.dp))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 14.dp,
                            topEnd = 14.dp,
                            bottomStart = if (isUser) 14.dp else 2.dp,
                            bottomEnd = if (isUser) 2.dp else 14.dp
                        )
                    )
                    .background(if (isUser) BioCyanPrimary else BioDarkSurfaceVariant)
                    .border(
                        1.dp,
                        if (isUser) BioCyanPrimary else BioDarkBorder,
                        RoundedCornerShape(14.dp)
                    )
                    .padding(14.dp)
            ) {
                Text(
                    text = message.text.ifEmpty { "Generating reasoning..." },
                    fontFamily = SpaceGrotesk,
                    fontSize = 13.sp,
                    color = if (isUser) BioDarkBackground else Color.White,
                    lineHeight = 19.sp
                )
            }
        }
    }
}
