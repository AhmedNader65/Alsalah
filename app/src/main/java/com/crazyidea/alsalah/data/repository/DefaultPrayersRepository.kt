package com.crazyidea.alsalah.data.repository

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crazyidea.alsalah.data.DataStoreManager
import com.crazyidea.alsalah.data.api.Network
import com.crazyidea.alsalah.data.model.asDateDatabaseModel
import com.crazyidea.alsalah.data.model.asMetaDatabaseModel
import com.crazyidea.alsalah.data.model.asTimingDatabaseModel
import com.crazyidea.alsalah.data.room.AppDatabase
import com.crazyidea.alsalah.data.room.entity.prayers.DateWithTiming
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class DefaultPrayersRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val externalScope: CoroutineScope,
) : PrayersRepository {


    /**
     * A list of asteroids that can be shown on the screen.
     */
    private var _prayers: MutableLiveData<DateWithTiming> = MutableLiveData()
    override var prayers: LiveData<DateWithTiming> = _prayers
    override suspend fun getPrayers(day: Int, month: String) {
        return withContext(externalScope.coroutineContext) {
            val dayFormatted = String.format(Locale.ENGLISH, "%02d", day)
            _prayers.postValue(appDatabase.prayersDao().getTodayTimings(dayFormatted, month))
        }
    }


    /**
     * Refresh the asteroids stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     */
    override suspend fun refreshPrayers(
        month: String,
        year: String,
        lat: String,
        lng: String,
        method: Int,
        school: Int,
        tune: String?,
        adjustment: Int
    ) {

        var methodSt: String? = method.toString()
        var schoolSt: String? = school.toString()
        var adjustmentSt: String? = adjustment.toString()
        withContext(externalScope.coroutineContext) {
            if (method == -1)
                methodSt = null
            if (school == -1)
                schoolSt = null
            if (adjustment == -1)
                adjustmentSt = null
            val prayers = Network.prayers.getPrayersTimingByAddress(
                lat,
                lng,
                month,
                year,
                methodSt,
                schoolSt,
                tune,
                adjustmentSt,
            )

            appDatabase.prayersDao().deleteDates()
            appDatabase.prayersDao().deleteMeta()
            appDatabase.prayersDao().deleteTimings()
            prayers.data.let {
                val inMetaId = appDatabase.prayersDao().insertMeta(it.first().asMetaDatabaseModel())
                it.forEach { prayerResponseApiModel ->
                    try {

                        val inTimingId = appDatabase.prayersDao()
                            .insertTiming(prayerResponseApiModel.timings.asTimingDatabaseModel())
                        appDatabase.prayersDao()
                            .insertDate(prayerResponseApiModel.date.asDateDatabaseModel().apply {
                                metaId = inMetaId
                                timingId = inTimingId
                            })
                    } catch (e: SQLiteConstraintException) {
                        Timber.e("error", e.printStackTrace())
                        Timber.e("error", prayerResponseApiModel)
                    }
                }
            }
        }
    }


}