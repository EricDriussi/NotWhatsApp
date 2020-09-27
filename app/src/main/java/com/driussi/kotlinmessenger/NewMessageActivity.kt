package com.driussi.kotlinmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row.view.*

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

// Manages user display in RecyclerView - activity_new_message
class UserItem(val user: User) : Item<GroupieViewHolder>() {

    override fun getLayout(): Int {

        return R.layout.user_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.itemView.username.text = user.username
        Picasso.get()
                .load(user.photoURL)
                .into(viewHolder.itemView.userpic)
    }
}