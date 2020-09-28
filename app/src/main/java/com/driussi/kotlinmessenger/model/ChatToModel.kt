package com.driussi.kotlinmessenger.model

import com.driussi.kotlinmessenger.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.user_row.view.*

// Manages message display in RecyclerView - activity_chat_log
class ChatToModel(val text: String, val user: User?) : Item<GroupieViewHolder>() {

    override fun getLayout(): Int {

        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.messageTO.text = text
        Picasso.get().load(user?.photoURL).into(viewHolder.itemView.toUserPic)
    }
}