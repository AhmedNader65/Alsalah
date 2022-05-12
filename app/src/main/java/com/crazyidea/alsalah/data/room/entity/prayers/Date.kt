package com.crazyidea.alsalah.data.room.entity.prayers

import androidx.room.*

@Entity(
    foreignKeys = arrayOf(
        ForeignKey(
            entity = Timing::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("timingId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Meta::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("metaId"),
            onDelete = ForeignKey.CASCADE
        )
    ), indices = [Index(value = ["readable"])]
)
data class Date(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = 0,
    @ColumnInfo(name = "timingId") val timingId: Long,
    @ColumnInfo(name = "metaId") val metaId: Long,
    @ColumnInfo(name = "readable") val readable: String,
    @ColumnInfo(name = "timestamp") val timestamp: String,
    @ColumnInfo(name = "g_date") val g_date: String,
    @ColumnInfo(name = "g_day") val g_day: String,
    @ColumnInfo(name = "g_month") val g_month: String,
    @ColumnInfo(name = "g_month_en") val g_month_en: String,
    @ColumnInfo(name = "g_year") val g_year: String,
    @ColumnInfo(name = "h_date") val h_date: String,
    @ColumnInfo(name = "h_day") val h_day: String,
    @ColumnInfo(name = "h_day_en") val h_day_en: String,
    @ColumnInfo(name = "h_day_ar") val h_day_ar: String,
    @ColumnInfo(name = "h_month") val h_month: String,
    @ColumnInfo(name = "h_month_en") val h_month_en: String,
    @ColumnInfo(name = "h_month_ar") val h_month_ar: String,
    @ColumnInfo(name = "h_year") val h_year: String,
){

}
