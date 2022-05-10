package com.crazyidea.alsalah.data.workManager

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.crazyidea.alsalah.MainActivity
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
        Log.e("worker is", "Fireeeed")
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
        val calendar: Calendar = Calendar.getInstance(TimeZone.getDefault());

        val timings = prayerDao.getTodayTimings(
            calendar.get(Calendar.DAY_OF_MONTH).toString(),
            (calendar.get(Calendar.MONTH) + 1).toString()
        )

        val mAlarmMgr = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val myIntent = Intent(applicationContext, MainActivity::class.java)
        myIntent.putExtra("show_dialog", true)

        val mPi = PendingIntent.getActivity(
            applicationContext, 0, myIntent, FLAG_IMMUTABLE
        )
        val fajrTime = timings.timing.Fajr.split(":")
        calendar.set(Calendar.HOUR_OF_DAY, fajrTime[0].toInt())
        calendar.set(Calendar.MINUTE, fajrTime[1].toInt())
        calendar.set(Calendar.SECOND, 0)
        setExactAndAllowWhileIdle(mAlarmMgr, AlarmManager.RTC_WAKEUP, calendar.timeInMillis, mPi)
        val ZuhrTime = timings.timing.Dhuhr.split(":")
        calendar.set(Calendar.HOUR_OF_DAY, ZuhrTime[0].toInt())
        calendar.set(Calendar.MINUTE, ZuhrTime[1].toInt())
        calendar.set(Calendar.SECOND, 0)
        setExactAndAllowWhileIdle(mAlarmMgr, AlarmManager.RTC_WAKEUP, calendar.timeInMillis, mPi)
        val AsrTime = timings.timing.Asr.split(":")
        calendar.set(Calendar.HOUR_OF_DAY, AsrTime[0].toInt())
        calendar.set(Calendar.MINUTE, AsrTime[1].toInt())
        calendar.set(Calendar.SECOND, 0)
        setExactAndAllowWhileIdle(mAlarmMgr, AlarmManager.RTC_WAKEUP, calendar.timeInMillis, mPi)
        val MaghribTime = timings.timing.Maghrib.split(":")
        calendar.set(Calendar.HOUR_OF_DAY, MaghribTime[0].toInt())
        calendar.set(Calendar.MINUTE, MaghribTime[1].toInt())
        calendar.set(Calendar.SECOND, 0)
        setExact(mAlarmMgr, AlarmManager.RTC_WAKEUP, calendar.timeInMillis, mPi)
        val IshaTime = timings.timing.Isha.split(":")
        calendar.set(Calendar.HOUR_OF_DAY, IshaTime[0].toInt())
        calendar.set(Calendar.MINUTE, IshaTime[1].toInt())
        calendar.set(Calendar.SECOND, 0)
        setExact(mAlarmMgr, AlarmManager.RTC_WAKEUP, calendar.timeInMillis, mPi)
    }

    fun setExactAndAllowWhileIdle(
        alarmManager: AlarmManager, type: Int,
        triggerAtMillis: Long, operation: PendingIntent
    ) {
        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(type, triggerAtMillis, operation)
        } else {
            AlarmManagerCompat.setExact(alarmManager, type, triggerAtMillis, operation)
        }
    }
//
    fun setExact(
        alarmManager: AlarmManager, type: Int, triggerAtMillis: Long,
        operation: PendingIntent
    ) {
        alarmManager.setExact(type, triggerAtMillis, operation)
    }
}