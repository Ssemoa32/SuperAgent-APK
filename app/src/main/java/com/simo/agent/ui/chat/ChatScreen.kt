package com.simo.agent.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import java.util.UUID

private val DarkBg      = Color(0xFF0D0D0D)
private val CardBg      = Color(0xFF161616)
private val NeonCyan    = Color(0xFF00F0FF)
private val NeonPurple  = Color(0xFF8A2BE2)
private val TextPrimary = Color(0xFFE0E0E0)
private val TextSecondary = Color(0xFFAAAAAA)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val isUser: Boolean
)

@Composable
fun ChatScreen(vm: ChatViewModel = viewModel()) {
    val uiState by vm.uiState.collectAsState()
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // اسكرول تلقائي لآخر رسالة
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty())
            listState.animateScrollToItem(uiState.messages.size - 1)
    }

    Column(modifier = Modifier.fillMaxSize().background(DarkBg)) {

        // Header
        Row(
            modifier = Modifier.fillMaxWidth().background(CardBg).padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(38.dp).background(
                    Brush.linearGradient(listOf(NeonCyan, NeonPurple)),
                    RoundedCornerShape(10.dp)
                ), contentAlignment = Alignment.Center
            ) { Text("S", color = Color.Black, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.width(10.dp))
            Column {
                Text("SimoAgent", color = TextPrimary, fontWeight = FontWeight.Bold)
                Text(
                    if (uiState.isLoading) "⚡ يفكر..." else "● متاح",
                    color = if (uiState.isLoading) NeonPurple else Color(0xFF00E676),
                    fontSize = 11.sp
                )
            }
        }

        // Messages
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(uiState.messages, key = { it.id }) { msg ->
                MessageBubble(msg)
            }
            if (uiState.isLoading) {
                item {
                    Row {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) { Text("...", color = TextSecondary, fontSize = 18.sp) }
                    }
                }
            }
        }

        // Input bar
        Row(
            modifier = Modifier.fillMaxWidth().background(CardBg).padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                placeholder = { Text("اكتب أمرك...", color = TextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = Color(0xFF333333),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f),
                maxLines = 3
            )
            IconButton(
                onClick = {
                    val text = input.trim()
                    if (text.isNotEmpty()) {
                        vm.send(text)
                        input = ""
                    }
                },
                enabled = !uiState.isLoading,
                modifier = Modifier.size(48.dp).background(
                    if (!uiState.isLoading)
                        Brush.linearGradient(listOf(NeonCyan, NeonPurple))
                    else Brush.linearGradient(listOf(Color(0xFF333333), Color(0xFF333333))),
                    RoundedCornerShape(12.dp)
                )
            ) {
                Icon(Icons.Default.Send, contentDescription = null, tint = Color.Black)
            }
        }
    }
}

@Composable
fun MessageBubble(msg: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(
                    if (msg.isUser)
                        Brush.linearGradient(listOf(
                            Color(0xFF00F0FF).copy(0.25f),
                            Color(0xFF8A2BE2).copy(0.25f)
                        ))
                    else Brush.linearGradient(listOf(Color(0xFF1E1E1E), Color(0xFF1E1E1E))),
                    RoundedCornerShape(
                        topStart = 16.dp, topEnd = 16.dp,
                        bottomStart = if (msg.isUser) 16.dp else 4.dp,
                        bottomEnd   = if (msg.isUser) 4.dp  else 16.dp
                    )
                )
                .padding(12.dp)
        ) {
            Text(msg.content, color = TextPrimary, fontSize = 14.sp, lineHeight = 20.sp)
        }
    }
}
