package com.crazyidea.alsalah.data.repository

import com.crazyidea.alsalah.data.dataSource.EventsRemoteDataSource
import com.crazyidea.alsalah.data.model.Resource
import com.crazyidea.alsalah.data.model.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CalendarRepository @Inject constructor(
    private val remoteDataSource: EventsRemoteDataSource,
    private val externalScope: CoroutineScope

) {

    suspend fun getEventsList(
        date: String,
    ): Resource<String> {
        var resultList: Resource<String> = Resource(Status.LOADING, null, null)
        withContext(externalScope.coroutineContext) {
            var result = remoteDataSource.getEventByDay(date)
            resultList = Resource(result.status, null, result.message)
            if (result.status == Status.SUCCESS) {
                var str: String = ""
                result.data!!.parse.wikitext.text.let {
                    str = it
                    val re = Regex("\\shref=\"[^\"]*")
                    str = str.replace(re, "")
                    val editSpan = "<span class=\"mw-editsection-bracket\">]</span></span>"
                    str = str.replaceRange(
                        str.indexOf("<span class=\"mw-editsection\">", 0),
                        str.indexOf(editSpan, 0) + editSpan.length,
                        ""
                    )

                }
                resultList = Resource(result.status, str, "")
            }
        }
        return resultList
    }


}