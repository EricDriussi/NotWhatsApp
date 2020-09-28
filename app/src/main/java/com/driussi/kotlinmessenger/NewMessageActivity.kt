package com.driussi.kotlinmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.driussi.kotlinmessenger.model.User
import com.driussi.kotlinmessenger.model.UserItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*

class NewMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        supportActionBar?.title = "Select User"

        fetchUsers()
    }

    // Fetch users from database and presents prepared objects for display
    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()

                // Fetching part
                snapshot.children.forEach {
                    Log.d("NewMessage", it.toString())

                    // Preparing object part
                    val user = it.getValue(User::class.java)

                    if (user != null && user.uid != FirebaseAuth.getInstance().uid)
                        adapter.add(UserItem(user))
                }

                // Redirects to chat log
                adapter.setOnItemClickListener { item, view ->

                    val userItem = item as UserItem

                    intent = Intent(view.context, ChatLogActivity::class.java)
                    intent.putExtra("USER_KEY", userItem.user)

                    startActivity(intent)
                    finish()
                }

                // Displaying part
                recView_NewMessage.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

}
