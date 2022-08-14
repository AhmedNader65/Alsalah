package com.crazyidea.alsalah.workManager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.crazyidea.alsalah.data.DataStoreManager
import com.crazyidea.alsalah.data.repository.PrayersRepository
import com.crazyidea.alsalah.ui.setting.AzanSettings
import com.crazyidea.alsalah.ui.setting.SalahSettings
import com.crazyidea.alsalah.utils.GlobalPreferences
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import java.util.*

@HiltWorker
class RefreshDataWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: PrayersRepository,
    private val globalPreferences: GlobalPreferences,
    val dataStoreManager: DataStoreManager,
) : CoroutineWorker(context, workerParams) {
    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        var calculationMethods = 0
        var school = 0
        var poleCalc = 0
        var fajrMargin: Int = 0
        var shorokMargin: Int = 0
        var dhuhrMargin: Int = 0
        var asrMargin: Int = 0
        var maghribMargin: Int = 0
        var ishaMargin: Int = 0
        runBlocking {
            val salahPref = dataStoreManager.prayerSettings.data.first()
            calculationMethods = salahPref[SalahSettings.CALCULATION_METHOD] ?: 0
            school = salahPref[SalahSettings.SCHOOL] ?: 0
            poleCalc = salahPref[SalahSettings.POLE_CALCULATION] ?: 0
            fajrMargin = salahPref[SalahSettings.FAJR_MARGIN] ?: 0
            shorokMargin = salahPref[SalahSettings.SHOROK_MARGIN] ?: 0
            dhuhrMargin = salahPref[SalahSettings.DHUHR_MARGIN] ?: 0
            asrMargin = salahPref[SalahSettings.ASR_MARGIN] ?: 0
            maghribMargin = salahPref[SalahSettings.MAGHRIB_MARGIN] ?: 0
            ishaMargin = salahPref[SalahSettings.ISHA_MARGIN] ?: 0
        }
        val calendar = Calendar.getInstance()
        return try {
            repository.refreshPrayers(
                (calendar.get(Calendar.MONTH) + 1).toString(),
                (calendar.get(Calendar.YEAR)).toString(),
                globalPreferences.getLatitude(),
                globalPreferences.getLongitude(),
                calculationMethods,
                school,
                "0,$fajrMargin,$shorokMargin,$dhuhrMargin,$asrMargin,$maghribMargin,$ishaMargin,0",
                poleCalc
            )
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}