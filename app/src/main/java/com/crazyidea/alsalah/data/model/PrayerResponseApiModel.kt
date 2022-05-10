package com.crazyidea.alsalah.data.model

data class PrayerResponseApiModel(
    val timings: PrayerTimingApiModel,
    val date: PrayerDateApiModel,
    val meta: PrayerMetaApiModel
)
