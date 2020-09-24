package com.driussi.kotlinmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*


class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    //    private lateinit var database: DatabaseReference
    val database = Firebase.database


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth


        register.setOnClickListener {

            registerUser()

        }

    }

    private fun registerUser() {

        if (email.text.toString().isEmpty() || password.text.toString().isEmpty() || name.toString().isEmpty()) {
            Toast.makeText(this, "Please enter your information", Toast.LENGTH_SHORT).show()
            return

        } else {

            auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                    .addOnCompleteListener(this) {
                        if (!it.isSuccessful) return@addOnCompleteListener

                        val user = auth!!.currentUser
                        writeNewUser(user!!.uid, user.displayName, user.email)
                        Toast.makeText(this, "Registered ${it.result?.user.toString()} succesfully!", Toast.LENGTH_SHORT).show()


                    }.addOnFailureListener {
                        Toast.makeText(this, "${it.message}!", Toast.LENGTH_LONG).show()

                    }
        }
    }

    private fun writeNewUser(userId: String, username: String?, email: String?) {

        val user = User(username, email)

        database.reference.child("users").child(userId)
                .child("email").setValue(email)

        database.reference.child("users").child(userId)
                .child("uuid").setValue(userId)

    }

    fun gotoLogin(view: View) {

        val intent = Intent(this, LoginActivity::class.java)

        startActivity(intent)
        finish()


    }
}

@IgnoreExtraProperties
data class User(
        var username: String? = "",
        var email: String? = ""
)
