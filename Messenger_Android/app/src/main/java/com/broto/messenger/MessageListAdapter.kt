package com.broto.messenger

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.broto.messenger.model.FirebaseMessage

class MessageListAdapter(
    var mMessages: ArrayList<FirebaseMessage>,
    var mUserId: String,
    var itemCallback: ItemClickedListener,
    var context: Context
) :
    RecyclerView.Adapter<MessageListAdapter.MessageHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        return MessageHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.message_list_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return mMessages.size
    }

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        val item = mMessages[position]
        holder.message.text = item.messageData

        if (mUserId == item.sender) {
            holder.chat_viewgroup.gravity = Gravity.RIGHT
            holder.message.background = context.getDrawable(R.drawable.message_sent_item_background)
        } else {
            holder.chat_viewgroup.gravity = Gravity.LEFT
            holder.message.background = context.getDrawable(R.drawable.message_received_item_background)
        }
        holder.chat_viewgroup.setOnClickListener {
            itemCallback.onItemClicked(item)
        }
    }

    fun updateMessageList(mMessages: ArrayList<FirebaseMessage>) {
        this.mMessages = mMessages
    }

    inner class MessageHolder(view: View): RecyclerView.ViewHolder(view) {
        var chat_viewgroup = view.findViewById<RelativeLayout>(R.id.rl_chat_item)
        var message = view.findViewById<TextView>(R.id.tv_chat_item_message)
    }

    interface ItemClickedListener {
        fun onItemClicked(item: FirebaseMessage)
    }
}