package com.volio.coloringbook2.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image_data")
data class ImageModel(
    @PrimaryKey(autoGenerate = false)
    val name: String,
    var type: Int = 0,
    var id: Int = 0,
    var percent: Int = 0,
    var pixelWhite: Int = 0)
