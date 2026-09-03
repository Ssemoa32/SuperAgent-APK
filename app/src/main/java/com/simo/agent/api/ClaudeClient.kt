package com.simo.agent.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Claude API Client (Anthropic)
 * يتصل بـ Claude مباشرة من Android
 */
object ClaudeClient {

    private const val BASE_URL  = "https://api.anthropic.com/v1/messages"
    private const val API_VER   = "2023-06-01"
    private const val MODEL     = "claude-sonnet-4-5"   // أو claude-haiku-3-5 للسرعة

    private val history = mutableListOf<JSONObject>()

    suspend fun chat(
        apiKey: String,
        userMessage: String,
        systemPrompt: String = "أنت SimoAgent وكيل ذكي. أجب بالعربية باختصار ودقة.",
        clearHistory: Boolean = false
    ): String = withContext(Dispatchers.IO) {

        if (clearHistory) history.clear()

        history.add(JSONObject().apply {
            put("role", "user")
            put("content", userMessage)
        })

        try {
            val conn = URL(BASE_URL).openConnection() as HttpURLConnection
            conn.apply {
                requestMethod = "POST"
                doOutput = true
                setRequestProperty("x-api-key",        apiKey)
                setRequestProperty("anthropic-version", API_VER)
                setRequestProperty("content-type",      "application/json")
                connectTimeout = 30_000
                readTimeout    = 60_000
            }

            val body = JSONObject().apply {
                put("model",      MODEL)
                put("max_tokens", 1024)
                put("system",     systemPrompt)
                put("messages",   JSONArray(history.takeLast(10).toString()))
            }

            conn.outputStream.use { it.write(body.toString().toByteArray()) }

            val response = if (conn.responseCode == 200)
                conn.inputStream.bufferedReader().readText()
            else
                conn.errorStream.bufferedReader().readText()

            val reply = JSONObject(response)
                .getJSONArray("content")
                .getJSONObject(0)
                .getString("text")

            history.add(JSONObject().apply {
                put("role", "assistant")
                put("content", reply)
            })

            reply

        } catch (e: Exception) {
            "❌ Claude Error: ${e.message}"
        }
    }

    fun clearHistory() = history.clear()
}
