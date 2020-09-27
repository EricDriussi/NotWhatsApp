package com.driussi.kotlinmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        val user = intent.getParcelableExtra<User>("USER_KEY")

        supportActionBar?.title = user?.username

        val adapter = GroupAdapter<GroupieViewHolder>()

        adapter.add(ChatFromItem())
        adapter.add(ChatToItem())
        adapter.add(ChatToItem())
        adapter.add(ChatFromItem())
        adapter.add(ChatFromItem())
        adapter.add(ChatFromItem())
        adapter.add(ChatToItem())


        adapter.setOnItemClickListener { item, view ->
            intent = Intent(view.context, ChatLogActivity::class.java)
            startActivity(intent)
            finish()
        }
        chatRecicler.adapter = adapter

    }
}

// Manages message display in RecyclerView - activity_chat_log
class ChatFromItem() : Item<GroupieViewHolder>() {

    override fun getLayout(): Int {

        return R.layout.chat_from_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }
}

// Manages message display in RecyclerView - activity_chat_log
class ChatToItem() : Item<GroupieViewHolder>() {

    override fun getLayout(): Int {

        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }
}