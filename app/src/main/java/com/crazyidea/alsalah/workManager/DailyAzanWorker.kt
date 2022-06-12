package com.crazyidea.alsalah.workManager

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.crazyidea.alsalah.receiver.AlarmReceiver
import com.crazyidea.alsalah.data.room.AppDatabase
import com.crazyidea.alsalah.data.room.dao.PrayerDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.*
import java.util.concurrent.TimeUnit


private const val TAG_OUTPUT: String = "DailyAzanWorker"

@HiltWorker
public class DailyAzanWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    appDatabase: AppDatabase
) : Worker(context, workerParams) {
    private var prayerDao: PrayerDao = appDatabase.prayersDao()

    override fun doWork(): Result {
        Log.e("Work manager","do work")
        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()
        // Set Execution around 05:00:00 AM
        dueDate.set(Calendar.HOUR_OF_DAY, 0)
        dueDate.set(Calendar.MINUTE, 30)
        dueDate.set(Calendar.SECOND, 0)
        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24)
        }
        val timeDiff = (dueDate.timeInMillis - currentDate.timeInMillis)
        val dailyWorkRequest = OneTimeWorkRequestBuilder<DailyAzanWorker>()
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .addTag(TAG_OUTPUT)
            .build()
        WorkManager.getInstance(applicationContext)
            .enqueue(dailyWorkRequest)
        setupAlarms()
        return Result.success()
    }

    private fun setupAlarms() {
        Log.e("settings alarm","yeah")
        val calendar: Calendar = Calendar.getInstance(TimeZone.getDefault())
        val currentDate: Calendar = Calendar.getInstance(TimeZone.getDefault())
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val daySt = String.format( Locale.ENGLISH,"%02d", day)
        (calendar.get(Calendar.MONTH) + 1).toString()
//         val prayers = prayerDao.getTodayTimings(
//             daySt,
//             month.toString()
//        )
//        val timings = prayers.value?.timing
//        timings?.let { timings ->
//            val fajrTime = timings.Fajr.split(":")
//            calendar.set(Calendar.HOUR_OF_DAY, fajrTime[0].toInt())
//            calendar.set(Calendar.MINUTE, fajrTime[1].toInt())
//            calendar.set(Calendar.SECOND, 0)
//
//            if (calendar.after(currentDate)) {
//                setAlarm("fajr", calendar.timeInMillis)
//            }
//            val ZuhrTime = timings.Dhuhr.split(":")
//            calendar.set(Calendar.HOUR_OF_DAY, ZuhrTime[0].toInt())
//            calendar.set(Calendar.MINUTE, ZuhrTime[1].toInt())
//            calendar.set(Calendar.SECOND, 0)
//
//            if (calendar.after(currentDate)) {
//                setAlarm("zuhr", calendar.timeInMillis)
//            }
//            val AsrTime = timings.Asr.split(":")
//            calendar.set(Calendar.HOUR_OF_DAY, AsrTime[0].toInt())
//            calendar.set(Calendar.MINUTE, AsrTime[1].toInt())
//            calendar.set(Calendar.SECOND, 0)
//            if (calendar.after(currentDate)) {
//                setAlarm("asr", calendar.timeInMillis)
//            }
//            val MaghribTime = timings.Maghrib.split(":")
//            calendar.set(Calendar.HOUR_OF_DAY, MaghribTime[0].toInt())
//            calendar.set(Calendar.MINUTE, MaghribTime[1].toInt())
//            calendar.set(Calendar.SECOND, 0)
//            if (calendar.after(currentDate)) {
//                setAlarm("maghrib", calendar.timeInMillis)
//            }
//            val IshaTime = timings.Isha.split(":")
//            calendar.set(Calendar.HOUR_OF_DAY, IshaTime[0].toInt())
//            calendar.set(Calendar.MINUTE, IshaTime[1].toInt())
//            calendar.set(Calendar.SECOND, 0)
//            if (calendar.after(currentDate)) {
//                setAlarm("isha", calendar.timeInMillis)
//            }
//        }

    }

    private fun setAlarm(title: String, timeInMillis: Long) {
        Log.e(title,"$timeInMillis")
        val alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(applicationContext, AlarmReceiver::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        intent.action = title
        intent.putExtra("salah", title)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getBroadcast(
                applicationContext,
                0,
                intent,
                FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getBroadcast(applicationContext, 0, intent, 0)
        }

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

}