package com.simo.agent.ui.skills

data class Skill(
    val id: String,
    val name: String,
    val nameAr: String,
    val description: String,
    val descriptionAr: String,
    val icon: String,
    val isEnabled: Boolean = true,
    val category: SkillCategory = SkillCategory.GENERAL
)

enum class SkillCategory {
    GENERAL, SYSTEM, COMMUNICATION, DATA, AI
}

val defaultSkills = listOf(
    Skill("web_search", "Web Search", "البحث على الويب",
        "Search the internet", "البحث في الإنترنت", "🔍", true, SkillCategory.GENERAL),
    Skill("code_assistant", "Code Assistant", "مساعد البرمجة",
        "Write and debug code", "كتابة وتصحيح الكود", "💻", true, SkillCategory.AI),
    Skill("open_app", "App Launcher", "تشغيل التطبيقات",
        "Open and control apps", "فتح التطبيقات", "📱", true, SkillCategory.SYSTEM),
    Skill("make_call", "Phone Call", "إجراء مكالمة",
        "Make phone calls", "إجراء مكالمات", "📞", true, SkillCategory.COMMUNICATION),
    Skill("data_analyst", "Data Analyst", "تحليل البيانات",
        "Analyze data", "تحليل البيانات", "📊", true, SkillCategory.DATA),
    Skill("api_connector", "API Connector", "اتصال API",
        "Connect to APIs", "الاتصال بـ APIs", "🔗", false, SkillCategory.SYSTEM)
)
