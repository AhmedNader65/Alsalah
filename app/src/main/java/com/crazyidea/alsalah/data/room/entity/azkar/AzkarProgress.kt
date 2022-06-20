package com.crazyidea.alsalah.data.room.entity.azkar

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class AzkarProgress(
    @PrimaryKey
    @ColumnInfo(name = "day") val day: String,
    @ColumnInfo(name = "morning") var morning: Int=0,
    @ColumnInfo(name = "evening") var evening: Int=0,
    @ColumnInfo(name = "sleeping") var sleeping: Int=0,
    @ColumnInfo(name = "prayer") var prayer: Int=0,
    @ColumnInfo(name = "sebha") var sebha: Int=0,
    @ColumnInfo(name = "more") var more: Int=0,
    )
