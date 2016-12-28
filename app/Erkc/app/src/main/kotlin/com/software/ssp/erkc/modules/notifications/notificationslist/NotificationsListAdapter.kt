package com.software.ssp.erkc.modules.notifications.notificationslist

import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.realm.models.RealmNotification
import com.software.ssp.erkc.extensions.toString
import kotlinx.android.synthetic.main.item_notification.view.*


class NotificationsListAdapter(val dataList: List<RealmNotification>,
                               val interactionListener: InteractionListener? = null) : RecyclerView.Adapter<NotificationsListAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return dataList.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindNotification(dataList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view, interactionListener)
    }

    class ViewHolder(view: View,
                     val interactionListener: InteractionListener?) : RecyclerView.ViewHolder(view) {

        fun bindNotification(notification: RealmNotification) {
            itemView.apply {

                notification.deliveredDate?.let {
                    dateTimeText.text = it.toString(if (DateUtils.isToday(it.time)) Constants.NOTIFICATIONS_DATETIME_FORMAT_SIMPLE else Constants.NOTIFICATIONS_DATETIME_FORMAT)
                }

                titleText.text = notification.title

                titleText.typeface = if (notification.isRead) Typeface.DEFAULT else Typeface.DEFAULT_BOLD

                deleteImageView.setOnClickListener { interactionListener?.notificationDeleteClick(notification) }
                setOnClickListener { interactionListener?.notificationClick(notification) }
            }
        }
    }

    interface InteractionListener {
        fun notificationDeleteClick(notification: RealmNotification)
        fun notificationClick(notification: RealmNotification)
    }
}
