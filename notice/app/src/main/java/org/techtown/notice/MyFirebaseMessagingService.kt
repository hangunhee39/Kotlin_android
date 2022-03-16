package org.techtown.notice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        createNotificationChannel()

        val type = remoteMessage.data["type"]
            ?.let { NotificationType.valueOf(it) }
        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["message"]

        type ?: return

        NotificationManagerCompat.from(this)
            .notify(type.id, creatNotification(type,title,message))
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            channel.description = CHANNEL_DESCRIPTION

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)

        }
    }

    private fun creatNotification(
        type: NotificationType,
        title: String?,
        message: String?
    ): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("notificationType","${type.title} 타입")
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(this, type.id, intent,FLAG_UPDATE_CURRENT)


        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        when(type){
            NotificationType.NORMAL -> Unit
            NotificationType.EXPANDABLE -> {
                notificationBuilder.setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            "\uD83D\uDC36 \uD83D\uDC31 \uD83D\uDC2D \uD83D\uDC39 \uD83D\uDC30 \uD83E\uDD8A \uD83D\uDC3B \uD83D\uDC3C \uD83D\uDC3B\u200D❄️" +
                                    " \uD83D\uDC28 \uD83D\uDC2F \uD83E\uDD81 \uD83D\uDC2E \uD83D\uDC37 \uD83D\uDC3D \uD83D\uDC38" +
                                    " \uD83D\uDC35 \uD83D\uDE48 \uD83D\uDE49" +
                                    " \uD83D\uDE4A \uD83D\uDC12 \uD83D\uDC14 \uD83D\uDC27 \uD83D\uDC26 \uD83D\uDC24 \uD83D\uDC23 " +
                                    "\uD83D\uDC25 \uD83E\uDD86 \uD83E\uDD85 " +
                                    "\uD83E\uDD89 \uD83E\uDD87 \uD83D\uDC3A \uD83D\uDC17 \uD83D\uDC34 \uD83E\uDD84 \uD83D\uDC1D" +
                                    " \uD83E\uDEB1 \uD83D\uDC1B \uD83E\uDD8B " +
                                    "\uD83D\uDC0C \uD83D\uDC1E \uD83D\uDC1C \uD83E\uDEB0 \uD83E\uDEB2 \uD83E\uDEB3 \uD83E\uDD9F \uD83E\uDD97 " +
                                    "\uD83D\uDD77 \uD83D\uDD78 " +
                                    "\uD83E\uDD82 \uD83D\uDC22 \uD83D\uDC0D \uD83E\uDD8E \uD83E\uDD96 \uD83E\uDD95 \uD83D\uDC19" +
                                    " \uD83E\uDD91 \uD83E\uDD90 \uD83E\uDD9E \uD83E\uDD80 " +
                                    "\uD83D\uDC21 \uD83D\uDC20 \uD83D\uDC1F \uD83D\uDC2C \uD83D\uDC33 \uD83D\uDC0B \uD83E\uDD88 " +
                                    "\uD83D\uDC0A \uD83D\uDC05 \uD83D\uDC06 \uD83E\uDD93 " +
                                    "\uD83E\uDD8D " +
                                    "\uD83E\uDDA7 \uD83E\uDDA3 \uD83D\uDC18 \uD83E\uDD9B \uD83E\uDD8F \uD83D\uDC2A \uD83D\uDC2B \uD83E\uDD92 " +
                                    "\uD83E\uDD98 \uD83E\uDDAC "
                        )
                )
            }
            NotificationType.CUSTOM -> {
                notificationBuilder
                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(
                        RemoteViews(
                            packageName,
                            R.layout.view_custom_notification
                        ).apply {
                            setTextViewText(R.id.title, title)
                            setTextViewText(R.id.message,message)
                        }
                    )
            }
        }
        return notificationBuilder.build()
    }

    companion object {
        private const val CHANNEL_NAME = "Emoji Party"
        private const val CHANNEL_DESCRIPTION = "Emoji Party를 위한 채널"
        private const val CHANNEL_ID = "Channel Id"
    }
}