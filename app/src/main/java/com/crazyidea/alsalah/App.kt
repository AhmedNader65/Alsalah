package com.crazyidea.alsalah

import android.app.Application
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.crazyidea.alsalah.data.DataStoreManager
import com.crazyidea.alsalah.ui.setting.AppSettings
import com.crazyidea.alsalah.workManager.RefreshDataWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import timber.log.Timber.Forest.plant
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltAndroidApp
class App : Application(), Configuration.Provider {
    private lateinit var locale: Locale

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    private val applicationScope = CoroutineScope(Dispatchers.Default)
    override fun getWorkManagerConfiguration() = Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .build()

    override fun onCreate() {
        super.onCreate()
        instance = this
        delayedInit()
        getFirstLanguage()
        GlobalScope.launch {
            dataStoreManager.settingsDataStore.data.map { preferences ->

                Timber.e("getting language3")
                preferences[AppSettings.APP_LANGUAGE] ?: "ar"
            }.collect {
                locale = Locale(it)
            }
        }
        DataStoreCollector.startCollecting(dataStoreManager)
        // This will initialise Timber
        if (BuildConfig.DEBUG) {
            plant(Timber.DebugTree())
        }
    }

    private fun getFirstLanguage() {
        runBlocking {
            locale = Locale(dataStoreManager.settingsDataStore.data.map { preferences ->

                Timber.e("getting language3")
                preferences[AppSettings.APP_LANGUAGE] ?: "ar"
            }.first())
        }
    }

    companion object {
        lateinit var instance: App
            private set
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

        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            RefreshDataWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            repeatingRequest
        )
    }

    fun getAppLocale(): Locale {
        Timber.e("getting language ${locale.language}")
        return locale
    }

}
