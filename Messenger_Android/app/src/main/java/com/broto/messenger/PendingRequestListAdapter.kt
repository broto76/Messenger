package com.broto.messenger

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.broto.messenger.jsonResponseModels.GetAllRequestsResponse
import com.broto.messenger.jsonResponseModels.PostAcceptMessageRequest
import com.broto.messenger.jsonResponseModels.PostAcceptMessageResponse
import com.broto.messenger.retrofitServices.UserdataService
import com.broto.messenger.retrofitServices.Utility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PendingRequestListAdapter(
    var mPendingList: ArrayList<GetAllRequestsResponse.RequestedUserDetails>,
    var mContext: Context,
    var mActivity: Activity
):
    RecyclerView.Adapter<PendingRequestListAdapter.PendingRequestHolder>() {

    private val TAG = "PendingRequestAdapter"

    inner class PendingRequestHolder(view: View): RecyclerView.ViewHolder(view) {
        var tv_friend_name = view.findViewById<TextView>(R.id.tv_friend_name)
        var tv_friend_number = view.findViewById<TextView>(R.id.tv_friend_number)
        var tv_user_id = view.findViewById<TextView>(R.id.tv_user_id)
        var pending_request_item_accept_request = view.
        findViewById<ImageButton>(R.id.pending_request_item_accept_request)
        var pending_request_item_reject_request = view.
        findViewById<ImageButton>(R.id.pending_request_item_reject_request)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingRequestHolder {
        return PendingRequestHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.pending_request_item, parent, false))
    }

    override fun getItemCount(): Int {
        return mPendingList.size
    }

    override fun onBindViewHolder(holder: PendingRequestHolder, position: Int) {
        val item = mPendingList[position]
        holder.tv_friend_name.text = item.remoteUserName
        holder.tv_friend_number.text = item.remoteUserPhoneNumber
        holder.tv_user_id.text = item.remoteUserId
        holder.pending_request_item_accept_request.setOnClickListener {
            val loginToken = com.broto.messenger.Utility.getPreference(Constants.SP_KEY_LOGIN_TOKEN,
                mContext)
            val webService = Utility.getRetrofitService().create(UserdataService::class.java)
            Log.d(TAG, "onAcceptRequestClicked: ${holder.tv_user_id.text}")

            webService.postAcceptMessageRequest(loginToken,
                PostAcceptMessageRequest(holder.tv_user_id.text.toString())
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

                        Toast.makeText(mContext,
                            response?.body()?.message, Toast.LENGTH_SHORT).show()
                        mActivity.finish()
                    }

                })
        }
        holder.pending_request_item_reject_request.setOnClickListener {
            Toast.makeText(mContext, "Gotcha!", Toast.LENGTH_SHORT).show()
        }
    }
}