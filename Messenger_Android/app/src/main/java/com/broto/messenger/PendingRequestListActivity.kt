package com.broto.messenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.broto.messenger.jsonResponseModels.GetAllRequestsResponse
import com.broto.messenger.services.CoreService
import kotlinx.android.synthetic.main.activity_pending_request_list.*

class PendingRequestListActivity : AppCompatActivity() {

    private val TAG ="PendingRequestList"
    private var mPendingList: ArrayList<GetAllRequestsResponse.RequestedUserDetails>? = null
    private var adapter: PendingRequestListAdapter? = null

    private var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_request_list)

        username = intent.getStringExtra(Constants.KEY_USERNAME)
        populateUi()

    }

    private fun populateUi() {
        mPendingList = CoreService.getInstance()?.mPendingRequestList
        if (mPendingList.isNullOrEmpty()) {
            Log.d(TAG, "No pending requests. Ignore")
            finish()
            return
        }
        tv_pending_request_username.text = username
        tv_pending_request_count.setText(tv_pending_request_count.text.toString() + "${mPendingList?.size}")
        rv_pending_list.layoutManager = LinearLayoutManager(this)
        adapter = PendingRequestListAdapter(mPendingList!!, this, this)
        rv_pending_list.adapter = adapter
    }
}
