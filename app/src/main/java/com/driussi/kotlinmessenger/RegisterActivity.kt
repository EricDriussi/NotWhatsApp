package com.driussi.kotlinmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

    private lateinit var database: DatabaseReference
//    val database = Firebase.database


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth
        database = Firebase.database.reference


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

                        var user = auth!!.currentUser
                        writeNewUser(user!!.uid, name.text.toString(), user.email)

                    }.addOnSuccessListener {

                        val intent = Intent(this, LatestMessagesActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)

                    }.addOnFailureListener {
                        Log.d("RegisterActivity: ", "${it.message}!")

                    }
        }
    }

    private fun writeNewUser(userId: String, username: String?, email: String?) {
        val user = User(username, email)
        database.child("users").child(userId).setValue(user)

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
