package com.driussi.kotlinmessenger

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.driussi.kotlinmessenger.model.ChatFromModel
import com.driussi.kotlinmessenger.model.ChatMessage
import com.driussi.kotlinmessenger.model.ChatToModel
import com.driussi.kotlinmessenger.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*


class ChatLogActivity : AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()
    var toUser: User? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        toUser = intent.getParcelableExtra<User>("USER_KEY")
        auth = Firebase.auth
        database = Firebase.database

        listenMessage()
        configView()

        sendBtn.setOnClickListener { sendMessage() }
    }

    // Listener for updates to Firebase - messages
    private fun listenMessage() {

        database.getReference("/messages").addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val aMessage = snapshot.getValue(ChatMessage::class.java)

                if (aMessage != null) {
                    chatRecycler.scrollToPosition(adapter.itemCount - 1)

                    // Ensures correct messages are showing
                    if (aMessage.fromID == auth.uid && aMessage.toID == toUser!!.uid)
                        adapter.add(ChatFromModel(aMessage.text.toString(), LatestMessagesActivity.currentUser))
                    else if (aMessage.fromID == toUser!!.uid && aMessage.toID == auth.uid)
                        adapter.add(ChatToModel(aMessage.text.toString(), toUser))
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

        val ref = database.getReference("/messages").push()

        val id = ref.key
        val msg = textToSend.text.toString()
        val from = FirebaseAuth.getInstance().uid
        val to = toUser?.uid

        // Construct Chat Message
        val chatMessage = ChatMessage(id, msg, from, to, System.currentTimeMillis())

        if (msg != null && msg != "") {
            // Store Message in Firebase
            ref.setValue(chatMessage).addOnSuccessListener {

                textToSend.text = null
                chatRecycler.scrollToPosition(adapter.itemCount - 1)
            }
        }

        // Latest message storing logic
        val latestFROMRef = database.getReference("/latest-messages/$from/$to")
        latestFROMRef.setValue(chatMessage)

        val latestTORef = database.getReference("/latest-messages/$to/$from")
        latestTORef.setValue(chatMessage)

    }


    // Sets up the activity visuals
    private fun configView() {

        supportActionBar?.title = toUser?.username
        chatRecycler.adapter = adapter
        chatRecycler.scrollToPosition(adapter.itemCount - 1)
        setKeyBoardConfig()
    }

    // Ensures keyboard coherence when interacting with textfield
    private fun setKeyBoardConfig() {

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        // Stackoverflow (34102741) but converted to kotlin
        chatRecycler.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                chatRecycler.postDelayed({
                    chatRecycler.scrollToPosition(chatRecycler.adapter!!.itemCount - 1)
                }, 100)
            }
        }
    }
}

