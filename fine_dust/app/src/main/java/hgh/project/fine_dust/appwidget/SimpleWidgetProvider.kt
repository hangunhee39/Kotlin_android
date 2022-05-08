package hgh.project.fine_dust.appwidget

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import hgh.project.fine_dust.R
import hgh.project.fine_dust.data.Repository
import hgh.project.fine_dust.data.models.airquality.Grade
import kotlinx.coroutines.launch
import java.lang.Exception

class SimpleWidgetProvider : AppWidgetProvider() {

    companion object {
        private const val WIDGET_REFRESH_CHANNEL_ID = "WIDGET_REFRESH_CHANNEL_ID"
        private const val NOTIFICATION_ID = 101
    }

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        //update 주기마다  ForegroundService 시작하기
        ContextCompat.startForegroundService(
            context!!,
            Intent(context, UpdateWidgetService::class.java)
        )
    }

    class UpdateWidgetService : LifecycleService() {

        override fun onCreate() {
            super.onCreate()

            //channel 만들고 Notification 만들기
            createChannelIfNeeded()
            startForeground(
                NOTIFICATION_ID,
                createNotification()
            )
        }

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            //foreground service 권한이 없으면
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.FOREGROUND_SERVICE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val updateViews = RemoteViews(packageName, R.layout.widget_simple).apply {
                    setTextViewText(
                        R.id.resultTextView,
                        "권한없음"
                    )
                    setViewVisibility(R.id.labelTextView, View.GONE)
                    setViewVisibility(R.id.gradeLabelTextView, View.GONE)
                }
                updateWidget(updateViews)

                //서비스 종료
                stopSelf()

                return super.onStartCommand(intent, flags, startId)
            }
            //최근 위치 가져오기
            LocationServices.getFusedLocationProviderClient(this).lastLocation
                .addOnSuccessListener { location ->
                    lifecycleScope.launch {
                        try {
                            val nearbyMonitoringStation = Repository.getNearbyMonitoringStation(
                                location.latitude,
                                location.longitude
                            )
                            val measuredValue =
                                Repository.getLatestAirQualityData(nearbyMonitoringStation!!.stationName!!)
                            val updateViews =
                                RemoteViews(packageName, R.layout.widget_simple).apply {
                                    setViewVisibility(R.id.labelTextView, View.VISIBLE)
                                    setViewVisibility(R.id.gradeLabelTextView, View.VISIBLE)

                                    val currentGrade = (measuredValue?.khaiGrade ?: Grade.UNKNOWN)
                                    setTextViewText(R.id.resultTextView, currentGrade.emoji)
                                    setTextViewText(R.id.gradeLabelTextView, currentGrade.label)
                                }

                            updateWidget(updateViews)
                        } catch (exception: Exception) {
                            exception.printStackTrace()
                        } finally {
                            //서비스 종료
                            stopSelf()
                        }
                    }
                }

            return super.onStartCommand(intent, flags, startId)
        }

        override fun onDestroy() {
            super.onDestroy()

            //notification 지우기
            stopForeground(true)
        }

        //notification Channel 만들기
        private fun createChannelIfNeeded() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                (getSystemService(NOTIFICATION_SERVICE) as? NotificationManager)
                    ?.createNotificationChannel(
                        NotificationChannel(
                            WIDGET_REFRESH_CHANNEL_ID,
                            "위젯 갱신",
                            NotificationManager.IMPORTANCE_LOW
                        )
                    )
            }
        }

        //notification 만들기
        private fun createNotification(): Notification =
            NotificationCompat.Builder(this)
                .setChannelId(WIDGET_REFRESH_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_refresh_24)
                .build()

        //위젯 업데이트 하기
        private fun updateWidget(updateViews: RemoteViews) {
            val widgetProvider = ComponentName(this, SimpleWidgetProvider::class.java)
            AppWidgetManager.getInstance(this).updateAppWidget(widgetProvider, updateViews)
        }
    }

}