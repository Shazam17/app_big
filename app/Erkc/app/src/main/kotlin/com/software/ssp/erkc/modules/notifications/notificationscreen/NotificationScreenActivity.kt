package com.software.ssp.erkc.modules.notifications.notificationscreen

import android.os.Bundle
import android.support.v4.app.ShareCompat
import android.view.Menu
import android.view.MenuItem
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.delegates.extras
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.realm.models.RealmNotification
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.toString
import kotlinx.android.synthetic.main.activity_notification_screen.*
import java.util.*
import javax.inject.Inject


class NotificationScreenActivity : MvpActivity(), INotificationScreenView {

    @Inject lateinit var presenter: INotificationScreenPresenter

    val notificationId: String by extras(defaultValue = "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_screen)

        initViews()

        presenter.notificationId = notificationId

        presenter.onViewAttached()
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerNotificationScreenComponent.builder()
                .appComponent(appComponent)
                .notificationScreenModule(NotificationScreenModule(this))
                .build()
                .inject(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.notification_screen_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_share -> {
                presenter.onShareClick()
                return true
            }

            android.R.id.home -> {
                finish()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun showNotificationInfo(notification: RealmNotification) {
        with(notification) {
            deliveredDateText.text = deliveredDate!!.toString(Constants.NOTIFICATIONS_SCREEN_DATETIME_FORMAT)
            readDateText.text = readDate?.toString(Constants.NOTIFICATIONS_SCREEN_DATETIME_FORMAT) ?: getString(R.string.notification_screen_loading)
            titleText.text = title
            messageText.text = message
        }
    }

    override fun showReadDate(date: Date) {
        readDateText.text = date.toString(Constants.NOTIFICATIONS_SCREEN_DATETIME_FORMAT)
    }

    override fun showShareDialog(notification: RealmNotification) {
        ShareCompat.IntentBuilder.from(this)
                .setText(notification.message)
                .setType("text/plain")
                .startChooser()
    }

    private fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
