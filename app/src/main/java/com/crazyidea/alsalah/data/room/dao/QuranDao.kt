package com.crazyidea.alsalah.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.crazyidea.alsalah.data.room.entity.*

@Dao
interface QuranDao {
    @Transaction
    @Query("SELECT * FROM Ayat where page = :page")
    fun getPage(page: Int): List<Ayat>

    @Transaction
    @Query("SELECT * FROM Surah")
    fun getAllSurah(): List<Surah>

    @Transaction
    @Query("SELECT * FROM Edition where language = :lang")
    fun getEditionByLanguage(lang: String): Edition?

    @Transaction
    @Insert
    fun insertSurah(vararg surah: Surah)

    @Transaction
    @Insert
    fun insertEdition(edition: Edition)

    @Transaction
    @Insert
    fun insertAyah(vararg ayat: Ayat)


    @Query("SELECT bookmarked FROM Ayat where id = :id")
    fun isAyaBookmarked(id: Long): Boolean

    @Query("SELECT * FROM Bookmarks where page = :id")
    fun isPageBookmarked(id: Long): List<Bookmarks>


    @Query("DELETE FROM Ayat")
    fun emptyAyat()

    @Query("DELETE FROM Surah")
    fun emptySurah()

    @Query("DELETE FROM Edition")
    fun emptyEdition()

    @Query("SELECT page FROM Ayat where juz = :juz LIMIT 1")
    fun getJuzPage(juz: Int): Int

    @Query("SELECT id FROM Ayat where page = :page AND number = :ayahId AND text LIKE '%' || :ayah  || '%'")
    fun getAyaId(ayah: String, ayahId: Int, page: Int): Int

    @Transaction
    @Insert
    fun bookmark(bookmark: Bookmarks)

    @Query("DELETE FROM Bookmarks where aya = :id")
    fun deleteAyaBookmark(id: Long)

    @Query("DELETE FROM Bookmarks where page = :id")
    fun deletePageBookmark(id: Long)

    @Query("SELECT * FROM Bookmarks ORDER BY id DESC")
    fun getBookmarks(): LiveData<List<BookmarkWithAya>>

    @Update(entity = Ayat::class)
    fun updateAya(obj: AyatBookMark)

}