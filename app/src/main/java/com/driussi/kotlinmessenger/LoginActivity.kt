package com.driussi.kotlinmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var TAG: String = "LOGIN"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        loginBtn.setOnClickListener { chekcUserInput() }

        loginToMain.setOnClickListener { gotoReg() }
    }

    // Starts the login process if necessary data is provided
    private fun chekcUserInput() {
        if (emailLogin.text.toString().isEmpty() || passwordLogin.text.toString().isEmpty()) {
            Toast.makeText(this, "Please enter login information", Toast.LENGTH_SHORT).show()
            return

        } else {

            loginUser()
        }
    }

    // Checks the login process and reacts to it
    private fun loginUser() {

        auth.signInWithEmailAndPassword(emailLogin.text.toString(), passwordLogin.text.toString())
                .addOnCompleteListener(this) {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    // If authenticated
                }.addOnSuccessListener {

                    Log.d(TAG, "Logged in successfully")
                    gotoMessages()

                }.addOnFailureListener {
                    Toast.makeText(this, "Unable to login, check email and password", Toast.LENGTH_LONG).show()
                    Log.d(TAG, "${it.message}")
                }
    }

    // Redirects to LatestMessagesActivity
    private fun gotoMessages() {

        val intent = Intent(this, LatestMessagesActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    // Redirects to RegisterActivity
    fun gotoReg() {

        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }
}