package com.broto.messenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.broto.messenger.fragments.NotificationPagerAdapter
import com.broto.messenger.services.CoreService
import kotlinx.android.synthetic.main.activity_notifications.*


class NotificationsActivity : AppCompatActivity() {

    private val TAG = "NotificationsActivity"
    val mMaxTabs = 2

    private lateinit var adapter: NotificationPagerAdapter
    var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        username = intent.getStringExtra(Constants.KEY_USERNAME)

        adapter = NotificationPagerAdapter(supportFragmentManager, this)
        vp_main_notifications.adapter = adapter
        setupTabView()
    }

    override fun onResume() {
        if (CoreService.getInstance()?.mPendingRequestList?.isNotEmpty() == true) {
            vp_main_notifications.currentItem = 1
        } else {
            vp_main_notifications.currentItem = 0
        }
        super.onResume()
    }

    fun setupTabView() {
        tabs_main_notification.setupWithViewPager(vp_main_notifications)
        tabs_main_notification.getTabAt(0)?.icon = getDrawable(R.drawable.ic_friend_add_icon)
        tabs_main_notification.getTabAt(1)?.icon = getDrawable(R.drawable.ic_notifications_active)
    }
}
