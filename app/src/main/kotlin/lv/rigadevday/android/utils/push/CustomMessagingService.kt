package lv.rigadevday.android.utils.push

import android.app.NotificationManager
import android.support.v4.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import cz.eman.android.devfest.R
import lv.rigadevday.android.utils.BaseApp
import java.util.*
import javax.inject.Inject

/**
 * Just show simple notification even when app is in foreground.
 */
class CustomMessagingService : FirebaseMessagingService() {

    @Inject lateinit var notifications: NotificationManager

    override fun onCreate() {
        super.onCreate()
        BaseApp.graph.inject(this)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        notifications.notify(
                Random().nextInt(),
                NotificationCompat.Builder(this, this.packageName)
                        .setContentTitle(remoteMessage.notification.title)
                        .setContentText(remoteMessage.notification.body)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setColorized(false)
                        .setAutoCancel(true)
                        .build()
        )
    }

}
