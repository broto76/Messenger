package com.broto.messenger

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.broto.messenger.jsonResponseModels.AllFriendsResponse

class FriendChatListAdapter(var mFriendList: List<AllFriendsResponse.FriendDetails>,
                            var context: Context) : RecyclerView.Adapter<FriendChatListAdapter.FriendHolder>() {

    inner class FriendHolder(view: View): RecyclerView.ViewHolder(view) {
        val friend_name = view.findViewById<TextView>(R.id.tv_friend_name)
        val friend_number = view.findViewById<TextView>(R.id.tv_friend_number)
        val list_item = view.findViewById<RelativeLayout>(R.id.rl_list_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendHolder {
        return FriendHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.friend_list_home_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return mFriendList.size
    }

    override fun onBindViewHolder(holder: FriendHolder, position: Int) {
        val item = mFriendList[position]
        holder.friend_name.text = item.name
        holder.friend_number.text = item.phoneNumber
        holder.list_item.setOnClickListener {
            val intent = Intent(context, ChatDataActivity::class.java)
            intent.putExtra(Constants.KEY_REMOTE_USERID, item._id)
            context.startActivity(intent)
        }
    }
}