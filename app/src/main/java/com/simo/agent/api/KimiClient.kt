package com.simo.agent.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object KimiClient {

    private const val BASE_URL = "https://api.moonshot.cn/v1/chat/completions"
    private const val MODEL    = "moonshot-v1-8k"

    // history per session — يُصفَّر عند كل إعادة تشغيل
    private val history = mutableListOf<JSONObject>()

    suspend fun chat(apiKey: String, prompt: String): String = withContext(Dispatchers.IO) {
        try {
            history.add(JSONObject().apply {
                put("role", "user")
                put("content", prompt)
            })
            // احتفظ بآخر 20 رسالة
            if (history.size > 20) history.removeAt(0)

            val messages = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", "أنت SimoAgent — وكيل ذكي لـ Simo. أجب بالعربية بإيجاز ووضوح.")
                })
                history.forEach { put(it) }
            }

            val body = JSONObject().apply {
                put("model", MODEL)
                put("messages", messages)
                put("max_tokens", 1024)
                put("temperature", 0.7)
            }

            val conn = URL(BASE_URL).openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput     = true
            conn.connectTimeout = 20_000
            conn.readTimeout    = 30_000
            conn.setRequestProperty("Authorization", "Bearer $apiKey")
            conn.setRequestProperty("Content-Type", "application/json")
            conn.outputStream.write(body.toString().toByteArray())

            val reply = if (conn.responseCode == 200)
                conn.inputStream.bufferedReader().readText()
            else
                return@withContext "❌ Kimi خطأ ${conn.responseCode}: ${conn.errorStream.bufferedReader().readText()}"

            val content = JSONObject(reply)
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")

            history.add(JSONObject().apply {
                put("role", "assistant")
                put("content", content)
            })
            content
        } catch (e: Exception) {
            "❌ خطأ Kimi: ${e.message}"
        }
    }

    fun clearHistory() = history.clear()
}
