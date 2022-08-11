package com.crazyidea.alsalah.data.repository

import androidx.lifecycle.LiveData
import com.crazyidea.alsalah.data.room.entity.Ayat
import com.crazyidea.alsalah.data.room.entity.BookmarkWithAya
import com.crazyidea.alsalah.data.room.entity.Surah

interface QuranRepository : BaseRepository {
    val bookmarks: LiveData<List<BookmarkWithAya>>

    suspend fun getPage(page: Int = 1): List<Ayat>
    suspend fun getAllSurah(): List<Surah>
    suspend fun downloadQuran(): Boolean
    suspend fun getAudio(ayah: String, ayahId: Int, page: Int): String
    suspend fun getJuzPage(toInt: Int): Int
    suspend fun bookmarkPage(page: Long)
    suspend fun bookmarkAya(text:String,ayaId: Int,page:Int)
}