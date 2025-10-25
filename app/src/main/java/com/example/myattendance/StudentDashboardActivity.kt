package com.example.myattendance

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import android.util.Log

class StudentDashboardActivity : ComponentActivity() {
    private val TAG = "StudentDashboardActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            val tv = TextView(this)
            tv.text = getString(R.string.student_dashboard)
            tv.textSize = 24f
            setContentView(tv)
        } catch (e: Exception) {
            Log.e(TAG, "onCreate failed", e)
            Toast.makeText(this, "Startup error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
