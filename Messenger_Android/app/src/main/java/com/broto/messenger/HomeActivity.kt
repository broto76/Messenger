package com.broto.messenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.broto.messenger.jsonResponseModels.AllFriendsResponse
import com.broto.messenger.retrofitServices.UserdataService
import com.broto.messenger.retrofitServices.Utility
import com.broto.messenger.jsonResponseModels.UserDetailsResponse
import com.broto.messenger.model.HomeFriendList
import com.broto.messenger.services.CoreService
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity(), CoreService.JobCompleteCallback {

    private val TAG = "HomeActivity"
    private var mUserDetails: UserDetailsResponse? = null

    private var mFriendList: ArrayList<HomeFriendList> = ArrayList()
    private var friendListAdapter: FriendChatListAdapter? = null

    var isInForeground = false
    var isRequestActivityOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        ll_home_username.visibility = View.INVISIBLE

        populateUserName()

        CoreService.getInstance()?.mIsHomeActivityRunning = true
        CoreService.getInstance()?.setuserId(
            com.broto.messenger.Utility.getPreference(Constants.SP_KEY_LOGIN_USERID, this))
    }

    override fun onResume() {
        super.onResume()
        synchronized(isInForeground) {
            isInForeground = true
        }
        populateFriendData()
        CoreService.getInstance()?.fetchPendingRequestsRemote(this)
    }

    override fun onPause() {
        synchronized(isInForeground) {
            isInForeground = false
        }
        super.onPause()
        CoreService.getInstance()?.unregisterMonitorUnreadMessages()
    }

    override fun onDestroy() {
        super.onDestroy()
        CoreService.getInstance()?.mIsHomeActivityRunning = false
    }

    private fun populateUserName() {

        val loginToken = com.broto.messenger.Utility.getPreference(Constants.SP_KEY_LOGIN_TOKEN,
            applicationContext)
        Log.d(TAG, "Token: $loginToken")
        if (loginToken.isEmpty()) {
            Log.d(TAG, "Token not found. Abort")
            finish()
            return
        }

        val webservice = Utility.getRetrofitService().create(UserdataService::class.java)
        webservice.getUserDetails(loginToken).enqueue(object: Callback<UserDetailsResponse> {
            override fun onFailure(call: Call<UserDetailsResponse>?, t: Throwable?) {
                Log.e(TAG, "GetUserData Failed. Message: ${t?.message}")
            }

            override fun onResponse(
                call: Call<UserDetailsResponse>?,
                response: Response<UserDetailsResponse>?
            ) {
                val body = response?.body()
                Log.d(TAG, "GetUserData ResponseCode: ${response?.code()}")

                if ((response?.code()?:0) == 200) {
                    tv_username.text = "${body?.name}"
                    ll_home_username.visibility = View.VISIBLE
                    mUserDetails = body
                }
            }

        })
    }

    private fun populateFriendData() {
        rv_friend_list.visibility = View.GONE
        rv_friend_list.layoutManager = LinearLayoutManager(this)

        val loginToken = com.broto.messenger.Utility.getPreference(Constants.SP_KEY_LOGIN_TOKEN,
            applicationContext)
        Log.d(TAG, "Token: $loginToken")
        if (loginToken.isEmpty()) {
            Log.d(TAG, "Token not found. Abort")
            finish()
            return
        }
        val webservice = Utility.getRetrofitService().create(UserdataService::class.java)
        webservice.getAllFriends(loginToken).enqueue(object: Callback<AllFriendsResponse> {
            override fun onFailure(call: Call<AllFriendsResponse>?, t: Throwable?) {
                Log.e(TAG, "GetAllFriends Failed. Message: ${t?.message}")
            }

            override fun onResponse(
                call: Call<AllFriendsResponse>?,
                response: Response<AllFriendsResponse>?
            ) {
                val body = response?.body()
                Log.d(TAG, "GetAllFriends ResponseCode: ${response?.code()}")
                Log.d(TAG, "FriendList: $body")

                if ((response?.code()?:0) == 200) {
                    //tv_username.text = "Welcome\n${body?.name}"
                    //mFriendList = body?.friendList
                    mFriendList.clear()
                    body?.returnList?.forEach {
                        mFriendList.add(HomeFriendList(it.name, it.phoneNumber, it._id, 0))
                    }
                    populateFriendList()
                }
            }

        })
    }

    private fun populateFriendList() {
        CoroutineScope(Dispatchers.Main).launch {
            friendListAdapter = FriendChatListAdapter(mFriendList, this@HomeActivity)
            rv_friend_list.adapter = friendListAdapter
            friendListAdapter?.notifyDataSetChanged()
            rv_friend_list.visibility = View.VISIBLE
            CoreService.getInstance()?.registerMonitorUnreadMessages(mFriendList, friendListAdapter!!)
        }
    }

    fun fabAddFriend(view: View) {
        startActivity(Intent(this, FindFriendActivity::class.java))
    }

    override fun onjobcompleted(status: Int) {
        var pendingRequestList = CoreService.getInstance()?.mPendingRequestList
        if (pendingRequestList == null || pendingRequestList.isEmpty()) {
            Log.d(TAG, "No pending requests.")
            return
        }
        CoroutineScope(Dispatchers.Main).launch {
            ll_home_username.setOnClickListener {
                ll_home_username.setOnClickListener(null)
                startPendingRequestActivity()
            }
            iv_request_flicker.visibility = View.VISIBLE
            while (isInForeground) {
                iv_request_flicker.setImageResource(R.drawable.ic_person_solid_primary_color)
                delay(Constants.DELAY_PENDING_FLICKERING)
                iv_request_flicker.setImageResource(R.drawable.ic_person_stroke_primary_color)
                delay(Constants.DELAY_PENDING_FLICKERING)
            }
            iv_request_flicker.visibility = View.GONE
        }
    }

    fun startPendingRequestActivity() {
        isRequestActivityOpened = true
        val intent = Intent(this, PendingRequestListActivity::class.java)
        intent.putExtra(Constants.KEY_USERNAME, mUserDetails?.name)
        startActivity(intent)
    }
}
