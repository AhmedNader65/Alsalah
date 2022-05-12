package com.crazyidea.alsalah.data.room.entity.azkar

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class Azkar(
    @PrimaryKey(autoGenerate = true)
    val id: Long?=0,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "count") val count: Int,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "reference") val reference: String,
    @ColumnInfo(name = "content") val content: String,
    )
