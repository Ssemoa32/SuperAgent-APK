package com.simo.agent.agent

import android.content.Context
import com.simo.agent.api.ClaudeClient
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
    private val model: String = "qwen/qwen3.8-27b",
    private val claudeApiKey: String = "",
    private val provider: String = "groq"   // "groq" | "claude"
) {

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
                skillManager.run("web_search", mapOf("query" to command))
            }
            provider == "claude" && claudeApiKey.isNotEmpty() ->
                ClaudeClient.chat(claudeApiKey, command)
            groqApiKey.isNotEmpty() ->
                askGroq(command)
            else ->
                "أمر غير مفهوم. جرّب: افتح / اتصل / ابحث"
        }
    }

    private suspend fun askGroq(prompt: String): String = withContext(Dispatchers.IO) {
        try {
            val conn = URL("https://api.groq.com/openai/v1/chat/completions")
                .openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty("Authorization", "Bearer $groqApiKey")
            conn.setRequestProperty("Content-Type", "application/json")

            val body = JSONObject().apply {
                put("model", model)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", "أنت SimoAgent وكيل ذكي. أجب بالعربية باختصار.")
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                })
                put("max_tokens", 512)
            }
            conn.outputStream.write(body.toString().toByteArray())

            val resp = if (conn.responseCode == 200)
                conn.inputStream.bufferedReader().readText()
            else conn.errorStream.bufferedReader().readText()

            JSONObject(resp)
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
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
