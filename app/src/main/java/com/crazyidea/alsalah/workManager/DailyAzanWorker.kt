package com.crazyidea.alsalah.workManager

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.room.AppDatabase
import com.crazyidea.alsalah.data.room.dao.AzkarDao
import com.crazyidea.alsalah.data.room.dao.KhatmaDao
import com.crazyidea.alsalah.data.room.dao.PrayerDao
import com.crazyidea.alsalah.utils.GlobalPreferences
import com.crazyidea.alsalah.utils.setAlarm
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit


private const val TAG_OUTPUT: String = "DailyAzanWorker"

@HiltWorker
class DailyAzanWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    appDatabase: AppDatabase,
    val globalPreferences: GlobalPreferences,
) : Worker(context, workerParams) {
    private var prayerDao: PrayerDao = appDatabase.prayersDao()
    private var khatmaDao: KhatmaDao = appDatabase.khatmaDao()
    private var azkarDao: AzkarDao = appDatabase.azkarDao()

    override fun doWork(): Result {
        Timber.e("setting alarms")
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
        setupPrayingAlarms()
        setupKhatmaAlarms()
        setupAzkarAlarms()
        return Result.success()
    }

    private fun setupAzkarAlarms() {
        val currentDate = Calendar.getInstance()
        val res: Resources = applicationContext.resources
        var context: Context
        val configuration: Configuration? = res.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration?.setLocale(Locale("ar"))
            val localeList = LocaleList(Locale("ar"))
            LocaleList.setDefault(localeList)
            configuration?.setLocales(localeList)
            context = applicationContext.createConfigurationContext(configuration!!)
        } else {
            configuration?.setLocale(Locale("ar"))
            context = applicationContext.createConfigurationContext(configuration!!)
        }

        if (globalPreferences.isMorningNotification()) {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 7)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            if (currentDate.timeInMillis < calendar.timeInMillis) {
                setAlarm(
                    applicationContext,
                    "azkar",
                    context.resources.getString(R.string.morning_azkar),
                    calendar.timeInMillis,
                    category = "أذكار الصباح",
                )
            }
        }
        if (globalPreferences.isEveningNotification()) {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 19)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            if (currentDate.timeInMillis < calendar.timeInMillis) {
                setAlarm(
                    applicationContext,
                    "azkar",
                    context.resources.getString(R.string.night_azkar),
                    calendar.timeInMillis,
                    category = "أذكار المساء",
                )
            }
        }
        if (globalPreferences.isSleepingNotification()) {

            if (currentDate.timeInMillis < globalPreferences.getSleepingTime()) {
                setAlarm(
                    applicationContext,
                    "azkar",
                    context.resources.getString(R.string.sleep_azkar),
                    globalPreferences.getSleepingTime(),
                    category = "أذكار النوم",
                )
            }
        }
    }

    private fun setupKhatmaAlarms() {
        val khatmas = khatmaDao.getActive()
        khatmas.forEach {

            val currentDate = Calendar.getInstance()
            val dueDate = Calendar.getInstance()
            val originalDate = Calendar.getInstance()
            // Set Execution around 05:00:00 AM
            originalDate.timeInMillis = it.time!!
            dueDate.set(Calendar.HOUR_OF_DAY, originalDate.get(Calendar.HOUR_OF_DAY))
            dueDate.set(Calendar.MINUTE, originalDate.get(Calendar.MINUTE))
            if (dueDate.after(currentDate)) {
                setAlarm(
                    applicationContext,
                    "khatma",
                    it.name!!,
                    dueDate.timeInMillis
                )
            }
        }
    }

    private fun setupPrayingAlarms() {
        val calendar: Calendar = Calendar.getInstance(TimeZone.getDefault())
        val currentDate: Calendar = Calendar.getInstance(TimeZone.getDefault())
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val daySt = String.format(Locale.ENGLISH, "%02d", day)
        (calendar.get(Calendar.MONTH) + 1).toString()
        val prayers = prayerDao.getTodayTimings(
            daySt,
            month.toString()
        )
        val type = "salah"
        val timings = prayers.timing
        val fajrTime = timings.Fajr.split(":")
        calendar.set(Calendar.HOUR_OF_DAY, fajrTime[0].toInt())
        calendar.set(Calendar.MINUTE, fajrTime[1].toInt())
        calendar.set(Calendar.SECOND, 0)

        if (calendar.after(currentDate)) {
                setAlarm(
                    applicationContext,
                    "before_prayer",
                    "fajr",
                    calendar.timeInMillis - (globalPreferences.beforeAzanNotificationPeriod() * 60000)
                )
            setAlarm(
                applicationContext,
                "iqama",
                "fajr",
                calendar.timeInMillis + (20 * 60000)
            )
            setAlarm(applicationContext, type, "fajr", calendar.timeInMillis)
        }
        val ZuhrTime = timings.Dhuhr.split(":")
        calendar.set(Calendar.HOUR_OF_DAY, ZuhrTime[0].toInt())
        calendar.set(Calendar.MINUTE, ZuhrTime[1].toInt())
        calendar.set(Calendar.SECOND, 0)

        if (calendar.after(currentDate)) {
            Timber.e("SETTING zuhr PRAYER ALARM")
                setAlarm(
                    applicationContext,
                    "before_prayer",
                    "zuhr",
                    calendar.timeInMillis - (globalPreferences.beforeAzanNotificationPeriod() * 60000)
                )
            setAlarm(
                applicationContext,
                "iqama",
                "zuhr",
                calendar.timeInMillis + (10 * 60000)
            )
            setAlarm(applicationContext, type, "zuhr", calendar.timeInMillis)
        }
        val AsrTime = timings.Asr.split(":")
        calendar.set(Calendar.HOUR_OF_DAY, AsrTime[0].toInt())
        calendar.set(Calendar.MINUTE, AsrTime[1].toInt())
        calendar.set(Calendar.SECOND, 0)
        if (calendar.after(currentDate)) {
                setAlarm(
                    applicationContext,
                    "before_prayer",
                    "asr",
                    calendar.timeInMillis - (globalPreferences.beforeAzanNotificationPeriod() * 60000)
                )
            setAlarm(
                applicationContext,
                "iqama",
                "asr",
                calendar.timeInMillis + (10 * 60000)
            )
            setAlarm(applicationContext, type, "asr", calendar.timeInMillis)
        }
        val MaghribTime = timings.Maghrib.split(":")
        calendar.set(Calendar.HOUR_OF_DAY, MaghribTime[0].toInt())
        calendar.set(Calendar.MINUTE, MaghribTime[1].toInt())
        calendar.set(Calendar.SECOND, 0)
        if (calendar.after(currentDate)) {
            Timber.e("before prayer is here 3")
                setAlarm(
                    applicationContext,
                    "before_prayer",
                    "maghrib",
                    calendar.timeInMillis - (globalPreferences.beforeAzanNotificationPeriod() * 60000)
                )
            setAlarm(
                applicationContext,
                "iqama",
                "maghrib",
                calendar.timeInMillis + (10 * 60000)
            )
            setAlarm(applicationContext, type, "maghrib", calendar.timeInMillis)
        }
        val IshaTime = timings.Isha.split(":")
        calendar.set(Calendar.HOUR_OF_DAY, IshaTime[0].toInt())
        calendar.set(Calendar.MINUTE, IshaTime[1].toInt())
        calendar.set(Calendar.SECOND, 0)
        if (calendar.after(currentDate)) {
                setAlarm(
                    applicationContext,
                    "before_prayer",
                    "isha",
                    calendar.timeInMillis - (globalPreferences.beforeAzanNotificationPeriod() * 60000)
                )
                setAlarm(
                    applicationContext,
                    "iqama",
                    "isha",
                    calendar.timeInMillis + (10 * 60000)
                )
            setAlarm(applicationContext, type, "isha", calendar.timeInMillis)
        }

    }


}