package com.crazyidea.alsalah.data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class Timing(
    @PrimaryKey(autoGenerate = true)
    val id: Long?=0,
    @ColumnInfo(name = "Fajr") val Fajr: String,
    @ColumnInfo(name = "Sunrise") val Sunrise: String,
    @ColumnInfo(name = "Dhuhr") val Dhuhr: String,
    @ColumnInfo(name = "Asr") val Asr: String,
    @ColumnInfo(name = "Sunset") val Sunset: String,
    @ColumnInfo(name = "Maghrib") val Maghrib: String,
    @ColumnInfo(name = "Isha") val Isha: String,
    @ColumnInfo(name = "Imsak") val Imsak: String,
    @ColumnInfo(name = "Midnight") val Midnight: String,
    )
