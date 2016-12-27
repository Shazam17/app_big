package com.software.ssp.erkc.modules.notifications.notificationslist

import android.support.v7.widget.RecyclerView
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
                dateTimeText.text = notification.deliveredDate?.toString(Constants.NOTIFICATIONS_DATETIME_FORMAT)
                titleText.text = notification.title

                readImageView.visibility = if (notification.isRead) View.VISIBLE else View.GONE

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
