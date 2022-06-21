package com.crazyidea.alsalah.data.model

import com.crazyidea.alsalah.data.room.entity.Ayat
import com.crazyidea.alsalah.data.room.entity.Surah
import com.google.gson.JsonElement


data class QuranNetworkContainer(
    val data: QuranResponseApiModel
)

data class QuranResponseApiModel(
    val surahs: List<SurahResponseApiModel>,
)

data class SurahResponseApiModel(
    val number: Int,
    val name: String,
    val ayahs: List<AyatResponseApiModel>
)

data class AyatResponseApiModel(
    val number: Int,
    val text: String,
    val numberInSurah: Int,
    val juz: Int,
    val manzil: Int,
    val page: Int,
    val ruku: Int,
    val hizbQuarter: Int,
    val sajda: JsonElement,
)

@JvmName("asDatabaseModelAyatResponseApiModel")
fun List<AyatResponseApiModel>.asDatabaseModel(): List<Ayat> {
    return map {
        Ayat(
            it.number.toLong(),
            it.text,
            it.numberInSurah,
            it.juz,
            it.manzil,
            it.page,
            it.ruku,
            it.hizbQuarter,
            false
        )
    }
}

fun List<SurahResponseApiModel>.asDatabaseModel(): List<Surah> {
    return map { Surah(it.number.toLong(), it.name) }
}