package com.broto.messenger

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.broto.messenger.Constants.Companion.FIREBASE_DATABASE
import com.broto.messenger.model.FirebaseMessage
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chat_data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ChatDataActivity : AppCompatActivity(), MessageListAdapter.ItemClickedListener {

    private val TAG = "ChatDataActivity"

    private lateinit var userId: String
    private lateinit var remoteUserId: String

    private lateinit var database: FirebaseDatabase

    private var messageNodeKey: String = ""
    private var mChatReference: DatabaseReference? = null
    private var mChatReferenceListener: ChildEventListener? = null

    private var mMessages: ArrayList<FirebaseMessage> = ArrayList()
    private lateinit var mMessagesAdapter: MessageListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_data)

        userId = Utility.getPreference(Constants.SP_KEY_LOGIN_USERID, this)
        remoteUserId = intent.getStringExtra(Constants.KEY_REMOTE_USERID)?:""

        Log.d(TAG, "userId: $userId")
        Log.d(TAG, "remoteUserId: $remoteUserId")

        setAdjustScreen()
        setUpRecyclerView()
        addFirebaseDatabaseListener()

    }

    private fun setAdjustScreen() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    private fun setUpRecyclerView() {
        mMessagesAdapter = MessageListAdapter(mMessages, userId, this, this)
        rv_message_list.adapter = mMessagesAdapter
        val lm = LinearLayoutManager(this)
        lm.stackFromEnd = true
        lm.reverseLayout = false
        rv_message_list.layoutManager = lm
    }

    private fun addFirebaseDatabaseListener() {
        messageNodeKey = Utility.getFirebaseMessageKeyName(userId, remoteUserId)
        Log.d(TAG, "addFirebaseDatabaseListener: $messageNodeKey")
        database = Firebase.database(FIREBASE_DATABASE)
        mChatReference = database.getReference(messageNodeKey)

        mChatReferenceListener = object: ChildEventListener {
            override fun onCancelled(error: DatabaseError) {
                return
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                return
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val newMessage = snapshot.getValue(FirebaseMessage::class.java)
                val message = mMessages.find {
                    it.timestamp == newMessage?.timestamp
                }
                message?.messageData = newMessage?.messageData
                mMessagesAdapter.notifyDataSetChanged()
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(FirebaseMessage::class.java)
                Log.d(TAG,"Message Added: $message")
                if (message != null) {
                    mMessages.add(message)
                    mMessagesAdapter.notifyDataSetChanged()
                    rv_message_list.smoothScrollToPosition(mMessages.size -1)
                    updateMessageReadStatus(message, snapshot.key)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val updatedList = mMessages.filter {
                    it.timestamp != snapshot.getValue(FirebaseMessage::class.java)?.timestamp
                }
                mMessages = ArrayList(updatedList)
                mMessagesAdapter.updateMessageList(mMessages)
                mMessagesAdapter.notifyDataSetChanged()
                rv_message_list.smoothScrollToPosition(mMessages.size -1)
            }

        }

        mChatReference!!.addChildEventListener(mChatReferenceListener!!)

    }

    fun sendMessage(view: View) {
        val message = et_message_data.text.toString()
        if (message.isEmpty()) {
            return
        }

        val currentStamp = System.currentTimeMillis()
        val messageObject = FirebaseMessage(message, userId, currentStamp, false)
        val messageKey = messageNodeKey + "_" + currentStamp
        CoroutineScope(Dispatchers.IO).launch {
            mChatReference?.child(messageKey)?.setValue(messageObject)
        }
        et_message_data.setText("")

    }

    fun updateMessageReadStatus(message: FirebaseMessage?, key: String?) {
        if (message == null || key == null) {
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            if (message.sender == userId) {
                Log.d(TAG, "Dont update read status from owner.")
            } else {
                message.isRead = true
                mChatReference?.child(key)?.setValue(message)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (mChatReferenceListener != null) {
            mChatReference?.removeEventListener(mChatReferenceListener!!)
        }
    }

    override fun onItemClicked(item: FirebaseMessage) {
//        val messageKey = messageNodeKey + "_" + item.Timestamp.toString()
//        item.MessageData = item.MessageData + "1"
//        mChatReference?.child(messageKey)?.setValue(item)
        return
    }
}
