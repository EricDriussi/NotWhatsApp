package com.driussi.kotlinmessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.driussi.kotlinmessenger.model.ChatFromItem
import com.driussi.kotlinmessenger.model.ChatToItem
import com.driussi.kotlinmessenger.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()
    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        toUser = intent.getParcelableExtra<User>("USER_KEY")

        supportActionBar?.title = toUser?.username

        chatRecicler.adapter = adapter

        listenUp()

        sendBtn.setOnClickListener {
            sendMessage()
        }
    }

    // Listener for updates to Firebase - messages
    private fun listenUp() {

        val ref = FirebaseDatabase.getInstance().getReference("/messages").addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val aMessage = snapshot.getValue(ChatMessage::class.java)

                if (aMessage != null) {

                    if (aMessage.fromID == FirebaseAuth.getInstance().uid)
                        adapter.add(ChatFromItem(aMessage.text.toString(), LatestMessagesActivity.currentUser))
                    else {
                        adapter.add(ChatToItem(aMessage.text.toString(), toUser))
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

        })

    }

    // Stores message in Firebase
    private fun sendMessage() {
        val ref = Firebase.database.getReference("/messages").push()

        val id = ref.key
        val msg = textToSend.text.toString()
        val from = FirebaseAuth.getInstance().uid
        val to = intent.getParcelableExtra<User>("USER_KEY")?.uid

        val chatMessage = ChatMessage(id, msg, from, to, System.currentTimeMillis())

        if (msg != null) {
            ref.setValue(chatMessage).addOnSuccessListener {

                textToSend.setText(null)
            }
        }
    }
}

