package com.crazyidea.alsalah.data.room.entity

import androidx.room.*

@Entity
data class Surah(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "revelationType") val revelationType: String,
    @ColumnInfo(name = "numberOfAyahs") val numberOfAyahs: Int,
    @ColumnInfo(name = "page") val page: Int,
    @ColumnInfo(name = "last_page") val last_page: Int,
    @ColumnInfo(name = "juz") val juz: Int,

){
    @Ignore
    var checked: Boolean = false
}

@Entity
data class Ayat(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = 0,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "number") val number: Int,
    @ColumnInfo(name = "juz") val juz: Int,
    @ColumnInfo(name = "surah") val surah: String,
    @ColumnInfo(name = "surah_id") val surah_id: Int,
    @ColumnInfo(name = "manzil") val manzil: Int,
    @ColumnInfo(name = "page") val page: Int,
    @ColumnInfo(name = "ruku") val ruku: Int,
    @ColumnInfo(name = "hizbQuarter") val hizbQuarter: Int,
    @ColumnInfo(name = "sajda") val sajda: Boolean,
)

@Entity
data class Edition(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = 0,
    @ColumnInfo(name = "identifier") val identifier: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "language") val language: String
)


data class SurahWithAyat(
    @Embedded val surah: Surah,
    @Relation(
        parentColumn = "surahId",
        entityColumn = "id"
    )
    val ayat: Ayat
)
