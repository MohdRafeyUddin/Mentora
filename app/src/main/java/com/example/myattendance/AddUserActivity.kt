package com.example.myattendance

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.myattendance.fb.FBService
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class AddUserActivity : ComponentActivity() {

    private val TAG = "AddUserActivity"
    private val DEFAULT_PASSWORD = "123456"
    private val ADMIN_EMAIL = "admin@gmail.com"
    private val ADMIN_PASSWORD = "admin"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)

        // Initialize stub/service (detects whether real Firebase SDK is available)
        FBService.initialize(this)

        val role = intent?.getStringExtra("role") ?: "student"

        val etEmail = findViewById<EditText>(R.id.etUserEmail)
        val etName = findViewById<EditText>(R.id.etUserName)
        val btnCreate = findViewById<Button>(R.id.btnCreateUser)

        btnCreate.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val name = etName.text.toString().trim()
            if (email.isEmpty() || name.isEmpty()) {
                Toast.makeText(this, "Please enter name and email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            createUser(email, name, role)
        }
    }

    private fun createUser(email: String, name: String, role: String) {
        // Use FBService abstraction which uses real Firebase KTX
        FBService.createUser(email, DEFAULT_PASSWORD, onSuccess = { uid ->
            Log.i(TAG, "FBService created user uid=$uid")
            FBService.saveUser(email, name, role, uid, onSuccess = {
                // Success: show message and restore admin session
                Toast.makeText(this, "User added successfully", Toast.LENGTH_SHORT).show()

                // After successful creation and save, ensure admin remains signed in.
                // Sign out the newly created user (if any), then sign in the admin credentials.
                try {
                    FBService.signOut()
                } catch (e: Exception) {
                    Log.w(TAG, "FBService.signOut() threw: ${e.message}")
                }

                // Attempt to sign in admin again (real or stub will handle it)
                FBService.signIn(ADMIN_EMAIL, ADMIN_PASSWORD, onSuccess = {
                    Toast.makeText(this, "Admin restored session", Toast.LENGTH_SHORT).show()
                    finish()
                }, onFailure = { e ->
                    Log.w(TAG, "Failed to re-sign-in admin: ${e?.message}")
                    // Notify admin to manually re-login
                    Toast.makeText(this, "User added; please sign in again as admin.", Toast.LENGTH_LONG).show()
                    finish()
                })

            }, onFailure = { e ->
                Log.e(TAG, "FBService save failed", e)
                Toast.makeText(this, "Failed to save user: ${e?.message}", Toast.LENGTH_LONG).show()
            })
        }, onFailure = { e ->
            Log.e(TAG, "FBService create user failed", e)
            // If email already exists, inform admin; don't overwrite
            if (e is FirebaseAuthUserCollisionException) {
                Toast.makeText(this, "Email already exists. Choose a different email.", Toast.LENGTH_LONG).show()
                return@createUser
            }
            // For other errors, attempt to save to DB only (optional)
            FBService.saveUser(email, name, role, null, onSuccess = {
                Toast.makeText(this, "User saved to database (auth not created)", Toast.LENGTH_SHORT).show()
                finish()
            }, onFailure = { ex ->
                Log.e(TAG, "Failed to save user in fallback", ex)
                Toast.makeText(this, "Failed to create user: ${e?.message}", Toast.LENGTH_LONG).show()
            })
        })
    }
}
