package com.crazyidea.alsalah.data.model

import com.crazyidea.alsalah.data.room.entity.Ayat
import com.crazyidea.alsalah.data.room.entity.Edition
import com.crazyidea.alsalah.data.room.entity.Surah
import com.google.gson.JsonElement


data class QuranNetworkContainer(
    val data: QuranResponseApiModel
)

data class QuranResponseApiModel(
    val surahs: List<SurahResponseApiModel>,
    val edition: EditionResponseApiModel,
    val audio: String,
)

data class SurahResponseApiModel(
    val number: Int,
    val name: String,
    val revelationType: String,
    val ayahs: List<AyatResponseApiModel>
)

data class EditionResponseApiModel(
    val identifier: String,
    val language: String,
    val name: String,
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
fun SurahResponseApiModel.asAyatDatabaseModel(): List<Ayat> {
    return ayahs.map {
        Ayat(
            it.number.toLong(),
            it.text,
            it.numberInSurah,
            it.juz,
            name,
            number,
            it.manzil,
            it.page,
            it.ruku,
            it.hizbQuarter,
            false
        )
    }
}

@JvmName("asDatabaseModelAyatResponseApiModel")
fun EditionResponseApiModel.asEditionDatabaseModel(): Edition {
    return Edition(
        identifier = identifier, name = name, language = language
    )
}

fun List<SurahResponseApiModel>.asDatabaseModel(): List<Surah> {
    return map {
        Surah(
            it.number.toLong(),
            it.name,
            it.revelationType,
            it.ayahs.size,
            it.ayahs.first().page,
            it.ayahs.last().page,
            it.ayahs.first().juz
        )
    }
}