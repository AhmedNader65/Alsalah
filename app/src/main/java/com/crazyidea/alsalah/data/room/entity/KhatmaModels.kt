package com.crazyidea.alsalah.data.room.entity

import androidx.room.*

@Entity
data class Khatma(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = 0,
    @ColumnInfo(name = "name") var name: String?,
    @ColumnInfo(name = "type") var type: String?,
    @ColumnInfo(name = "pages_num") var pages_num: Int=0,
    @ColumnInfo(name = "start") var start: Int =0,
    @ColumnInfo(name = "read") var read: Int =0,
    @ColumnInfo(name = "days") var days: Int=0,
    @ColumnInfo(name = "onTrack") var onTrack: Boolean = true,
  ) {}
