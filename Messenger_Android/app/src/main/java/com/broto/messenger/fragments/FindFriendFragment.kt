package com.broto.messenger.fragments


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.broto.messenger.ChatDataActivity
import com.broto.messenger.Constants
import com.broto.messenger.NotificationsActivity

import com.broto.messenger.R
import com.broto.messenger.jsonResponseModels.*
import com.broto.messenger.retrofitServices.UserdataService
import com.broto.messenger.retrofitServices.Utility
import kotlinx.android.synthetic.main.fragment_find_friend.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class FindFriendFragment : Fragment() {

    companion object {
        private var mParentActivity: NotificationsActivity? = null

        fun getInstance(activity: NotificationsActivity): FindFriendFragment {
            mParentActivity = activity
            return FindFriendFragment()
        }
    }

    private val TAG = "FindFriendFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find_friend, container, false)
    }

    override fun onStart() {
        super.onStart()
        ll_friend_query.visibility = View.VISIBLE
        btn_friend_query_lookup.setOnClickListener {
            lookupFriend(it)
        }
        btn_friend_query_result_send_request.setOnClickListener {
            onSendRequestClicked(it)
        }
        btn_friend_query_result_send_message.setOnClickListener {
            onSendMessageClicked(it)
        }
        btn_friend_query_result_accept.setOnClickListener {
            onAcceptRequestClicked(it)
        }
        btn_friend_query_result_reject.setOnClickListener {
            onRejectRequestClicked(it)
        }
    }

    fun lookupFriend(view: View) {
        if (et_find_phoneNumber.text.toString().isEmpty()) {
            CoroutineScope(Dispatchers.Main).launch {
                tv_find_friend_error.text = "Invalid Input"
                tv_find_friend_error.visibility = View.VISIBLE

                delay(Constants.DELAY_ERROR_MESSAGE)
                tv_find_friend_error.text = ""
                tv_find_friend_error.visibility = View.GONE
            }
            return
        }

        val phoneNumber = et_find_phoneNumber.text.toString()
        val loginToken = com.broto.messenger.Utility.getPreference(
            Constants.SP_KEY_LOGIN_TOKEN,
            mParentActivity!!)

        try {
            val imm: InputMethodManager =
                mParentActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(mParentActivity!!.currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        ll_friend_query.visibility = View.GONE
        et_find_phoneNumber.setText("")
        ll_progress_find_friend.visibility = View.VISIBLE

        val webService = Utility.getRetrofitService().create(UserdataService::class.java)
        webService.getUserFromNumber(loginToken, phoneNumber)
            .enqueue(object : Callback<UserFromNumberResponse> {
                override fun onFailure(call: Call<UserFromNumberResponse>?, t: Throwable?) {
                    Log.e(TAG, "GetUserData Failed. Message: ${t?.message}")
                }

                override fun onResponse(
                    call: Call<UserFromNumberResponse>?,
                    response: Response<UserFromNumberResponse>?
                ) {
                    Log.d(TAG, "GetUserData ResponseCode: ${response?.code()}")
                    Log.d(TAG, "Data: ${response?.body()}")
                    if ((response?.code()?:0) == 200) {
                        displayFriendDetails(response?.body()!!)
                    } else if ((response?.code()?:0) == 404) {
                        CoroutineScope(Dispatchers.Main).launch {
                            ll_progress_find_friend.visibility = View.GONE
                            ll_friend_query.visibility = View.VISIBLE

                            tv_find_friend_error.text = "User not registered"
                            tv_find_friend_error.visibility = View.VISIBLE

                            delay(Constants.DELAY_ERROR_MESSAGE)
                            tv_find_friend_error.text = ""
                            tv_find_friend_error.visibility = View.GONE
                        }
                    }
                }

            })
    }

    fun displayFriendDetails(friend: UserFromNumberResponse) {
        tv_friend_query_result_name.text = friend.user.name
        tv_friend_query_result_number.text = friend.user.phoneNumber
        tv_friend_query_result_id.text = friend.user._id

        ll_progress_find_friend.visibility = View.GONE
        ll_friend_query_result.visibility = View.VISIBLE

        if (friend.status == UserStatus.ACCEPTED) {
            // Aleady a friend
            Log.d(TAG, "Aleady a friend")
            btn_friend_query_result_send_message.visibility = View.VISIBLE
        } else if (friend.status == UserStatus.REQUESTED) {
            // Friend Request sent.
            if (friend.statusOwner == friend.user._id) {
                // Friend is the owner. Show Accept/Ignore button to user
                Log.d(TAG, "Friend is the owner. Show Accept/Ignore button to user")
                ll_friend_query_result_accept_reject.visibility = View.VISIBLE
            } else {
                // User is the owner. Show pending and disabled button
                Log.d(TAG, "User is the owner. Show pending and disabled button")
                btn_friend_query_result_send_request.visibility = View.VISIBLE
                btn_friend_query_result_send_request.isEnabled = false
                btn_friend_query_result_send_request.text = "Pending Request"
            }
        } else if (friend.status == UserStatus.UNKNOWN) {
            // Not friend. Show send request option.
            if (friend.user._id == com.broto.messenger.Utility.getPreference(Constants.SP_KEY_LOGIN_USERID, mParentActivity!!)) {
                Log.d(TAG, "This is the current user.")
                tv_friend_query_result_name.text = tv_friend_query_result_name.text.toString() + "\n(current user)"
                return
            }
            Log.d(TAG, "Not friend. Show send request option.")
            btn_friend_query_result_send_request.text = "Send Request"
            btn_friend_query_result_send_request.visibility = View.VISIBLE
            btn_friend_query_result_send_request.isEnabled = true
        }
    }

    fun onSendRequestClicked(view: View) {

        ll_friend_query_result.visibility = View.GONE
        btn_friend_query_result_send_request.visibility = View.GONE

        ll_progress_find_friend.visibility = View.VISIBLE

        val loginToken = com.broto.messenger.Utility.getPreference(Constants.SP_KEY_LOGIN_TOKEN,
            mParentActivity!!)
        val webService = Utility.getRetrofitService().create(UserdataService::class.java)
        Log.d(TAG, "onSendRequestClicked: ${tv_friend_query_result_id.text}")
        webService.postSendMessageRequest(loginToken,
            PostSendMessageRequest(tv_friend_query_result_id.text.toString())
        )
            .enqueue(object: Callback<PostSendMessageResponse> {
                override fun onFailure(call: Call<PostSendMessageResponse>?, t: Throwable?) {
                    Log.e(TAG, "PostSendMessageRequest Failed. Message: ${t?.message}")
                }

                override fun onResponse(
                    call: Call<PostSendMessageResponse>?,
                    response: Response<PostSendMessageResponse>?
                ) {
                    Log.d(TAG, "PostSendMessageRequest ResponseCode: ${response?.code()}")
                    //Log.d(TAG, "Data: ${response?.body()}")
                    Toast.makeText(mParentActivity,
                        response?.body()?.message, Toast.LENGTH_SHORT).show()

                    ll_progress_find_friend.visibility = View.GONE
                    ll_friend_query.visibility = View.VISIBLE
                }

            })

    }

    fun onSendMessageClicked(view: View) {
        btn_friend_query_result_send_message.visibility = View.GONE
        val intent = Intent(mParentActivity, ChatDataActivity::class.java)
        intent.putExtra(Constants.KEY_REMOTE_USERID, tv_friend_query_result_id.text.toString())
        startActivity(intent)
        mParentActivity!!.finish()
    }

    fun onAcceptRequestClicked(view: View) {
        btn_friend_query_result_send_message.visibility = View.GONE
        ll_friend_query_result.visibility = View.GONE

        ll_progress_find_friend.visibility = View.VISIBLE

        val loginToken = com.broto.messenger.Utility.getPreference(Constants.SP_KEY_LOGIN_TOKEN,
            mParentActivity!!)
        val webService = Utility.getRetrofitService().create(UserdataService::class.java)
        Log.d(TAG, "onAcceptRequestClicked: ${tv_friend_query_result_id.text}")

        webService.postAcceptMessageRequest(loginToken,
            PostAcceptMessageRequest(tv_friend_query_result_id.text.toString())
        )
            .enqueue(object: Callback<PostAcceptMessageResponse> {
                override fun onFailure(call: Call<PostAcceptMessageResponse>?, t: Throwable?) {
                    Log.e(TAG, "PostAcceptMessageRequest Failed. Message: ${t?.message}")
                }

                override fun onResponse(
                    call: Call<PostAcceptMessageResponse>?,
                    response: Response<PostAcceptMessageResponse>?
                ) {
                    Log.d(TAG, "PostAcceptMessageRequest ResponseCode: ${response?.code()}")
                    Log.d(TAG, "PostAcceptMessageRequest Data: ${response?.body()}")

                    Toast.makeText(mParentActivity,
                        response?.body()?.message, Toast.LENGTH_SHORT).show()

                    ll_progress_find_friend.visibility = View.GONE
                    ll_friend_query.visibility = View.VISIBLE
                }

            })

    }

    fun onRejectRequestClicked(view: View) {
        btn_friend_query_result_send_message.visibility = View.GONE
        ll_friend_query_result.visibility = View.GONE
        ll_friend_query.visibility = View.VISIBLE
        Toast.makeText(mParentActivity, "Gotcha!", Toast.LENGTH_SHORT).show()
    }
}
