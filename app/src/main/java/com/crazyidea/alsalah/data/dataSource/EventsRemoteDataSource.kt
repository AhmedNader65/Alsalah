package com.crazyidea.alsalah.data.dataSource

import com.crazyidea.alsalah.data.api.CalendarAPI
import com.crazyidea.alsalah.data.api.PrayersAPI
import com.crazyidea.alsalah.data.getResponse
import com.crazyidea.alsalah.data.getResponseNoServerResource
import com.crazyidea.alsalah.data.model.AzkarResponseApiModel
import com.crazyidea.alsalah.data.model.PrayerResponseApiModel
import com.crazyidea.alsalah.data.model.Resource
import com.crazyidea.alsalah.data.model.WikiResponseApiModel
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class EventsRemoteDataSource @Inject constructor(
    private val calendarAPI: CalendarAPI,
    private val wikiUrl:String
) {

    suspend fun getEventByDay(
        date: String,
    ): Resource<WikiResponseApiModel> {
        val url = wikiUrl.plus("&page=$date")
        return getResponseNoServerResource(
            request = {
                calendarAPI.getEvents(url)
            },
            defaultErrorMessage = "Error getting prayers try again"
        )
    }
}
