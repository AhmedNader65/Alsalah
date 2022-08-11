package com.crazyidea.alsalah.data.repository

interface CalendarRepository : BaseRepository {
    suspend fun getEventsList(
        date: String,
    ): String?
}