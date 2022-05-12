package com.crazyidea.alsalah.data.room.entity.prayers

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Meta(
    @PrimaryKey(autoGenerate = true)
    val id: Long?=0,
    @ColumnInfo(name = "method") val method: Int,
    @ColumnInfo(name = "city") val city: String,
    @ColumnInfo(name = "month") val month: String,
)
