package com.driussi.kotlinmessenger

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.driussi.kotlinmessenger.model.ChatMessage
import com.driussi.kotlinmessenger.model.LatestMessageModel
import com.xwray.groupie.GroupieViewHolder
import com.driussi.kotlinmessenger.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.GroupAdapter
import kotlinx.android.synthetic.main.activity_latest_messages.*
import java.util.*
import kotlin.collections.HashMap

class LatestMessagesActivity : AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()
    val messagesMap = HashMap<String, ChatMessage>()
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    // Current user to pass around
    companion object {
        var currentUser: User? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        supportActionBar?.title = "Recent chats"
        auth = Firebase.auth
        database = Firebase.database
        fetchCurrentUser()

        if (auth.uid == null) {
            backToLogin()
        }

        latestMessagesListener()
        gotoChatListener()
    }

    // Sends clicked user to ChatLogActivity
    private fun gotoChatListener() {

        // Redirects to chat log
        adapter.setOnItemClickListener { item, view ->

            val row = item as LatestMessageModel

            intent = Intent(this, ChatLogActivity::class.java)
            intent.putExtra("USER_KEY", row.chatPartner)
            startActivity(intent)
        }
    }

    // Listener for updates to Firebase - latest-messages
    private fun latestMessagesListener() {

        val fromID = FirebaseAuth.getInstance().uid
        database.getReference("/latest-messages/$fromID").addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                refreshLatestList(snapshot)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                refreshLatestList(snapshot)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}

        })

        latestMessageRecycler.adapter = adapter

    }

    // This is stupidly inefficient and it makes me MAD
    private fun refreshLatestList(snapshot: DataSnapshot) {

        adapter.clear()
        val message = snapshot.getValue(ChatMessage::class.java)!!
        messagesMap[snapshot.key!!] = message
        messagesMap.values.forEach { adapter.add(LatestMessageModel(it)) }
    }

    // Gets current user
    private fun fetchCurrentUser() {

        val uid = FirebaseAuth.getInstance().uid
        val ref = database.getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                currentUser = snapshot.getValue(User::class.java)
            }

            override fun onCancelled(error: DatabaseError) {}

        })

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

        when (item.itemId) {

            // Redirects to New MessageActivity
            R.id.newMessage -> {

                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            //Signs out user
            R.id.signOut -> {

                auth.signOut()
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
                    database.reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("photoURL").setValue(it.toString())


                }
            }
        }
    }
}