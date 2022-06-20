package com.crazyidea.alsalah.workManager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.crazyidea.alsalah.data.prayers.PrayersRepository
import com.crazyidea.alsalah.utils.GlobalPreferences
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import retrofit2.HttpException
import java.util.*

@HiltWorker
class RefreshDataWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: PrayersRepository,
    private val globalPreferences: GlobalPreferences
) : CoroutineWorker(context, workerParams) {
    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {

        val calendar = Calendar.getInstance()
        return try {
            repository.refreshPrayers(
                (calendar.get(Calendar.MONTH) + 1).toString(),
                (calendar.get(Calendar.YEAR)).toString(),
                globalPreferences.getLatitude(),
                globalPreferences.getLongitude(),
                globalPreferences.getCalculationMethod(),
                globalPreferences.getSchool(), null
            )
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}