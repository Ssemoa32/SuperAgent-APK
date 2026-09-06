package com.simo.agent.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object OpenRouterClient {

    private const val BASE_URL = "https://openrouter.ai/api/v1/chat/completions"

    // نماذج مجانية 100% — لا تحتاج رصيد
    val FREE_MODELS = listOf(
        "meta-llama/llama-3.1-8b-instruct:free"    to "Llama 3.1 8B ⚡ (مجاني)",
        "mistralai/mistral-7b-instruct:free"         to "Mistral 7B 🔥 (مجاني)",
        "google/gemma-2-9b-it:free"                  to "Gemma 2 9B 💎 (مجاني)",
        "qwen/qwen-2-7b-instruct:free"               to "Qwen 2 7B 🌟 (مجاني)",
        "microsoft/phi-3-mini-128k-instruct:free"    to "Phi-3 Mini 🔬 (مجاني)"
    )

    private val history = mutableListOf<JSONObject>()

    suspend fun chat(apiKey: String, model: String, prompt: String): String = withContext(Dispatchers.IO) {
        try {
            history.add(JSONObject().apply {
                put("role", "user")
                put("content", prompt)
            })
            if (history.size > 20) history.removeAt(0)

            val messages = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", "أنت SimoAgent — وكيل ذكي لـ Simo. أجب بالعربية بإيجاز.")
                })
                history.forEach { put(it) }
            }

            val body = JSONObject().apply {
                put("model", model)
                put("messages", messages)
                put("max_tokens", 1024)
                put("temperature", 0.7)
            }

            val conn = URL(BASE_URL).openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput      = true
            conn.connectTimeout = 20_000
            conn.readTimeout    = 30_000
            conn.setRequestProperty("Authorization", "Bearer $apiKey")
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("HTTP-Referer", "https://simoagent.app")
            conn.setRequestProperty("X-Title", "SimoAgent")
            conn.outputStream.write(body.toString().toByteArray())

            val raw = if (conn.responseCode == 200)
                conn.inputStream.bufferedReader().readText()
            else
                return@withContext "❌ OpenRouter ${conn.responseCode}: ${conn.errorStream.bufferedReader().readText()}"

            val content = JSONObject(raw)
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
            "❌ خطأ OpenRouter: ${e.message}"
        }
    }

    fun clearHistory() = history.clear()
}
