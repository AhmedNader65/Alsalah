package com.crazyidea.alsalah.data.room.entity.azkar

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class AzkarProgress(
    @PrimaryKey
    @ColumnInfo(name = "day") val day: String,
    @ColumnInfo(name = "morning") var morning: Int,
    @ColumnInfo(name = "evening") var evening: Int,
    @ColumnInfo(name = "sleeping") var sleeping: Int,
    @ColumnInfo(name = "prayer") var prayer: Int,
    @ColumnInfo(name = "sebha") var sebha: Int,
    @ColumnInfo(name = "more") var more: Int,
    )
