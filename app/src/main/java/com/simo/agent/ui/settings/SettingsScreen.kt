package com.simo.agent.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simo.agent.utils.Prefs

private val DarkBg        = Color(0xFF0D0D0D)
private val CardBg        = Color(0xFF161616)
private val NeonCyan      = Color(0xFF00F0FF)
private val NeonPurple    = Color(0xFF8A2BE2)
private val NeonBlue      = Color(0xFF4D8BFF)
private val TextPrimary   = Color(0xFFE0E0E0)
private val TextSecondary = Color(0xFF888888)

@Composable
fun SettingsScreen() {
    val ctx = LocalContext.current

    var groqKey   by remember { mutableStateOf(Prefs.getGroqKey(ctx)) }
    var claudeKey by remember { mutableStateOf(Prefs.getClaudeKey(ctx)) }
    var model     by remember { mutableStateOf(Prefs.getModel(ctx)) }
    var provider  by remember { mutableStateOf(Prefs.getProvider(ctx)) }  // "groq" | "claude"

    var showGroqKey   by remember { mutableStateOf(false) }
    var showClaudeKey by remember { mutableStateOf(false) }
    var saved         by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text("⚙️ الإعدادات", color = NeonCyan, fontSize = 22.sp, fontWeight = FontWeight.Bold)

        // ── Provider Selector ──────────────────────────────────
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBg),
            shape  = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("🧠 مزود الذكاء الاصطناعي", color = TextPrimary, fontWeight = FontWeight.SemiBold)

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ProviderChip(
                        label     = "⚡ Groq",
                        selected  = provider == "groq",
                        color     = NeonCyan,
                        modifier  = Modifier.weight(1f)
                    ) { provider = "groq"; saved = false }

                    ProviderChip(
                        label     = "🤖 Claude",
                        selected  = provider == "claude",
                        color     = NeonPurple,
                        modifier  = Modifier.weight(1f)
                    ) { provider = "claude"; saved = false }
                }
            }
        }

        // ── Groq API Key ────────────────────────────────────────
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBg),
            shape  = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔑 Groq API Key", color = TextPrimary, fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f))
                    if (provider == "groq")
                        Text("● نشط", color = NeonCyan, fontSize = 11.sp)
                }
                Text("احصل على مفتاح مجاني من groq.com", color = TextSecondary, fontSize = 12.sp)
                ApiKeyField(
                    value     = groqKey,
                    hint      = "gsk_...",
                    visible   = showGroqKey,
                    onToggle  = { showGroqKey = !showGroqKey },
                    onChange  = { groqKey = it; saved = false }
                )
            }
        }

        // ── Groq Model ─────────────────────────────────────────
        if (provider == "groq") {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape  = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("🤖 نموذج Groq", color = TextPrimary, fontWeight = FontWeight.SemiBold)

                    val models = listOf(
                        "qwen/qwen3.8-27b"     to "Qwen 3 27B ✅ (موصى به)",
                        "llama-3.1-8b-instant" to "Llama 3.1 8B ⚡ (أسرع)",
                        "gemma2-9b-it"         to "Gemma 2 9B",
                        "llama3-8b-8192"       to "Llama 3 8B"
                    )

                    models.forEach { (id, label) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(label, color = if (model == id) NeonCyan else TextSecondary, fontSize = 13.sp)
                            RadioButton(
                                selected = model == id,
                                onClick  = { model = id; saved = false },
                                colors   = RadioButtonDefaults.colors(selectedColor = NeonCyan)
                            )
                        }
                    }
                }
            }
        }

        // ── Claude API Key ─────────────────────────────────────
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBg),
            shape  = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔑 Claude API Key", color = TextPrimary, fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f))
                    if (provider == "claude")
                        Text("● نشط", color = NeonPurple, fontSize = 11.sp)
                }
                Text("claude-sonnet-4-5 — من console.anthropic.com", color = TextSecondary, fontSize = 12.sp)
                ApiKeyField(
                    value     = claudeKey,
                    hint      = "sk-ant-...",
                    visible   = showClaudeKey,
                    onToggle  = { showClaudeKey = !showClaudeKey },
                    onChange  = { claudeKey = it; saved = false }
                )
            }
        }

        // ── Save Button ─────────────────────────────────────────
        Button(
            onClick = {
                Prefs.setGroqKey(ctx, groqKey.trim())
                Prefs.setClaudeKey(ctx, claudeKey.trim())
                Prefs.setModel(ctx, model)
                Prefs.setProvider(ctx, provider)
                saved = true
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors   = ButtonDefaults.buttonColors(
                containerColor = if (provider == "claude") NeonPurple else NeonCyan
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (saved) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("تم الحفظ ✓", color = Color.White, fontWeight = FontWeight.Bold)
            } else {
                Text("💾 حفظ الإعدادات", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }

        // ── Status ──────────────────────────────────────────────
        val ready = (provider == "groq" && groqKey.isNotEmpty()) ||
                    (provider == "claude" && claudeKey.isNotEmpty())
        if (ready) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0A2A1A)),
                shape  = RoundedCornerShape(10.dp)
            ) {
                Text(
                    "✅ الشات جاهز ($provider)",
                    color    = Color(0xFF00E676),
                    modifier = Modifier.padding(12.dp),
                    fontSize = 13.sp
                )
            }
        } else {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A1A00)),
                shape  = RoundedCornerShape(10.dp)
            ) {
                Text(
                    "⚠️ أدخل API Key الخاص بـ $provider لتفعيل المحادثة",
                    color    = Color(0xFFFFAA00),
                    modifier = Modifier.padding(12.dp),
                    fontSize = 13.sp
                )
            }
        }
    }
}

// ── Reusable composables ────────────────────────────────────

@Composable
private fun ProviderChip(
    label: String, selected: Boolean, color: Color,
    modifier: Modifier = Modifier, onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) color.copy(alpha = 0.15f) else Color(0xFF222222))
            .border(1.5.dp, if (selected) color else Color(0xFF333333), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    ) {
        Text(
            label,
            color      = if (selected) color else Color(0xFF888888),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize   = 14.sp
        )
    }
}

@Composable
private fun ApiKeyField(
    value: String, hint: String, visible: Boolean,
    onToggle: () -> Unit, onChange: (String) -> Unit
) {
    OutlinedTextField(
        value          = value,
        onValueChange  = onChange,
        placeholder    = { Text(hint, color = Color(0xFF555555)) },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = NeonCyan,
            unfocusedBorderColor = Color(0xFF333333),
            focusedTextColor     = Color(0xFFE0E0E0),
            unfocusedTextColor   = Color(0xFFE0E0E0)
        ),
        shape    = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        trailingIcon = {
            TextButton(onClick = onToggle) {
                Text(if (visible) "إخفاء" else "إظهار", color = NeonCyan, fontSize = 11.sp)
            }
        }
    )
}
