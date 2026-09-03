# SimoAgent 🤖

تطبيق Android وكيل ذكي (AI Agent) حقيقي بصلاحيات نظام كاملة.

## الملفات

```
SimoAgent/
├── app/src/main/
│   ├── java/com/simo/agent/
│   │   ├── MainActivity.kt              # نقطة الدخول
│   │   ├── agent/
│   │   │   ├── AgentCore.kt            # النواة الرئيسية
│   │   │   ├── Router.kt               # توجيه الأوامر
│   │   │   ├── SkillManager.kt         # إدارة المهارات
│   │   │   └── MCPClient.kt            # اتصال MCP
│   │   ├── services/
│   │   │   ├── SimoAccessibilityService.kt  # التحكم بالشاشة
│   │   │   ├── AgentForegroundService.kt    # خدمة الخلفية
│   │   │   └── CallService.kt               # المكالمات
│   │   ├── ui/
│   │   │   ├── theme/
│   │   │   │   ├── Color.kt            # الألوان
│   │   │   │   └── Theme.kt            # الثيم الداكن
│   │   │   ├── dashboard/
│   │   │   │   ├── DashboardScreen.kt  # لوحة التحكم
│   │   │   │   └── DashboardViewModel.kt
│   │   │   ├── skills/
│   │   │   │   ├── Skill.kt            # نموذج المهارة
│   │   │   │   ├── SkillsScreen.kt     # شاشة المهارات
│   │   │   │   └── SkillsViewModel.kt
│   │   │   └── chat/
│   │   │       └── ChatScreen.kt       # واجهة الشات
│   │   └── utils/
│   │       └── AccessibilityUtils.kt
│   ├── res/xml/
│   │   └── accessibility_service_config.xml
│   └── AndroidManifest.xml
└── app/build.gradle.kts

## التثبيت

1. افتح Android Studio
2. أنشئ مشروع جديد: **Empty Compose Activity**
3. Package name: `com.simo.agent`
4. انسخ الملفات في مكانها
5. ابنِ المشروع (Build → Make Project)
6. فعّل **Accessibility Service** من الإعدادات

## الصلاحيات المطلوبة

- `BIND_ACCESSIBILITY_SERVICE` - للتحكم بالشاشة
- `CALL_PHONE` - للمكالمات
- `FOREGROUND_SERVICE` - للعمل في الخلفية
- `SYSTEM_ALERT_WINDOW` - للنوافذ العائمة
```
