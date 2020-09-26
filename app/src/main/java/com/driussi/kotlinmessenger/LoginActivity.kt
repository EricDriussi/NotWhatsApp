package com.driussi.kotlinmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        login.setOnClickListener {

            login()

        }
    }

    // Starts the login process if necessary data is provided
    private fun login() {
        if (emaillog.text.toString().isEmpty() || passwordlog.text.toString().isEmpty()) {
            Toast.makeText(this, "Please enter your information", Toast.LENGTH_SHORT).show()
            return

        } else {

            loginUser()
        }
    }

    // Checks the login process and reacts to it
    private fun loginUser() {

        auth.signInWithEmailAndPassword(emaillog.text.toString(), passwordlog.text.toString())
                .addOnCompleteListener(this) {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    // If authenticated
                }.addOnSuccessListener {

                    gotoMessages()

                }.addOnFailureListener {
                    Toast.makeText(this, "${it.message}!", Toast.LENGTH_LONG).show()

                }
    }

    // Redirects to LatestMessagesActivity
    private fun gotoMessages() {

        val intent = Intent(this, LatestMessagesActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    // Redirects to RegisterActivity
    fun gotoReg(view: View) {

        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()

    }
}