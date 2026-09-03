package com.simo.agent.utils

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityManager

object AccessibilityUtils {

fun isAccessibilityServiceEnabled(context: Context, serviceClass: Class<*>): Boolean {
val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)

val expectedId = "${context.packageName}/${serviceClass.canonicalName}"

for (service in enabledServices) {
if (service.id.equals(expectedId, ignoreCase = true)) {
return true
}
}
return false
}

fun openAccessibilitySettings(context: Context) {
val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
context.startActivity(intent)
}
}
