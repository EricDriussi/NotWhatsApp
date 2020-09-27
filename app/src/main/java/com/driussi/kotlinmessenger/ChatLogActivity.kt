package com.driussi.kotlinmessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.user_row.view.*

class ChatLogActivity : AppCompatActivity() {
    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        val user = intent.getParcelableExtra<User>("USER_KEY")

        supportActionBar?.title = user?.username

        chatRecicler.adapter = adapter

        listenUp()

        sendBtn.setOnClickListener {
            sendMessage()
        }
    }

    private fun listenUp() {

        val ref = FirebaseDatabase.getInstance().getReference("/messages").addChildEventListener(object: ChildEventListener{

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val aMessage = snapshot.getValue(ChatMessage::class.java)

                if(aMessage != null){

                    if (aMessage.fromID == FirebaseAuth.getInstance().uid)
                        adapter.add(ChatFromItem(aMessage.text.toString()))
                    else
                        adapter.add(ChatToItem(aMessage.text.toString()))
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

        })

    }

    private fun sendMessage() {
        val ref = Firebase.database.getReference("/messages").push()

        val id = ref.key
        val msg = textToSend.text.toString()
        val from = FirebaseAuth.getInstance().uid
        val to = intent.getParcelableExtra<User>("USER_KEY")?.uid
        val chatMessage = ChatMessage(id, msg, from, to, System.currentTimeMillis())

        ref.setValue(chatMessage).addOnSuccessListener {

            Log.d("message", "saved")
        }
    }
}

private fun setDummyData(): GroupAdapter<GroupieViewHolder>{
    val adapter = GroupAdapter<GroupieViewHolder>()

    adapter.add(ChatFromItem("FROM FROM"))
    adapter.add(ChatToItem("TO TO"))
    adapter.add(ChatToItem("TO TO"))
    adapter.add(ChatFromItem("FROM FROM"))
    adapter.add(ChatFromItem("FROM FROM"))
    adapter.add(ChatFromItem("FROM FROM"))
    adapter.add(ChatToItem("TO TO"))

    return adapter
}

// Manages message display in RecyclerView - activity_chat_log
class ChatFromItem(val text:  String ) : Item<GroupieViewHolder>() {

    override fun getLayout(): Int {

        return R.layout.chat_from_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.messageFROM.text = text

    }
}

// Manages message display in RecyclerView - activity_chat_log
class ChatToItem(val text:  String ) : Item<GroupieViewHolder>() {

    override fun getLayout(): Int {

        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.messageTO.text = text
    }
}