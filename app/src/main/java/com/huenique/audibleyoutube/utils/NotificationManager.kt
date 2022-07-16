package com.huenique.audibleyoutube.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

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
}
