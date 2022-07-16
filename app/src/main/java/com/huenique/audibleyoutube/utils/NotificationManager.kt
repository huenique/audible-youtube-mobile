package com.huenique.audibleyoutube.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.huenique.audibleyoutube.R

class NotificationManager {
  fun createNotificationChannel(channelId: String, context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val name = "AudibleYouTubeChannel"
      val descriptionText = "Audible YouTube Mobile's notification channel"
      val importance = NotificationManager.IMPORTANCE_DEFAULT
      val channel =
          NotificationChannel(channelId, name, importance).apply { description = descriptionText }

      val notificationManager: NotificationManager =
          context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }
  }

  fun showSimpleNotification(
      context: Context,
      channelId: String,
      notificationId: Int,
      textTitle: String,
      textContent: String,
      priority: Int = NotificationCompat.PRIORITY_DEFAULT
  ) {
    val builder =
        NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_cloud_download)
            .setContentTitle(textTitle)
            .setContentText(textContent)
            .setPriority(priority)

    with(NotificationManagerCompat.from(context)) { notify(notificationId, builder.build()) }
  }
}
