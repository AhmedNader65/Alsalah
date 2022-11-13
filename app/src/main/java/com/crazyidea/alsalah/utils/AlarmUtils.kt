package com.crazyidea.alsalah.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.crazyidea.alsalah.receiver.AlarmReceiver
import com.crazyidea.alsalah.receiver.FridayReceiver
import timber.log.Timber


fun setAlarm(
    context: Context,
    type: String,
    title: String,
    timeInMillis: Long,
    category: String = "",
    repeatable: Boolean = false
) {
    val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = if (type == "friday") Intent(context, FridayReceiver::class.java) else
        Intent(context, AlarmReceiver::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.action = type + title
    intent.putExtra(type, title)
    intent.putExtra("repeatable", repeatable)
    intent.putExtra("zekr_type", category)
    Timber.e("before prayer is here 3 $type")
    val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    } else {
        PendingIntent.getBroadcast(context, 0, intent, 0)
    }
    Timber.e("$title $timeInMillis")
    if (Build.VERSION.SDK_INT >= 23) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )
    } else {
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )
    }

}