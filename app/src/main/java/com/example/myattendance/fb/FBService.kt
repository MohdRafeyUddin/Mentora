package com.example.myattendance.fb

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.lang.reflect.Proxy

object FBService {
    private const val TAG = "FBService"
    private var initialized = false

    fun initialize(context: Context) {
        if (initialized) return
        try {
            FirebaseApp.initializeApp(context)
            initialized = true
            Log.i(TAG, "FirebaseApp initialized")
        } catch (e: Exception) {
            Log.w(TAG, "FirebaseApp.initializeApp failed: ${e.message}")
            // still set initialized to true so callers know we've tried
            initialized = true
        }
    }

    fun createUser(email: String, password: String, onSuccess: (String) -> Unit, onFailure: (Exception?) -> Unit) {
        try {
            val auth: FirebaseAuth = Firebase.auth
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid ?: ""
                        onSuccess(uid)
                    } else {
                        onFailure(task.exception)
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "createUser exception", e)
            onFailure(e)
        }
    }

    fun signIn(email: String, password: String, onSuccess: () -> Unit, onFailure: (Exception?) -> Unit) {
        try {
            val auth: FirebaseAuth = Firebase.auth
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onSuccess()
                    } else {
                        onFailure(task.exception)
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "signIn exception", e)
            onFailure(e)
        }
    }

    fun saveUser(email: String, name: String, role: String, uid: String?, onSuccess: () -> Unit, onFailure: (Exception?) -> Unit) {
        // Use reflection to call Firestore APIs to avoid compile-time dependency on Firebase Firestore classes
        try {
            val firestoreClass = Class.forName("com.google.firebase.firestore.FirebaseFirestore")
            val getInstance = firestoreClass.getMethod("getInstance")
            val dbInstance = getInstance.invoke(null)

            val collectionMethod = dbInstance.javaClass.getMethod("collection", String::class.java)
            val collRef = collectionMethod.invoke(dbInstance, "users")

            val docMethod = collRef.javaClass.getMethod("document", String::class.java)
            val docRef = docMethod.invoke(collRef, uid ?: email)

            val setMethod = docRef.javaClass.getMethod("set", Any::class.java)
            val userMap = hashMapOf<String, Any>("email" to email, "role" to role, "name" to name)
            val task = setMethod.invoke(docRef, userMap)

            // OnCompleteListener to detect success/failure
            val onCompleteInterface = Class.forName("com.google.android.gms.tasks.OnCompleteListener")
            val handler = Proxy.newProxyInstance(onCompleteInterface.classLoader, arrayOf(onCompleteInterface)) { _, method, args ->
                if (method.name == "onComplete") {
                    val taskObj = args[0]
                    val isSuccessful = taskObj.javaClass.getMethod("isSuccessful").invoke(taskObj) as Boolean
                    if (isSuccessful) {
                        onSuccess()
                    } else {
                        val ex = taskObj.javaClass.getMethod("getException").invoke(taskObj) as? Throwable
                        onFailure(ex as? Exception ?: Exception(ex?.message))
                    }
                }
                null
            }
            val addListenerMethod = task.javaClass.getMethod("addOnCompleteListener", onCompleteInterface)
            addListenerMethod.invoke(task, handler)
            return
        } catch (e: ClassNotFoundException) {
            Log.w(TAG, "Firestore classes not found; cannot save user: ${e.message}")
            onFailure(e)
            return
        } catch (e: Exception) {
            Log.e(TAG, "Reflection-based saveUser failed", e)
            onFailure(e)
            return
        }
    }

    fun signOut() {
        try {
            val auth: FirebaseAuth = Firebase.auth
            auth.signOut()
        } catch (e: Exception) {
            Log.w(TAG, "signOut failed: ${e.message}")
        }
    }
}
