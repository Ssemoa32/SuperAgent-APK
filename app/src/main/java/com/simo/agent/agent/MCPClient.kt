package com.simo.agent.agent

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class MCPTool(
    val name: String,
    val description: String,
    val parameters: Map<String, String> = emptyMap()
)

data class MCPResponse(
    val success: Boolean,
    val result: String,
    val error: String? = null
)

class MCPClient(
    private val baseUrl: String = "http://localhost:3000",
    private val apiKey: String = ""
) {
    private val tools = mutableListOf<MCPTool>()

    suspend fun listTools(): List<MCPTool> = withContext(Dispatchers.IO) {
        try {
            val connection = URL("$baseUrl/tools").openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Authorization", "Bearer $apiKey")
            connection.setRequestProperty("Content-Type", "application/json")

            if (connection.responseCode == 200) {
                val response = connection.inputStream.bufferedReader().readText()
                // Parse tools from JSON
                val json = JSONObject(response)
                // Return parsed tools
                tools
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun callTool(toolName: String, params: Map<String, Any>): MCPResponse =
        withContext(Dispatchers.IO) {
            try {
                val connection = URL("$baseUrl/tools/$toolName").openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty("Authorization", "Bearer $apiKey")
                connection.setRequestProperty("Content-Type", "application/json")

                val body = JSONObject(params as Map<*, *>).toString()
                connection.outputStream.write(body.toByteArray())

                if (connection.responseCode == 200) {
                    val result = connection.inputStream.bufferedReader().readText()
                    MCPResponse(success = true, result = result)
                } else {
                    MCPResponse(success = false, result = "", error = "HTTP ${connection.responseCode}")
                }
            } catch (e: Exception) {
                MCPResponse(success = false, result = "", error = e.message)
            }
        }

    fun registerLocalTool(tool: MCPTool) {
        tools.add(tool)
    }
}
