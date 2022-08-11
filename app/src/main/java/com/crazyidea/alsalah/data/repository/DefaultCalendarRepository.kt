package com.crazyidea.alsalah.data.repository

import com.crazyidea.alsalah.BuildConfig
import com.crazyidea.alsalah.data.api.Network
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultCalendarRepository @Inject constructor(
    private val externalScope: CoroutineScope

) :CalendarRepository{

    override suspend fun getEventsList(
        date: String,
    ): String? {
        var resultList: String? = null
        withContext(externalScope.coroutineContext) {
            var result = Network.calendar.getEvents(BuildConfig.WIKI_BASE_URL.plus("&page=$date"))
            resultList = null
            var str: String = ""
            result.parse.wikitext.text.let {
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
            resultList =  str

        }
        return resultList
    }


}