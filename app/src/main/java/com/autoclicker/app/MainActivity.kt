package com.autoclicker.app

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var etInterval: EditText
    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnStart = findViewById(R.id.btnStart)
        btnStop = findViewById(R.id.btnStop)
        etInterval = findViewById(R.id.etInterval)
        tvStatus = findViewById(R.id.tvStatus)
        btnStart.setOnClickListener {
            if (!isAccessibilityEnabled()) {
                Toast.makeText(this, "فعّل خدمة امكانية الوصول اولا!", Toast.LENGTH_LONG).show()
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            } else {
                val interval = etInterval.text.toString().toLongOrNull() ?: 1000L
                AutoClickService.interval = interval
                AutoClickService.isRunning = true
                tvStatus.text = "الحالة: يعمل"
            }
        }
        btnStop.setOnClickListener {
            AutoClickService.isRunning = false
            tvStatus.text = "الحالة: متوقف"
        }
        updateStatus()
    }
    override fun onResume() { super.onResume(); updateStatus() }
    private fun updateStatus() {
        tvStatus.text = when {
            !isAccessibilityEnabled() -> "الحالة: فعّل امكانية الوصول"
            AutoClickService.isRunning -> "الحالة: يعمل"
            else -> "الحالة: متوقف"
        }
    }
    private fun isAccessibilityEnabled(): Boolean {
        val service = packageName + "/" + AutoClickService::class.java.canonicalName
        val enabled = try {
            Settings.Secure.getInt(contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED)
        } catch (e: Exception) { 0 }
        if (enabled == 1) {
            val value = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            if (value != null) {
                val splitter = TextUtils.SimpleStringSplitter(':')
                splitter.setString(value)
                while (splitter.hasNext()) {
                    if (splitter.next().equals(service, ignoreCase = true)) return true
                }
            }
        }
        return false
    }
}
