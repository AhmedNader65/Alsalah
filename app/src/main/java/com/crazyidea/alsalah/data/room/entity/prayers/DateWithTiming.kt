package com.crazyidea.alsalah.data.room.entity.prayers

import androidx.room.Embedded
import androidx.room.Relation

data class DateWithTiming(
    @Embedded val date: Date,
    @Relation(
        parentColumn = "timingId",
        entityColumn = "id"
    )
    val timing: Timing
)