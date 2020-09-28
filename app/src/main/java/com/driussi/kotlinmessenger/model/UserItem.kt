package com.driussi.kotlinmessenger.model

import com.driussi.kotlinmessenger.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.user_row.view.*

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