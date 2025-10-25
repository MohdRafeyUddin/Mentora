package com.example.myattendance

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AdminDashboardActivity : AppCompatActivity() {
    private val TAG = "AdminDashboardActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_admin_dashboard)

            val btnAddStudent = findViewById<Button>(R.id.btnAddStudent)
            val btnAddFaculty = findViewById<Button>(R.id.btnAddFaculty)

            btnAddStudent.setOnClickListener {
                openAddUser("student")
            }

            btnAddFaculty.setOnClickListener {
                openAddUser("faculty")
            }

        } catch (e: Exception) {
            Log.e(TAG, "onCreate failed", e)
            Toast.makeText(this, "UI error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun openAddUser(role: String) {
        try {
            val intent = Intent(this, AddUserActivity::class.java)
            intent.putExtra("role", role)
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "openAddUser failed", e)
            Toast.makeText(this, "Navigation error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
