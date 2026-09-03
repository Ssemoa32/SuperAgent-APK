package com.simo.agent.agent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AgentState {
    IDLE, THINKING, EXECUTING, ERROR
}

data class AgentStatus(
    val state: AgentState = AgentState.IDLE,
    val currentTask: String = "",
    val tasksCompleted: Int = 0,
    val successRate: Float = 0.986f,
    val uptime: String = "0m"
)

class AgentCore(
    private val router: Router,
    private val mcpClient: MCPClient
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _status = MutableStateFlow(AgentStatus())
    val status: StateFlow<AgentStatus> = _status.asStateFlow()

    private val startTime = System.currentTimeMillis()

    fun execute(command: String, onResult: (String) -> Unit) {
        scope.launch {
            try {
                _status.value = _status.value.copy(
                    state = AgentState.THINKING,
                    currentTask = command
                )

                val result = router.route(command)

                _status.value = _status.value.copy(
                    state = AgentState.IDLE,
                    currentTask = "",
                    tasksCompleted = _status.value.tasksCompleted + 1
                )

                onResult(result)
            } catch (e: Exception) {
                _status.value = _status.value.copy(
                    state = AgentState.ERROR,
                    currentTask = "خطأ: ${e.message}"
                )
                onResult("حدث خطأ: ${e.message}")
            }
        }
    }

    fun getUptime(): String {
        val elapsed = System.currentTimeMillis() - startTime
        val minutes = (elapsed / 60000).toInt()
        val hours = minutes / 60
        return if (hours > 0) "${hours}h ${minutes % 60}m" else "${minutes}m"
    }
}
