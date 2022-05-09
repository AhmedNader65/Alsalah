package com.crazyidea.alsalah.data.model

data class PrayerDetailedDateApiModel(
    val date:String,
    val format:String,
    val day:String,
    val weekday:TranslateModel,
    val month:TranslateModel,
    val year:String,
)
