package com.crazyidea.alsalah

import android.app.Application
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaFormat
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import android.widget.VideoView
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.crazyidea.alsalah.notifications.Salah
import com.crazyidea.alsalah.utils.GlobalPreferences
import com.crazyidea.alsalah.utils.SubtitleView
import com.crazyidea.alsalah.workManager.RefreshDataWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.Timber.Forest.plant
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltAndroidApp
class App : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    private val applicationScope = CoroutineScope(Dispatchers.Default)
    override fun getWorkManagerConfiguration() = Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .build()

    override fun onCreate() {
        super.onCreate()
        delayedInit()
        // This will initialise Timber
        if (BuildConfig.DEBUG) {
            plant(Timber.DebugTree())
        }
    }


    private fun getSubtitles(): Int {

        val azanId = GlobalPreferences(this).getAzan()
        return  when (azanId) {
            1 -> R.raw.mecca_srt
            2 -> R.raw.madny_srt
            3 -> R.raw.aqsa
            4 -> R.raw.menshawy_srt
            5 -> R.raw.abdelbaset_srt
            6 -> R.raw.azan_srt
            else -> R.raw.mecca_srt

        }

    }


    private fun delayedInit() {
        applicationScope.launch {
            setupRecurringWork()
        }
    }


    private fun setupRecurringWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }.build()

        val repeatingRequest
                = PeriodicWorkRequestBuilder<RefreshDataWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            RefreshDataWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            repeatingRequest)
    }

}
