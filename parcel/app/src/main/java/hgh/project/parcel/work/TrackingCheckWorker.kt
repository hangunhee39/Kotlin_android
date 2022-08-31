package hgh.project.parcel.work

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import hgh.project.parcel.R
import hgh.project.parcel.data.entity.Level
import hgh.project.parcel.data.repository.TrackingItemRepository
import hgh.project.parcel.presentation.MainActivity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class TrackingCheckWorker(
    val context: Context,
    workerParams: WorkerParameters,
    private val trackingItemRepository: TrackingItemRepository,
    private val dispatcher: CoroutineDispatcher
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(dispatcher) {
        try {
            val startedTrackingItems = trackingItemRepository.getTrackingItemInformation()
                .filter { it.second.level == Level.START }

            if (startedTrackingItems.isNotEmpty()) {
                createNotificationChannelIfNeeded()

                val representativeItem = startedTrackingItems.first()
                NotificationManagerCompat
                    .from(context)
                    .notify(
                        NOTIFICATION_ID,
                        createNotification(
                            "${representativeItem.second.itemName}(${representativeItem.first.company.name}) " +
                                    "외 ${startedTrackingItems.size - 1}건의 택배가 배송 출발하였습니다."
                        )
                    )
            }

            Result.success()
        } catch (exception: Exception) {
            Result.failure()
        }
    }

    private fun createNotificationChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = CHANNEL_DESCRIPTION

            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    private fun createNotification(
        message: String?
    ): Notification {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, FLAG_UPDATE_CURRENT)

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_local_shipping_24)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
    }

    companion object {
        private const val CHANNEL_NAME = "Daily Tracking Updates"
        private const val CHANNEL_DESCRIPTION = "매일 배송 출발한 상품을 알려줍니다."
        private const val CHANNEL_ID = "Channel Id"
        private const val NOTIFICATION_ID = 101
    }
}