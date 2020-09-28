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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth
        database = Firebase.database.reference


        register.setOnClickListener {

            registration()
        }
    }

    // Starts the registration process if necessary data is provided
    private fun registration() {

        if (email.text.toString().isEmpty() || password.text.toString().isEmpty() || name.toString().isEmpty()) {
            Toast.makeText(this, "Please enter your information", Toast.LENGTH_SHORT).show()
            return

        } else {

            registerUser()
        }
    }

    // Checks the registration process and reacts to it
    private fun registerUser() {

        auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())

                // If authenticated
                .addOnCompleteListener(this) {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    val user = auth!!.currentUser
                    writeNewUser(user!!.uid, name.text.toString(), user.email)

                    // If successfully stored
                }.addOnSuccessListener {

                    gotoMessages()

                    // If something goes wrong
                }.addOnFailureListener {

                    Log.d("RegisterActivity: ", "${it.message}!")
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                }
    }

    // Creates new user in firebase
    private fun writeNewUser(userId: String, username: String, email: String?) {
        val user = User(userId, username, email)
        database.child("users").child(userId).setValue(user)
    }

    // Redirects to LatestMessagesActivity
    private fun gotoMessages(){

        val intent = Intent(this, LatestMessagesActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    // Redirects to LoginActivity
    fun gotoLogin(view: View) {

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
