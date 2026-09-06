package com.simo.agent.ui.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.simo.agent.agent.Router
import com.simo.agent.agent.SkillManager
import com.simo.agent.utils.Prefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage(content = "مرحباً! أنا SimoAgent 🤖\nاكتب أمرك أو سؤالك بالعربية", isUser = false)
    ),
    val isLoading: Boolean = false
)

class ChatViewModel(app: Application) : AndroidViewModel(app) {

    private val skillManager = SkillManager()

    // Router يُنشأ مرة واحدة — إعادة الإنشاء لكل رسالة تضيع تاريخ Groq وتهدر الموارد
    private var router: Router = buildRouter()

    private fun buildRouter(): Router {
        val ctx      = getApplication<Application>()
        val provider = Prefs.getProvider(ctx)
        return Router(
            skillManager      = skillManager,
            context           = ctx,
            groqApiKey        = Prefs.getGroqKey(ctx),
            model             = Prefs.getModel(ctx),
            claudeApiKey      = Prefs.getClaudeKey(ctx),
            kimiApiKey        = Prefs.getKimiKey(ctx),
            openRouterApiKey  = Prefs.getOpenRouterKey(ctx),
            openRouterModel   = Prefs.getOpenRouterModel(ctx),
            provider          = provider
        )
    }

    /** استدعيها بعد تغيير الإعدادات لإعادة بناء الـ Router بالمفاتيح الجديدة */
    fun reloadSettings() { router = buildRouter() }

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun send(text: String) {
        if (text.isBlank()) return

        val userMsg = ChatMessage(content = text, isUser = true)
        _uiState.value = _uiState.value.copy(
            messages  = _uiState.value.messages + userMsg,
            isLoading = true
        )

        viewModelScope.launch {
            val response = router.route(text)
            _uiState.value = _uiState.value.copy(
                messages  = _uiState.value.messages + ChatMessage(content = response, isUser = false),
                isLoading = false
            )
        }
    }
}
