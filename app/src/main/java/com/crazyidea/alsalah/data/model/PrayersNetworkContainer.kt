package com.crazyidea.alsalah.data.model

import com.crazyidea.alsalah.data.room.entity.prayers.Date
import com.crazyidea.alsalah.data.room.entity.prayers.Meta
import com.crazyidea.alsalah.data.room.entity.prayers.Timing


data class PrayersNetworkContainer(
    val data: List<PrayerResponseApiModel>
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

fun PrayerTimingApiModel.asTimingDatabaseModel(): Timing {
    return Timing(
        null,
        Fajr.substring(0, 5),
        Sunrise.substring(0, 5),
        Dhuhr.substring(0, 5),
        Asr.substring(0, 5),
        Sunset.substring(0, 5),
        Maghrib.substring(0, 5),
        Isha.substring(0, 5),
        Imsak.substring(0, 5),
        Midnight.substring(0, 5)
    )
}

fun PrayerResponseApiModel.asMetaDatabaseModel(): Meta {
    return Meta(
        null,
        meta.method.id,
        date.gregorian.month.number,
        if (meta.school == "STANDARD") 0 else 1
    )

}

fun PrayerDateApiModel.asDateDatabaseModel(): Date {
    return Date(
        null,
        0,
        0,
        readable,
        timestamp,
        gregorian.date,
        gregorian.day,
        gregorian.month.number,
        gregorian.month.en,
        gregorian.year,
        hijri.date,
        hijri.day,
        hijri.weekday.en,
        hijri.weekday.ar,
        hijri.month.number,
        hijri.month.en,
        hijri.month.ar,
        hijri.year
    )

}