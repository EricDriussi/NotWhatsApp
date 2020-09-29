package com.driussi.kotlinmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.driussi.kotlinmessenger.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*


class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var TAG: String = "REGISTER"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth
        database = Firebase.database.reference


        registerBtn.setOnClickListener { checkUserInput() }

        mainToLogin.setOnClickListener { gotoLogin() }
    }

    // Starts the registration process if necessary data is provided
    private fun checkUserInput() {

        if (emailReg.text.toString().isEmpty() || passwordReg.text.toString().isEmpty() || nameReg.toString().isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return

        } else {

            registerUser()
        }
    }

    // Checks the registration process and reacts to it
    private fun registerUser() {

        auth.createUserWithEmailAndPassword(emailReg.text.toString(), passwordReg.text.toString())

                // If authenticated
                .addOnCompleteListener(this) {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    val user = auth.currentUser
                    writeNewUser(user!!.uid, nameReg.text.toString(), user.email)

                    // If successfully stored
                }.addOnSuccessListener {

                    Log.d(TAG, "Registered user successfully")
                    gotoMessages()

                    // If something goes wrong
                }.addOnFailureListener {
                    Toast.makeText(this, "Unable to register, check email and username", Toast.LENGTH_LONG).show()
                    Log.d(TAG, "${it.message}")

                }
    }

    // Creates new user in firebase
    private fun writeNewUser(userId: String, username: String, email: String?) {
        val user = User(userId, username, email)
        database.child("users").child(userId).setValue(user)
    }

    // Redirects to LatestMessagesActivity
    private fun gotoMessages() {

        val intent = Intent(this, LatestMessagesActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    // Redirects to LoginActivity
    fun gotoLogin() {

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
