package com.broto.messenger.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.broto.messenger.*
import com.broto.messenger.jsonResponseModels.PostTokenValidityRequest
import com.broto.messenger.jsonResponseModels.PostTokenValidityResponse
import com.broto.messenger.model.FirebaseMessage
import com.broto.messenger.model.HomeFriendList
import com.broto.messenger.retrofitServices.AuthenticationService
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CoreService : Service() {

    private val TAG = "CoreService"

    var mUserId: String? = null

    var mIsHomeActivityRunning = false
    var mFriendList: ArrayList<HomeFriendList>? = null
    private lateinit var mDatabase: FirebaseDatabase
    var mFirebaseChildEventListenerList: ArrayList<ChildEventListener> = ArrayList()
    var mFirebaseChatReferenceList: ArrayList<DatabaseReference> = ArrayList()

    var mAdapter: FriendChatListAdapter? = null

    companion object {

        private var mServiceInstance: CoreService? = null

        fun getInstance(): CoreService? {
            return mServiceInstance
        }
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        mServiceInstance = this
        mDatabase = Firebase.database(Constants.FIREBASE_DATABASE)
    }

//    fun startJob() {
//        GlobalScope.launch {
//            var counter = 0
//            while(true) {
//                Log.d(TAG, "Counter: $counter")
//                counter++
//                delay(1000)
//            }
//        }
//    }

    fun checkTokenValid(callback: JobCompleteCallback) {
        val loginToken = Utility.getPreference(Constants.SP_KEY_LOGIN_TOKEN, applicationContext)
        val userId = Utility.getPreference(Constants.SP_KEY_LOGIN_USERID, applicationContext)

        Log.d(TAG, "Token: $loginToken")
        if (loginToken.isEmpty() || userId.isEmpty()) {
            Log.d(TAG, "Token/UserID not found. Verification failed")
            callback.onjobcompleted(1)
        } else {
            // Verify if the token is valid
            val authService = com.broto.messenger.retrofitServices.Utility.getRetrofitService()
                .create(AuthenticationService::class.java)
            authService.postTokenValidity(PostTokenValidityRequest(loginToken))
                .enqueue(object: Callback<PostTokenValidityResponse> {
                override fun onFailure(call: Call<PostTokenValidityResponse>?, t: Throwable?) {
                    Log.e(TAG, "Token verification Failed. Message: ${t?.message}")
                    callback.onjobcompleted(JobCompleteCallback.RESPONSE_FAILED)
                }

                override fun onResponse(
                    call: Call<PostTokenValidityResponse>?,
                    response: Response<PostTokenValidityResponse>?
                ) {
                    Log.d(TAG, "PostTokenValidity ResponseCode: ${response?.code()}")
                    Log.d(TAG, "PostTokenValidity response: ${response?.body()}")
                    if (response?.body()?.tokenStatus ==
                        PostTokenValidityResponse.TOKEN_STATUS_VALID) {
                        Log.d(TAG, "Token is valid.")
                        if (response?.body()?.userId == userId) {
                            Log.d(TAG, "UserID match success")
                            callback.onjobcompleted(JobCompleteCallback.RESPONSE_SUCCESS)
                        } else {
                            Log.d(TAG, "UserID match failed")
                            callback.onjobcompleted(JobCompleteCallback.RESPONSE_FAILED)
                        }
                    } else {
                        Log.d(TAG, "Token is invalid.")
                        callback.onjobcompleted(JobCompleteCallback.RESPONSE_FAILED)
                    }
                }

            })
        }
    }

    fun registerMonitorUnreadMessages(list: ArrayList<HomeFriendList>, adapter: FriendChatListAdapter) {
        if (mFirebaseChatReferenceList.isNotEmpty() && mFirebaseChildEventListenerList.isNotEmpty()) {
            cleanUpUnreadMonitor()
        }
        mFirebaseChatReferenceList.clear()
        mFirebaseChatReferenceList.clear()
        mFriendList = list
        mAdapter = adapter
        for (friend in mFriendList!!) {
            val messageNodeKey = Utility.getFirebaseMessageKeyName(mUserId?:"", friend._id)
            val mChatReference = mDatabase.getReference(messageNodeKey)
            val mChatReferenceListener = object : ChildEventListener {
                override fun onCancelled(error: DatabaseError) {
                    return
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    return
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    return
                }

                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = snapshot.getValue(FirebaseMessage::class.java)
                    val key = snapshot.key
                    if (message == null || key == null) {
                        Log.d(TAG, "regiserMonitorUnreadMessages:: onChildAdded:: " +
                                "Snapshot Key/Message is null")
                        return
                    }
                    if (message.sender == mUserId || message.isRead == true) {
                        Log.d(TAG,"regiserMonitorUnreadMessages:: onChildAdded:: Ignore Message")
                        return
                    }
                    val friendItem = mFriendList?.find {
                        it._id == message.sender
                    }
                    if (friendItem == null) {
                        Log.d(TAG, "regiserMonitorUnreadMessages:: onChildAdded:: " +
                                "No friendItem in mFriendList for id: ${message.sender}")
                        return
                    }
                    Log.d(TAG, "Increment counter for friend: ${friendItem.name} for message: ${message}")
                    friendItem.unreadMessages++
                    adapter.notifyDataSetChanged()
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    return
                }

            }
            mChatReference.addChildEventListener(mChatReferenceListener)
            mFirebaseChatReferenceList.add(mChatReference)
            mFirebaseChildEventListenerList.add(mChatReferenceListener)
        }
    }

    fun unregisterMonitorUnreadMessages() {
        mFriendList = null
        mAdapter = null
        cleanUpUnreadMonitor()
    }

     @Synchronized fun cleanUpUnreadMonitor() {
        if (mFirebaseChildEventListenerList.isEmpty() || mFirebaseChatReferenceList.isEmpty()) {
            mFirebaseChatReferenceList.clear()
            mFirebaseChildEventListenerList.clear()
            return
        }

        if (mFirebaseChildEventListenerList.size != mFirebaseChatReferenceList.size) {
            Log.d(TAG,"cleanUpUnreadMonitor:: Reference List and Listener List Size mismatch")
            mFirebaseChatReferenceList.clear()
            mFirebaseChildEventListenerList.clear()
            return
        }

        for (counter in 0 until mFirebaseChatReferenceList.size) {
            mFirebaseChatReferenceList[counter]
                .removeEventListener(mFirebaseChildEventListenerList[counter])
        }

        mFirebaseChatReferenceList.clear()
        mFirebaseChildEventListenerList.clear()

    }

    fun setuserId(userId: String) {
        this.mUserId = userId
    }

    override fun onDestroy() {
        if (mFriendList != null || mAdapter != null) {
            cleanUpUnreadMonitor()
        }
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    interface JobCompleteCallback {
        /**
         * 0 - Success
         * 1 - Failed
         *
         */
        fun onjobcompleted(status: Int)

        companion object {
            const val RESPONSE_SUCCESS = 0
            const val RESPONSE_FAILED  = 1
        }
    }
}
