package com.autoclicker.app

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent

class AutoClickService : AccessibilityService() {
    private val handler = Handler(Looper.getMainLooper())
    private val clickRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                performClick(clickX, clickY)
                handler.postDelayed(this, interval)
            }
        }
    }
    companion object {
        var isRunning = false
        var interval = 1000L
        var clickX = 500f
        var clickY = 900f
    }
    override fun onServiceConnected() {
        super.onServiceConnected()
        handler.post(clickRunnable)
    }
    private fun performClick(x: Float, y: Float) {
        val path = Path().apply { moveTo(x, y) }
        dispatchGesture(
            GestureDescription.Builder()
                .addStroke(GestureDescription.StrokeDescription(path, 0, 50))
                .build(), null, null
        )
    }
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() { isRunning = false }
    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        handler.removeCallbacks(clickRunnable)
    }
}
