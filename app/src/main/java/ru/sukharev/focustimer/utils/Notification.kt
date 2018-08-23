package ru.sukharev.focustimer.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import ru.sukharev.focustimer.R
import ru.sukharev.focustimer.focus.FocusActivity

const val NOTIFICATION_CHANNEL_STRING = "focus_notification_channel"
const val NOTIFICATION_ID = 1

private fun createNotificationChannelIfNeed(context : Context){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_STRING) == null) {
            val notificationChannel =
                    NotificationChannel(NOTIFICATION_CHANNEL_STRING,
                            context.getString(R.string.notification_channel_name),
                            NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}

fun sendFocusFinishedNotification(context: Context){
    createNotificationChannelIfNeed(context)
    val intent = Intent(context, FocusActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    val pendingIntent = PendingIntent.getActivity(context,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_ONE_SHOT)
    val builder = NotificationCompat.Builder(context,
            NOTIFICATION_CHANNEL_STRING)
            .setSmallIcon(R.drawable.ic_app_icon)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.notification_focus_finished))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(NOTIFICATION_ID,
            builder.build())
}