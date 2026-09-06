package com.simo.agent.utils

import android.content.Context

object Prefs {
    private const val FILE         = "simo_prefs"
    private const val KEY_GROQ     = "groq_api_key"
    private const val KEY_CLAUDE   = "claude_api_key"
    private const val KEY_KIMI     = "kimi_api_key"
    private const val KEY_MODEL    = "ai_model"
    private const val KEY_PROVIDER = "ai_provider"

    // نماذج Groq المتاحة فعلاً
    val GROQ_MODELS = listOf(
        "qwen/qwen3.8-27b",
        "llama-3.1-8b-instant",
        "gemma2-9b-it",
        "llama3-8b-8192"
    )
    private val DEPRECATED_MODELS = setOf(
        "llama-3.3-70b-versatile",
        "llama-3.3-70b",
        "mixtral-8x7b-32768"
    )

    // ── Groq ─────────────────────────────────────────────────
    fun getGroqKey(ctx: Context): String =
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE).getString(KEY_GROQ, "") ?: ""
    fun setGroqKey(ctx: Context, key: String) =
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putString(KEY_GROQ, key).apply()

    // ── Kimi (Moonshot AI) ────────────────────────────────────
    fun getKimiKey(ctx: Context): String =
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE).getString(KEY_KIMI, "") ?: ""
    fun setKimiKey(ctx: Context, key: String) =
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putString(KEY_KIMI, key).apply()

    // ── Claude ────────────────────────────────────────────────
    fun getClaudeKey(ctx: Context): String =
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE).getString(KEY_CLAUDE, "") ?: ""
    fun setClaudeKey(ctx: Context, key: String) =
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putString(KEY_CLAUDE, key).apply()

    // ── Model — تلقائياً يصحح النموذج القديم ────────────────
    fun getModel(ctx: Context): String {
        val saved = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .getString(KEY_MODEL, "qwen/qwen3.8-27b") ?: "qwen/qwen3.8-27b"
        return if (saved in DEPRECATED_MODELS) {
            // إصلاح تلقائي: النموذج القديم لم يعد متاحاً
            setModel(ctx, "qwen/qwen3.8-27b")
            "qwen/qwen3.8-27b"
        } else saved
    }
    fun setModel(ctx: Context, model: String) =
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putString(KEY_MODEL, model).apply()

    // ── Provider ──────────────────────────────────────────────
    fun getProvider(ctx: Context): String =
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE).getString(KEY_PROVIDER, "groq") ?: "groq"
    fun setProvider(ctx: Context, provider: String) =
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putString(KEY_PROVIDER, provider).apply()
}
