package com.simo.agent.agent

import android.content.Context
import android.content.Intent
import android.net.Uri

interface Skill {
    val name: String
    val description: String
    suspend fun execute(params: Map<String, Any>): String
}

class SkillManager {
    private val skills = mutableMapOf<String, Skill>()

    init {
        // تسجيل المهارات الأساسية
        register(OpenAppSkill())
        register(WebSearchSkill())
        register(MakeCallSkill())
    }

    fun register(skill: Skill) { skills[skill.name] = skill }

    suspend fun run(skillName: String, params: Map<String, Any>): String =
        skills[skillName]?.execute(params) ?: "المهارة '$skillName' غير موجودة"
}

// ── فتح تطبيق ──────────────────────────────────────────────────────────────
class OpenAppSkill : Skill {
    override val name = "open_app"
    override val description = "فتح تطبيق بواسطة اسم الحزمة"

    override suspend fun execute(params: Map<String, Any>): String {
        val context = params["context"] as? Context ?: return "لا يوجد context"
        val packageName = params["package"] as? String ?: return "لا يوجد اسم حزمة"
        return try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                "✅ تم فتح $packageName"
            } else {
                "❌ التطبيق غير مثبّت: $packageName"
            }
        } catch (e: Exception) {
            "خطأ: ${e.message}"
        }
    }
}

// ── بحث على الويب ──────────────────────────────────────────────────────────
class WebSearchSkill : Skill {
    override val name = "web_search"
    override val description = "فتح البحث في المتصفح"

    override suspend fun execute(params: Map<String, Any>): String {
        val context = params["context"] as? Context ?: return "لا يوجد context"
        val query = params["query"] as? String ?: return "لا يوجد استعلام"
        return try {
            val encoded = Uri.encode(query)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$encoded"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            "✅ جاري البحث عن: $query"
        } catch (e: Exception) {
            "خطأ: ${e.message}"
        }
    }
}

// ── إجراء مكالمة ───────────────────────────────────────────────────────────
class MakeCallSkill : Skill {
    override val name = "make_call"
    override val description = "إجراء مكالمة هاتفية"

    override suspend fun execute(params: Map<String, Any>): String {
        val number = params["number"] as? String ?: return "لا يوجد رقم"
        return "جاري الاتصال بـ $number"
    }
}
