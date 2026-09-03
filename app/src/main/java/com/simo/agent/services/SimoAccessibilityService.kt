package com.simo.agent.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent
import android.util.Log

class SimoAccessibilityService : AccessibilityService() {

companion object {
var instance: SimoAccessibilityService? = null
private const val TAG = "SimoAccessibility"
}

override fun onServiceConnected() {
super.onServiceConnected()
instance = this
Log.d(TAG, "SimoAccessibilityService connected")
}

override fun onAccessibilityEvent(event: AccessibilityEvent?) {
// يمكن إضافة منطق هنا لاحقاً
}

override fun onInterrupt() {
Log.d(TAG, "Service interrupted")
}

override fun onDestroy() {
super.onDestroy()
instance = null
}

// نقر على إحداثيات معينة
fun click(x: Float, y: Float) {
val path = Path().apply { moveTo(x, y) }
val gesture = GestureDescription.Builder()
.addStroke(GestureDescription.StrokeDescription(path, 0, 50))
.build()
dispatchGesture(gesture, null, null)
}

// فتح تطبيق
fun openApp(packageName: String) {
val intent = packageManager.getLaunchIntentForPackage(packageName)
intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
startActivity(intent)
}

// العودة للشاشة الرئيسية
fun goHome() {
val intent = Intent(Intent.ACTION_MAIN).apply {
addCategory(Intent.CATEGORY_HOME)
flags = Intent.FLAG_ACTIVITY_NEW_TASK
}
startActivity(intent)
}
}