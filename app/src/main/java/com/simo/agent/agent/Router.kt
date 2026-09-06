package com.simo.agent.agent

import android.content.Context
import com.simo.agent.api.ClaudeClient
import com.simo.agent.api.KimiClient
import com.simo.agent.api.OpenRouterClient
import com.simo.agent.services.CallService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class Router(
    private val skillManager: SkillManager,
    private val context: Context? = null,
    private val groqApiKey: String = "",
    private val model: String = "llama-3.3-70b-versatile", // Groq model ID صحيح
    private val claudeApiKey: String = "",
    private val kimiApiKey: String = "",
    private val openRouterApiKey: String = "",
    private val openRouterModel: String = "meta-llama/llama-3.1-8b-instruct:free",
    private val provider: String = "groq"   // "groq"|"claude"|"kimi"|"openrouter"
) {
    // تاريخ المحادثة لـ Groq (الـ clients الأخرى تحتفظ بـ history داخلياً)
    private val groqHistory = mutableListOf<JSONObject>()

    suspend fun route(command: String): String {
        return when {
            command.contains("افتح", ignoreCase = true) ||
            command.contains("شغّل", ignoreCase = true) -> {
                val pkg = extractPackageName(command)
                if (context != null && pkg != null) {
                    skillManager.run("open_app", mapOf("context" to context, "package" to pkg))
                } else {
                    "حدد اسم التطبيق (مثال: افتح واتساب)"
                }
            }
            command.contains("اتصل", ignoreCase = true) ||
            command.contains("كلّم", ignoreCase = true) -> {
                val number = CallService.extractPhoneNumber(command)
                if (context != null && number != null) {
                    CallService.makeCall(context, number)
                    "جاري الاتصال بـ $number"
                } else "ما لقيت رقم في الأمر"
            }
            command.contains("ابحث", ignoreCase = true) -> {
                val query = command.replace("ابحث", "", ignoreCase = true).trim()
                if (context != null) {
                    skillManager.run("web_search", mapOf("context" to context, "query" to query.ifEmpty { command }))
                } else {
                    "ابحث عن: $query"
                }
            }
            provider == "claude" && claudeApiKey.isNotEmpty() ->
                ClaudeClient.chat(claudeApiKey, command)
            provider == "kimi" && kimiApiKey.isNotEmpty() ->
                KimiClient.chat(kimiApiKey, command)
            provider == "openrouter" && openRouterApiKey.isNotEmpty() ->
                OpenRouterClient.chat(openRouterApiKey, openRouterModel, command)
            groqApiKey.isNotEmpty() ->
                askGroq(command)
            else ->
                "⚠️ أدخل API Key من الإعدادات ⚙️"
        }
    }

    private suspend fun askGroq(prompt: String): String = withContext(Dispatchers.IO) {
        // https://console.groq.com/docs/openai — OpenAI-compatible API
        groqHistory.add(JSONObject().apply {
            put("role", "user")
            put("content", prompt)
        })
        if (groqHistory.size > 20) groqHistory.removeAt(0)

        try {
            val conn = URL("https://api.groq.com/openai/v1/chat/completions")
                .openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.connectTimeout = 20_000
            conn.readTimeout    = 30_000
            conn.setRequestProperty("Authorization", "Bearer $groqApiKey")
            conn.setRequestProperty("Content-Type", "application/json")

            val messages = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", "أنت SimoAgent وكيل ذكي. أجب بالعربية باختصار.")
                })
                groqHistory.forEach { put(it) }
            }

            val body = JSONObject().apply {
                put("model", model)
                put("messages", messages)
                put("max_tokens", 512)
            }
            conn.outputStream.write(body.toString().toByteArray())

            val resp = if (conn.responseCode == 200)
                conn.inputStream.bufferedReader().readText()
            else return@withContext "❌ Groq ${conn.responseCode}: ${conn.errorStream.bufferedReader().readText()}"

            val content = JSONObject(resp)
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")

            groqHistory.add(JSONObject().apply {
                put("role", "assistant")
                put("content", content)
            })
            content
        } catch (e: Exception) {
            "خطأ Groq: ${e.message}"
        }
    }

    private fun extractPackageName(command: String): String? {
        val apps = mapOf(
            "واتساب"    to "com.whatsapp",
            "whatsapp"  to "com.whatsapp",
            "تيليجرام"  to "org.telegram.messenger",
            "telegram"  to "org.telegram.messenger",
            "يوتيوب"   to "com.google.android.youtube",
            "youtube"   to "com.google.android.youtube",
            "كروم"      to "com.android.chrome",
            "chrome"    to "com.android.chrome",
            "إعدادات"   to "com.android.settings",
            "settings"  to "com.android.settings",
            "كاميرا"    to "com.android.camera2"
        )
        return apps.entries.firstOrNull { command.contains(it.key, ignoreCase = true) }?.value
    }
}
