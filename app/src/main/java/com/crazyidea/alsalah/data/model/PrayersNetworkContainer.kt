package com.crazyidea.alsalah.data.model

import com.crazyidea.alsalah.data.room.entity.prayers.Date
import com.crazyidea.alsalah.data.room.entity.prayers.Meta
import com.crazyidea.alsalah.data.room.entity.prayers.Timing

data class PrayersNetworkContainer(
    val prayers: List<PrayerResponseApiModel>
)

data class PrayerResponseApiModel(
    val timings: PrayerTimingApiModel,
    val date: PrayerDateApiModel,
    val meta: PrayerMetaApiModel
)

data class PrayerMetaApiModel(
    val method: PrayerMethodApiModel,
    val school: String
)

data class PrayerMethodApiModel(
    val id: Int,
    val name: String,
)

data class PrayersCalculationMethods(
    val id: Int,
    val Name: String, var checked: Boolean = false
)

data class PrayerDateApiModel(
    val readable: String,
    val timestamp: String,
    val hijri: PrayerDetailedDateApiModel,
    val gregorian: PrayerDetailedDateApiModel
)

data class PrayerDetailedDateApiModel(
    val date: String,
    val format: String,
    val day: String,
    val weekday: TranslateModel,
    val month: TranslateModel,
    val year: String,
)

data class PrayerTimingApiModel(
    val Fajr: String,
    val Sunrise: String,
    val Dhuhr: String,
    val Asr: String,
    val Sunset: String,
    val Maghrib: String,
    val Isha: String,
    val Imsak: String,
    val Midnight: String,
)

fun PrayersNetworkContainer.asTimingDatabaseModel(): Array<Timing> {
    return prayers.map {
        Timing(
            null,
            it.timings.Fajr.substring(0, 5),
            it.timings.Sunrise.substring(0, 5),
            it.timings.Dhuhr.substring(0, 5),
            it.timings.Asr.substring(0, 5),
            it.timings.Sunset.substring(0, 5),
            it.timings.Maghrib.substring(0, 5),
            it.timings.Isha.substring(0, 5),
            it.timings.Imsak.substring(0, 5),
            it.timings.Midnight.substring(0, 5)
        )
    }.toTypedArray()
}

fun PrayersNetworkContainer.asMetaDatabaseModel(): Meta {
    return Meta(
        null,
        prayers.first().meta.method.id,
        prayers.first().date.gregorian.month.number,
        if (prayers.first().meta.school == "STANDARD") 0 else 1
    )

}

fun PrayersNetworkContainer.asDateDatabaseModel(): Date {
    return Date(
        null,
        0,
        0,
        prayers.first().date.readable,
        prayers.first().date.timestamp,
        prayers.first().date.gregorian.date,
        prayers.first().date.gregorian.day,
        prayers.first().date.gregorian.month.number,
        prayers.first().date.gregorian.month.en,
        prayers.first().date.gregorian.year,
        prayers.first().date.hijri.date,
        prayers.first().date.hijri.day,
        prayers.first().date.hijri.weekday.en,
        prayers.first().date.hijri.weekday.ar,
        prayers.first().date.hijri.month.number,
        prayers.first().date.hijri.month.en,
        prayers.first().date.hijri.month.ar,
        prayers.first().date.hijri.year
    )

}