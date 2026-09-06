package com.simo.agent.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.simo.agent.utils.Prefs
import java.util.UUID

// ── Colours ──────────────────────────────────────────────────
private val DarkBg        = Color(0xFF0D0D0D)
private val CardBg        = Color(0xFF161616)
private val NeonCyan      = Color(0xFF00F0FF)
private val NeonPurple    = Color(0xFF8A2BE2)
private val NeonBlue      = Color(0xFF4D8BFF)
private val NeonGreen     = Color(0xFF00E676)
private val TextPrimary   = Color(0xFFE0E0E0)
private val TextSecondary = Color(0xFFAAAAAA)

// ── Provider meta ─────────────────────────────────────────────
private data class ProviderMeta(val emoji: String, val label: String, val color: Color)
private fun providerMeta(p: String) = when (p) {
    "claude"      -> ProviderMeta("🤖", "Claude",      NeonPurple)
    "kimi"        -> ProviderMeta("🌙", "Kimi",         NeonBlue)
    "openrouter"  -> ProviderMeta("🆓", "Free",         NeonGreen)
    else          -> ProviderMeta("⚡", "Groq",          NeonCyan)
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val isUser: Boolean
)

// ── ChatScreen ────────────────────────────────────────────────
@Composable
fun ChatScreen(
    vm: ChatViewModel = viewModel(),
    navController: NavController? = null
) {
    val ctx      = LocalContext.current
    val uiState  by vm.uiState.collectAsState()
    var input    by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // قراءة المزود الحالي
    val provider by remember {
        derivedStateOf { Prefs.getProvider(ctx) }
    }
    val meta = providerMeta(provider)

    // تحقق من وجود مفتاح
    val hasKey = remember(provider) {
        when (provider) {
            "groq"       -> Prefs.getGroqKey(ctx).isNotEmpty()
            "claude"     -> Prefs.getClaudeKey(ctx).isNotEmpty()
            "kimi"       -> Prefs.getKimiKey(ctx).isNotEmpty()
            "openrouter" -> Prefs.getOpenRouterKey(ctx).isNotEmpty()
            else         -> false
        }
    }

    // اسكرول تلقائي لآخر رسالة
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty())
            listState.animateScrollToItem(uiState.messages.size - 1)
    }

    Column(modifier = Modifier.fillMaxSize().background(DarkBg)) {

        // ── Header ─────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBg)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Brush.linearGradient(listOf(meta.color, meta.color.copy(alpha = 0.5f))),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(meta.emoji, fontSize = 18.sp)
            }

            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("SimoAgent", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // نقطة الحالة
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(
                                if (uiState.isLoading) NeonPurple else if (hasKey) NeonGreen else Color(0xFFFF5555),
                                RoundedCornerShape(50)
                            )
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        when {
                            uiState.isLoading -> "يفكر..."
                            !hasKey           -> "أضف API Key من الإعدادات"
                            else              -> "جاهز"
                        },
                        color = when {
                            uiState.isLoading -> NeonPurple
                            !hasKey           -> Color(0xFFFF5555)
                            else              -> TextSecondary
                        },
                        fontSize = 11.sp
                    )
                }
            }

            // بادج المزود
            Surface(
                color = meta.color.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "${meta.emoji} ${meta.label}",
                    color    = meta.color,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }

            Spacer(Modifier.width(8.dp))

            // زر الإعدادات
            IconButton(
                onClick = { navController?.navigate("settings") },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.Settings, contentDescription = "إعدادات", tint = TextSecondary)
            }
        }

        // ── تحذير: لا يوجد مفتاح ───────────────────────────────
        if (!hasKey) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2A1A00))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "⚠️ أدخل ${meta.label} API Key لتفعيل المحادثة",
                    color    = Color(0xFFFFAA00),
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { navController?.navigate("settings") }) {
                    Text("إعدادات ⚙️", color = Color(0xFFFFAA00), fontSize = 12.sp)
                }
            }
        }

        // ── Messages ────────────────────────────────────────────
        LazyColumn(
            state    = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(uiState.messages, key = { it.id }) { msg ->
                MessageBubble(msg, meta.color)
            }
            if (uiState.isLoading) {
                item {
                    TypingIndicator(meta.color)
                }
            }
        }

        // ── Input bar ───────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBg)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value         = input,
                onValueChange = { input = it },
                placeholder   = { Text("اكتب أمرك...", color = TextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = meta.color,
                    unfocusedBorderColor = Color(0xFF333333),
                    focusedTextColor     = TextPrimary,
                    unfocusedTextColor   = TextPrimary
                ),
                shape    = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f),
                maxLines = 3
            )

            IconButton(
                onClick = {
                    val text = input.trim()
                    if (text.isNotEmpty() && !uiState.isLoading) {
                        vm.send(text)
                        input = ""
                    }
                },
                enabled  = !uiState.isLoading && input.trim().isNotEmpty(),
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (!uiState.isLoading && input.trim().isNotEmpty())
                            Brush.linearGradient(listOf(meta.color, meta.color.copy(alpha = 0.6f)))
                        else
                            Brush.linearGradient(listOf(Color(0xFF333333), Color(0xFF333333))),
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(Icons.Default.Send, contentDescription = "إرسال", tint = Color.Black)
            }
        }
    }
}

// ── MessageBubble ─────────────────────────────────────────────
@Composable
fun MessageBubble(msg: ChatMessage, accentColor: Color = NeonCyan) {
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
                            accentColor.copy(alpha = 0.22f),
                            accentColor.copy(alpha = 0.10f)
                        ))
                    else
                        Brush.linearGradient(listOf(Color(0xFF1E1E1E), Color(0xFF222222))),
                    RoundedCornerShape(
                        topStart    = 16.dp,
                        topEnd      = 16.dp,
                        bottomStart = if (msg.isUser) 16.dp else 4.dp,
                        bottomEnd   = if (msg.isUser) 4.dp  else 16.dp
                    )
                )
                .padding(12.dp)
        ) {
            Text(msg.content, color = TextPrimary, fontSize = 14.sp, lineHeight = 21.sp)
        }
    }
}

// ── Typing Indicator ──────────────────────────────────────────
@Composable
private fun TypingIndicator(color: Color) {
    Row {
        Surface(
            color = Color(0xFF1E1E1E),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(color.copy(alpha = 0.7f), RoundedCornerShape(50))
                    )
                }
            }
        }
    }
}
