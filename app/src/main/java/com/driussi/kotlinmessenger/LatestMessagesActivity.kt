package com.driussi.kotlinmessenger

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.driussi.kotlinmessenger.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class LatestMessagesActivity : AppCompatActivity() {

    // Current user to pass around
    companion object {
        var currentUser: User? = null
    }

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        supportActionBar?.title = "Recent chats"

        database = Firebase.database.reference

        fetchCurrentUser()
        checkLogin()
    }

    // Gets current user
    private fun fetchCurrentUser() {

        val uid = FirebaseAuth.getInstance().uid
        val ref = Firebase.database.getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                currentUser = snapshot.getValue(User::class.java)
                Log.d("userpickup", currentUser?.username.toString())
            }

            override fun onCancelled(error: DatabaseError) {}

        })

    }

    // Redirects to LoginActivity if not logged in
    private fun checkLogin() {
        val uid = FirebaseAuth.getInstance().uid

        if (uid == null) {
            backToLogin()
        }
    }

    // Redirects to RegisterActivity
    private fun backToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    // Makes top right menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Reacts to menu selection
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item?.itemId) {

            // Redirects to New MessageActivity
            R.id.newMessage -> {

                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            //Signs out user
            R.id.signOut -> {

                FirebaseAuth.getInstance().signOut()
                backToLogin()
            }
            //Updates user profile pic
            R.id.pic -> {

                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 0)
            }

        }
        return super.onOptionsItemSelected(item)
    }


    // Updates Firebase with new image selection
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {

            val uri = data.data

            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

            if (uri == null) return

            ref.putFile(uri).addOnSuccessListener {

                Log.d("imageupload ", "${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {

                    // Actual logic
                    database.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("photoURL").setValue(it.toString())


                }
            }
        }
    }
}