package com.simo.agent.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.simo.agent.services.SimoAccessibilityService
import com.simo.agent.utils.AccessibilityUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
val isAccessibilityEnabled: Boolean = false,
val agentStatus: String = "Offline",
val isSafeMode: Boolean = false,
val skillsCount: Int = 6,
val routerConnected: Boolean = false,
val mcpConnected: Boolean = false
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

private val _uiState = MutableStateFlow(DashboardUiState())
val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

init {
checkAccessibilityStatus()
}

fun checkAccessibilityStatus() {
viewModelScope.launch {
val enabled = AccessibilityUtils.isAccessibilityServiceEnabled(
getApplication(),
SimoAccessibilityService::class.java
)
_uiState.value = _uiState.value.copy(
isAccessibilityEnabled = enabled,
agentStatus = if (enabled) "Online / Active" else "Offline - Enable Accessibility"
)
}
}

fun openAccessibilitySettings() {
AccessibilityUtils.openAccessibilitySettings(getApplication())
}

fun toggleSafeMode(enabled: Boolean) {
_uiState.value = _uiState.value.copy(isSafeMode = enabled)
}
}
