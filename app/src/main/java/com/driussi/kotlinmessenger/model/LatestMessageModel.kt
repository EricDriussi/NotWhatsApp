package com.driussi.kotlinmessenger.model

import android.net.Uri
import android.util.Log
import com.driussi.kotlinmessenger.LatestMessagesActivity
import com.driussi.kotlinmessenger.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.lm_row.view.*
import kotlinx.android.synthetic.main.user_row.view.*

// Manages message display in RecyclerView - latest_messages
class LatestMessageModel(val message: ChatMessage) : Item<GroupieViewHolder>() {

    var chatPartner: User? = null

    override fun getLayout(): Int {

        return R.layout.lm_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.itemView.lmmessage.text = message.text

        val partnerID: String =

        if (message.fromID == FirebaseAuth.getInstance().currentUser?.uid)
            message.toID.toString()
        else
            message.fromID.toString()


        val ref = FirebaseDatabase.getInstance().getReference("/users/$partnerID")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                chatPartner = snapshot.getValue(User::class.java)
                viewHolder.itemView.lmusername.text = chatPartner?.username

                Picasso.get()
                        .load(chatPartner?.photoURL)
                        .into(viewHolder.itemView.lmpic)
            }

            override fun onCancelled(error: DatabaseError) {}

        })


    }

}