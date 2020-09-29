package com.driussi.kotlinmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.driussi.kotlinmessenger.model.User
import com.driussi.kotlinmessenger.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*

class NewMessageActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var TAG: String = "NEW_MESSAGES"
    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Select User"

        auth = Firebase.auth
        database = Firebase.database.getReference("/users")

        fetchUsers()
    }

    // Fetch users from database and presents prepared objects for display
    private fun fetchUsers() {

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                // Fetching part
                addUsersToAdapter(snapshot)

                // Redirects to chat log
                gotoChatLog()

                // Displaying part
                recView_NewMessage.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun gotoChatLog() {
        adapter.setOnItemClickListener { item, view ->

            val userItem = item as UserModel

            intent = Intent(view.context, ChatLogActivity::class.java)
            intent.putExtra("USER_KEY", userItem.user)

            startActivity(intent)
            finish()
        }
    }

    private fun addUsersToAdapter(snapshot: DataSnapshot) {

        snapshot.children.forEach {

            // Preparing object part
            val user = it.getValue(User::class.java)
            Log.d(TAG, "Fetched user: ${user?.username.toString()}")

            if (user != null && user.uid != auth.uid)
                adapter.add(UserModel(user))
        }
    }

}
