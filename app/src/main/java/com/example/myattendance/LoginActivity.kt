package com.example.myattendance

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.myattendance.fb.FBService

class LoginActivity : ComponentActivity() {

    private val TAG = "LoginActivity"
    private val ADMIN_EMAIL = "admin@gmail.com"
    private val ADMIN_PASSWORD = "admin"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize FBService stub
        FBService.initialize(this)

        val role = intent?.getStringExtra("role") ?: "student"

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass = etPassword.text.toString().trim()
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            performRegister(btnRegister, btnLogin, email, pass, role)
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass = etPassword.text.toString().trim()
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            performLogin(btnRegister, btnLogin, email, pass, role)
        }
    }

    private fun setButtonsEnabled(vararg buttons: View, enabled: Boolean) {
        for (b in buttons) b.isEnabled = enabled
    }

    private fun performRegister(registerButton: View, loginButton: View, email: String, password: String, role: String) {
        setButtonsEnabled(registerButton, loginButton, enabled = false)

        // Use FBService stub to create user and save to DB
        FBService.createUser(email, password, onSuccess = { uid ->
            Log.i(TAG, "FBService created user uid=$uid")
            FBService.saveUser(email, "", role, uid, onSuccess = {
                Toast.makeText(this, "Registration successful (stub)", Toast.LENGTH_SHORT).show()
                navigateToDashboard(role)
            }, onFailure = { e ->
                Log.e(TAG, "FBService save failed", e)
                Toast.makeText(this, "Registration succeeded (stub) but failed to save user: ${e?.message}", Toast.LENGTH_LONG).show()
            })
        }, onFailure = { e ->
            Log.e(TAG, "FBService create user failed", e)
            Toast.makeText(this, "Registration failed: ${e?.message}", Toast.LENGTH_LONG).show()
        })

        setButtonsEnabled(registerButton, loginButton, enabled = true)
    }

    private fun performLogin(registerButton: View, loginButton: View, email: String, password: String, role: String) {
        // Special-case admin credentials (local hard-coded)
        if (email.equals(ADMIN_EMAIL, ignoreCase = true) && password == ADMIN_PASSWORD) {
            Toast.makeText(this, "Admin login successful", Toast.LENGTH_SHORT).show()
            navigateToDashboard("admin")
            return
        }

        setButtonsEnabled(registerButton, loginButton, enabled = false)

        FBService.signIn(email, password, onSuccess = {
            Toast.makeText(this, "Login successful (stub)", Toast.LENGTH_SHORT).show()
            navigateToDashboard(role)
            setButtonsEnabled(registerButton, loginButton, enabled = true)
        }, onFailure = { e ->
            Log.e(TAG, "FBService signIn failed", e)
            Toast.makeText(this, "Login failed: ${e?.message}", Toast.LENGTH_LONG).show()
            setButtonsEnabled(registerButton, loginButton, enabled = true)
        })
    }

    private fun navigateToDashboard(role: String) {
        val intent = when (role.lowercase()) {
            "student" -> Intent(this, StudentDashboardActivity::class.java)
            "faculty" -> Intent(this, FacultyDashboardActivity::class.java)
            "admin" -> Intent(this, AdminDashboardActivity::class.java)
            else -> Intent(this, StudentDashboardActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}
