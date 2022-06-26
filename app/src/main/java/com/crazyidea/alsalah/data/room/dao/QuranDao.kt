package com.crazyidea.alsalah.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.crazyidea.alsalah.data.room.entity.Ayat
import com.crazyidea.alsalah.data.room.entity.Edition
import com.crazyidea.alsalah.data.room.entity.Surah

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


    @Query("DELETE FROM Ayat")
    fun emptyAyat()

    @Query("DELETE FROM Surah")
    fun emptySurah()

    @Query("DELETE FROM Edition")
    fun emptyEdition()
}