package com.crazyidea.alsalah.data.room.entity.fajr

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class Fajr(
    @PrimaryKey(autoGenerate = true)
    val id: Long?=0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "number") val number: String,
    var checked:Boolean = false
    )
