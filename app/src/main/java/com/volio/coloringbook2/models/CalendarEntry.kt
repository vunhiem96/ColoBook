package com.volio.coloringbook2.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calendar")
data class CalendarEntry(
    @PrimaryKey(autoGenerate = false)
    val date: String,
    val img1: String,
    val img2: String,
    val img3: String
)