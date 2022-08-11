package com.crazyidea.alsalah.data.repository

import androidx.lifecycle.LiveData
import com.crazyidea.alsalah.data.room.entity.prayers.DateWithTiming

interface PrayersRepository : BaseRepository {


    var prayers: LiveData<DateWithTiming>
    suspend fun getPrayers(day: Int, month: String)

    suspend fun refreshPrayers(
        month: String,
        year: String,
        lat: String,
        lng: String,
        method: Int,
        school: Int,
        tune: String?,
        adjustment: Int
    )

}