package com.crazyidea.alsalah.data.room.entity

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize
import java.sql.Time

@Entity
@Parcelize
data class Khatma(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = 0,
    @ColumnInfo(name = "name") var name: String?,
    @ColumnInfo(name = "type") var type: String?,
    @ColumnInfo(name = "pages_num") var pages_num: Int = 0,
    @ColumnInfo(name = "read") var read: Int = 0,
    @ColumnInfo(name = "days") var days: Int = 0,
    @ColumnInfo(name = "onTrack") var onTrack: Boolean = true,
    @ColumnInfo(name = "time") var time: Long?,
    @ColumnInfo(name = "notify") var notify: Boolean = true,
    @ColumnInfo(name = "status") var status :Int= 0,
) : Parcelable {}

@Entity
data class KhatmaUpdate(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = 0,
    @ColumnInfo(name = "read") val read: Int,
    @ColumnInfo(name = "status") val status: Int,
)