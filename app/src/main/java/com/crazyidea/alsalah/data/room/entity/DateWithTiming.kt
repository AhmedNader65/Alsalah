package com.crazyidea.alsalah.data.room.entity

import androidx.room.Embedded
import androidx.room.Relation

data class DateWithTiming(
    @Embedded val date: Date,
    @Relation(
        parentColumn = "timingId",
        entityColumn = "id"
    )
    val timingsList: Timing
)