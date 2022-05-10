package com.crazyidea.alsalah.data.room.entity

import androidx.room.Embedded
import androidx.room.Relation

data class DateAndMeta(
    @Embedded val date: Date,
    @Relation(
        parentColumn = "id",
        entityColumn = "metaId"
    )
    val meta: Meta
)