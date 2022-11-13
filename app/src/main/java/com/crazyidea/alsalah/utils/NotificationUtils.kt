package com.crazyidea.alsalah.utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.crazyidea.alsalah.MainActivity
import com.crazyidea.alsalah.R


fun sendNotification(
    context: Context,
    channel_ID: String,
    title: String,
    msg: String,
    sound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
    destIntent: Intent? = null
) {
    val notificationManager = context
        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val fullScreenPendingIntent = PendingIntent.getActivity(
        context,
        0,
        destIntent ?: Intent(context, MainActivity::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    // We need to create a NotificationChannel associated with our CHANNEL_ID before sending a notification.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        && notificationManager.getNotificationChannel(channel_ID) == null
    ) {
        createChannel(sound, channel_ID, context)
    }


//    build the notification object with the data to be shown
    val notification = NotificationCompat.Builder(context, channel_ID).apply {

        setSmallIcon(R.drawable.ic_after_prayer)
        setContentTitle(title)
        setContentText(msg)
        setAutoCancel(true)
        setLights(Color.GRAY, 500, 500)
        priority = NotificationCompat.PRIORITY_MAX
        setDefaults(NotificationCompat.DEFAULT_ALL)
        setSound(sound)
        setContentIntent(fullScreenPendingIntent)
        if (destIntent != null)
            setFullScreenIntent(fullScreenPendingIntent, true)
    }.build()

    notificationManager.notify(getUniqueId(), notification)
}

private fun createChannel(
    sound: Uri,
    CHANNEL_ID: String,
    context: Context
) {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()
        val name = context.getString(R.string.channel_name)
        val descriptionText = context.getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel =
            NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                lightColor = Color.GRAY
                enableLights(true)
                setShowBadge(true)
                setSound(sound, audioAttributes)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Register the channel with the system
        notificationManager.createNotificationChannel(channel)
    }
}

private fun getUniqueId() = ((System.currentTimeMillis() % 10000).toInt())