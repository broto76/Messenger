package com.broto.messenger.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.broto.messenger.NotificationsActivity

class NotificationPagerAdapter(fm: FragmentManager,
                               private var activity: NotificationsActivity
    ): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                FindFriendFragment.getInstance(activity)
            }
            else -> {
                PendingRequestListFragment.getInstance(activity)
            }
        }
    }
//
//    override fun getPageTitle(position: Int): CharSequence? {
//        return when (position) {
//            0 -> {
//                "Find Friend"
//            }
//            else -> {
//                "Pending Requests"
//            }
//        }
//    }

    override fun getCount(): Int {
        return activity.mMaxTabs
    }

}