package com.broto.messenger

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.broto.messenger.Constants.Companion.FIREBASE_DATABASE
import com.broto.messenger.model.FirebaseMessage
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chat_data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ChatDataActivity : AppCompatActivity() {

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
//        fetchNodeName()
        addFirebaseDatabaseListener()

    }

    private fun setAdjustScreen() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        /*android:windowSoftInputMode="adjustPan|adjustResize"*/
    }

    private fun setUpRecyclerView() {
        mMessagesAdapter = MessageListAdapter(mMessages, userId, this)
        rv_message_list.adapter = mMessagesAdapter
        rv_message_list.layoutManager = LinearLayoutManager(this)
    }

    private fun addFirebaseDatabaseListener() {
        messageNodeKey = Utility.getFirebaseMessageKeyName(userId, remoteUserId)
        Log.d(TAG, "addFirebaseDatabaseListener: $messageNodeKey")
        database = Firebase.database(FIREBASE_DATABASE)
        mChatReference = database.getReference(messageNodeKey)

        mChatReferenceListener = object: ChildEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(FirebaseMessage::class.java)
                Log.d(TAG,"Message Added: $message")
                if (message != null) {
                    mMessages.add(message)
                    mMessagesAdapter.notifyDataSetChanged()
                    rv_message_list.smoothScrollToPosition(mMessages.size -1)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val updatedList = mMessages.filter {
                    it.Timestamp != snapshot.getValue(FirebaseMessage::class.java)?.Timestamp
                }
                mMessages = ArrayList(updatedList)
                mMessagesAdapter.updateMessageList(mMessages)
                mMessagesAdapter.notifyDataSetChanged()
                rv_message_list.smoothScrollToPosition(mMessages.size -1)
            }

        }

        mChatReference!!.addChildEventListener(mChatReferenceListener!!)

    }

//    private fun fetchNodeName() {
//        database = Firebase.database(FIREBASE_DATABASE)
//        val key1 = userId + "_" + remoteUserId
//        val key2 = remoteUserId + "_" + userId
//
//        val reference1 = database.getReference(key1)
//        val reference2 = database.getReference(key2)
//
//        reference1.addValueEventListener(object : ValueEventListener {
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                Log.d(TAG, "$key1 child: ${snapshot.hasChildren()}")
//                if (snapshot.hasChildren()) {
//                    messageNodeKey = key1
//                    addFirebaseDatabaseListener()
//                }
//                reference1.removeEventListener(this)
//            }
//
//        })
//
//        reference2.addValueEventListener(object : ValueEventListener {
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                Log.d(TAG, "$key2 child: ${snapshot.hasChildren()}")
//                if (snapshot.hasChildren()) {
//                    messageNodeKey = key2
//                    addFirebaseDatabaseListener()
//                }
//                reference2.removeEventListener(this)
//            }
//        })
//    }

    fun sendMessage(view: View) {
        val message = et_message_data.text.toString()
        if (message.isEmpty()) {
            return
        }

//        if (messageNodeKey.isEmpty()) {
//            messageNodeKey = userId + "_" + remoteUserId
//            addFirebaseDatabaseListener()
//            Log.d(TAG,"Empty messageNodeKey is updated with $messageNodeKey")
//        }

        val currentStamp = System.currentTimeMillis()
        val messageObject = FirebaseMessage(message, userId, currentStamp)
        val messageKey = messageNodeKey + "_" + currentStamp
        CoroutineScope(Dispatchers.IO).launch {
            mChatReference?.child(messageKey)?.setValue(messageObject)
        }
        et_message_data.setText("")

    }

    override fun onDestroy() {
        super.onDestroy()
        if (mChatReferenceListener != null) {
            mChatReference?.removeEventListener(mChatReferenceListener!!)
        }
    }
}
