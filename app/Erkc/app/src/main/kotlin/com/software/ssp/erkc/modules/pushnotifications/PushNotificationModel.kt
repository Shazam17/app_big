package com.software.ssp.erkc.modules.pushnotifications


class PushNotificationModel(
        val title: String,
        val body: String,
        val id: String,
        val type: PushNotificationType,
        val typed_id: String?
)

enum class PushNotificationType {
    NOTIFICATION,
    PAYMENT,
    CARD
}