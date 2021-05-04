package com.broto.messenger.fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.broto.messenger.NotificationsActivity
import com.broto.messenger.PendingRequestListAdapter

import com.broto.messenger.R
import com.broto.messenger.jsonResponseModels.GetAllRequestsResponse
import com.broto.messenger.services.CoreService
import kotlinx.android.synthetic.main.fragment_pending_request_list.*

/**
 * A simple [Fragment] subclass.
 */
class PendingRequestListFragment : Fragment() {

    private val TAG ="PendingRequestFragment"
    private var mPendingList: ArrayList<GetAllRequestsResponse.RequestedUserDetails>? = null
    private var adapter: PendingRequestListAdapter? = null

    companion object {
        private var mParentActivity: NotificationsActivity? = null

        fun getInstance(activity: NotificationsActivity): PendingRequestListFragment {
            mParentActivity = activity
            return PendingRequestListFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pending_request_list, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        populateUi()
    }

    private fun populateUi() {
        mPendingList = CoreService.getInstance()?.mPendingRequestList
//        if (mPendingList.isNullOrEmpty()) {
//            Log.d(TAG, "No pending requests. Ignore")
//            mParentActivity!!.finish()
//            return
//        }
        tv_pending_request_username.text = mParentActivity?.username
        tv_pending_request_count.setText(tv_pending_request_count.text.toString() + "${mPendingList?.size}")
        rv_pending_list.layoutManager = LinearLayoutManager(mParentActivity)
        adapter = PendingRequestListAdapter(mPendingList!!, mParentActivity!!, mParentActivity!!)
        rv_pending_list.adapter = adapter
    }


}
