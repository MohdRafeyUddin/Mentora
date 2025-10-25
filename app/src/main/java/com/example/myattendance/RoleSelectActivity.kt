// Create an Android Activity named RoleSelectActivity in Kotlin
// When the app opens, it should show three buttons: Student, Faculty, and Admin.
// Each button should navigate to LoginActivity and pass the selected role ("student", "faculty", or "admin") using Intent extras.
// Also generate the XML layout for this activity with a vertical LinearLayout, title text "Select Your Role", and buttons for each role.
package com.example.myattendance

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.view.Gravity
import android.view.ViewGroup
import android.graphics.Typeface

class RoleSelectActivity : ComponentActivity() {
    private val TAG = "RoleSelectActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            // Try to look up the layout resource by name at runtime. Some static analyzers or
            // generated resources might not be available to the checker used here, so resolve
            // at runtime and fall back to building the same UI programmatically.
            val layoutId = resources.getIdentifier("activity_role_select", "layout", packageName)

            if (layoutId != 0) {
                setContentView(layoutId)

                val studentButton = findViewById<Button>(R.id.button_student)
                val facultyButton = findViewById<Button>(R.id.button_faculty)
                val adminButton = findViewById<Button>(R.id.button_admin)

                studentButton.setOnClickListener { navigateToLogin("student") }
                facultyButton.setOnClickListener { navigateToLogin("faculty") }
                adminButton.setOnClickListener { navigateToLogin("admin") }

                // Ensure buttons are clickable and have feedback
                studentButton.isClickable = true
                studentButton.isFocusable = true
                facultyButton.isClickable = true
                facultyButton.isFocusable = true
                adminButton.isClickable = true
                adminButton.isFocusable = true
            } else {
                // Programmatic fallback UI (mirrors the XML layout)
                val paddingDp = 24
                val paddingPx = (paddingDp * resources.displayMetrics.density).toInt()

                val layout = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER_HORIZONTAL
                    setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }

                val title = TextView(this).apply {
                    text = "Select Your Role"
                    textSize = 24f
                    setTypeface(null, Typeface.BOLD)
                    val lp = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    lp.bottomMargin = (32 * resources.displayMetrics.density).toInt()
                    layoutParams = lp
                }

                val btnParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = (12 * resources.displayMetrics.density).toInt() }

                val studentButton = Button(this).apply {
                    text = "Student"
                    layoutParams = btnParams
                    setOnClickListener { navigateToLogin("student") }
                }

                val facultyButton = Button(this).apply {
                    text = "Faculty"
                    layoutParams = btnParams
                    setOnClickListener { navigateToLogin("faculty") }
                }

                val adminButton = Button(this).apply {
                    text = "Admin"
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    setOnClickListener { navigateToLogin("admin") }
                }

                layout.addView(title)
                layout.addView(studentButton)
                layout.addView(facultyButton)
                layout.addView(adminButton)

                setContentView(layout)
            }
        } catch (e: Exception) {
            Log.e(TAG, "onCreate failed", e)
            Toast.makeText(this, "Startup error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun navigateToLogin(role: String) {
        try {
            val intent = android.content.Intent(this, LoginActivity::class.java)
            intent.putExtra("role", role)
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "navigateToLogin failed", e)
            Toast.makeText(this, "Navigation error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Add onClick handlers referenced by layout
    fun onStudentClicked(view: android.view.View) {
        try {
            Log.i(TAG, "Student button clicked")
            Toast.makeText(this, "Student selected", Toast.LENGTH_SHORT).show()
            navigateToLogin("student")
        } catch (e: Exception) {
            Log.e(TAG, "onStudentClicked failed", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun onFacultyClicked(view: android.view.View) {
        try {
            Log.i(TAG, "Faculty button clicked")
            Toast.makeText(this, "Faculty selected", Toast.LENGTH_SHORT).show()
            navigateToLogin("faculty")
        } catch (e: Exception) {
            Log.e(TAG, "onFacultyClicked failed", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun onAdminClicked(view: android.view.View) {
        try {
            Log.i(TAG, "Admin button clicked")
            Toast.makeText(this, "Admin selected", Toast.LENGTH_SHORT).show()
            navigateToLogin("admin")
        } catch (e: Exception) {
            Log.e(TAG, "onAdminClicked failed", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
