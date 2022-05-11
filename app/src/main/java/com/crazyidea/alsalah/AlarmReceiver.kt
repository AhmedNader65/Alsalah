package com.crazyidea.alsalah

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.DEFAULT_ALL
import java.util.*


class AlarmReceiver : BroadcastReceiver() {
    private val CHANNEL_ID: String = "PrayerTimes"

    override fun onReceive(context: Context, intent: Intent?) {
        Log.e("receiver", "received")
// Create an explicit intent for an Activity in your app
        Toast.makeText(context, "alarm ran", Toast.LENGTH_SHORT).show()
//        createNotificationChannel(context)

        val fullScreenIntent = Intent(context, MainActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, 0,
            fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
        )
        sendNotification(context,
            getTitle(context,intent?.getStringExtra("salah")) as String,fullScreenPendingIntent)

    }

    private fun sendNotification(context: Context,title:String, pendingIntent: PendingIntent) {
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_after_prayer)
                .setContentTitle(title)
                .setContentText(context.getString(R.string.continue_using))
                .setAutoCancel(true)
                .setLights(Color.GRAY,500,500)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(DEFAULT_ALL)
                .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.azan))
                .setContentIntent(pendingIntent)
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(context)
        }
        notificationManager.notify(
            Random().nextInt() /* ID of notification */,
            notificationBuilder.build()
        )
    }

    private fun createChannel(context: Context) {
        val soundUri =
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.azan)
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.getString(R.string.channel_name)
                val descriptionText = context.getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                channel.setShowBadge(true);
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()
                channel.setSound(soundUri, audioAttributes)
                channel.lightColor = Color.GRAY;
                channel.enableLights(true);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

                // Register the channel with the system
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
    }

    private fun getTitle(context: Context, stringExtra: String?): CharSequence? {
        return when (stringExtra) {
            "fajr" -> context.getString(R.string.fajr_prayer_notification)
            "zuhr" ->
                context
                    .getString(R.string.zuhr_prayer_notification)
            "asr" ->
                context
                    .getString(R.string.asr_prayer_notification)
            "maghrib" ->
                context
                    .getString(R.string.maghrib_prayer_notification)
            "isha" ->
                context
                    .getString(R.string.isha_prayer_notification)
            else ->
                context
                    .getString(R.string.prayer_notification)
        }

    }

    private fun createNotificationChannel(context: Context) {
        val soundUri =
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.azan)

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Hello"
            val descriptionText = "getString(R.string.channel_description)"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            channel.setSound(soundUri, audioAttributes)
            channel.lightColor = Color.GRAY;
            channel.enableLights(true);
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}