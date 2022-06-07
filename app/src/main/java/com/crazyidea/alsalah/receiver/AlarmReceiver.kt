package com.crazyidea.alsalah.receiver

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
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.DEFAULT_ALL
import androidx.core.content.ContextCompat.startActivity
import com.crazyidea.alsalah.MainActivity
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.repository.FajrListRepository
import com.crazyidea.alsalah.utils.GlobalPreferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    private var listOfNumbers: List<String>? = null

    @Inject
    lateinit var fajrRepository: FajrListRepository
    private val CHANNEL_ID: String = "PrayerTimes"
    lateinit var globalPreferences: GlobalPreferences
    lateinit var sound: Uri

    override fun onReceive(context: Context, intent: Intent?) {
        Log.e("receiver", "received")
// Create an explicit intent for an Activity in your app
        Toast.makeText(context, "alarm ran", Toast.LENGTH_SHORT).show()
//        createNotificationChannel(context)
        globalPreferences = GlobalPreferences(context)

        val fullScreenIntent = Intent(context, MainActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, 0,
            fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
        )
        sendNotification(
            context,
            getTitle(context, intent?.getStringExtra("salah")) as String, fullScreenPendingIntent
        )
        if (intent?.getStringExtra("salah") == "fajr") {
            GlobalScope.async {

                val listOfContacts = fajrRepository.getFajrList()
                listOfNumbers = listOfContacts?.map { it.number }
//                nextCalling(context, listOfNumbers!![0])
//                count++

            }
            val telephonyManager: TelephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Log.e("sdk is", "here")
                telephonyManager.registerTelephonyCallback(
                    context.mainExecutor,
                    object : TelephonyCallback(), TelephonyCallback.CallStateListener {
                        override fun onCallStateChanged(state: Int) {
                            listOfNumbers?.let {
                                Log.e("listOfNumbers ", "${it.size}")
                                checkState(context, state, listOfNumbers)
                            }
                        }
                    })
            } else {
                Log.e("sdk is", "here2")
                phoneListener = PhoneListener { checkState(context, it, listOfNumbers) }
                telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE)
            }
        }

    }

    class PhoneListener(val checkState: (state: Int) -> Unit) : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            Log.e("sate changed", "here2")
            checkState(state)
        }
    }

    private var phoneListener: PhoneListener? = null
    var count = 0
    private fun checkState(context: Context, state: Int, listOfNumbers: List<String>?) {
        when (state) {
            TelephonyManager.CALL_STATE_RINGING -> Log.v(
                this.javaClass.simpleName,
                "Inside Ringing State::"
            )
            TelephonyManager.CALL_STATE_IDLE -> {
                var phone_number: String? = null
                Log.v(this.javaClass.simpleName, "Inside Idle State::")
                if (listOfNumbers?.size!! > count) {
                    phone_number = listOfNumbers[count]
                    count++
                }
                phone_number?.let { nextCalling(context, it) }
            }
            TelephonyManager.CALL_STATE_OFFHOOK -> Log.v(
                this.javaClass.simpleName,
                "Inside OFFHOOK State::"
            )
            else -> {}
        }
    }

    private fun sendNotification(context: Context, title: String, pendingIntent: PendingIntent) {
        var number = globalPreferences.getAzan().toIntOrNull()
        sound = if (number != null) {
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + number)
        } else {
            Uri.parse(globalPreferences.getAzan())
        }


        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_after_prayer)
                .setContentTitle(title)
                .setContentText(context.getString(R.string.continue_using))
                .setAutoCancel(true)
                .setLights(Color.GRAY, 500, 500)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(DEFAULT_ALL)
                .setSound(sound)
                .setContentIntent(pendingIntent)
        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

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
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC;

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

    private fun nextCalling(context: Context, phone_number: String) {

        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phone_number"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent)
    }
}