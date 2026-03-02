package com.protas.dopaminaminimalist.data.services

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class CallNotificationListener : NotificationListenerService() {

    companion object {
        var isCallActive: Boolean = false
            private set
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        // Detectar si la notificación proviene del sistema de llamadas
        if (sbn.packageName == "com.android.server.telecom" || sbn.packageName == "com.google.android.dialer") {
            isCallActive = true
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        if (sbn.packageName == "com.android.server.telecom" || sbn.packageName == "com.google.android.dialer") {
            isCallActive = false
        }
    }
}