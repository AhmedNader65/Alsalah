package com.crazyidea.alsalah.data.model

data class PrayerDateApiModel(
    val readable:String,
    val timestamp:String,
    val hijri:PrayerDetailedDateApiModel,
    val gregorian:PrayerDetailedDateApiModel
)
