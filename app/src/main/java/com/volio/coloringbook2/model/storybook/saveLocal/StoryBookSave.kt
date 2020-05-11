package com.volio.coloringbook2.model.storybook.saveLocal

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.volio.coloringbook2.database.Converters


@Entity(tableName = "story_data")
@TypeConverters(Converters::class)
data class StoryBookSave(
    @PrimaryKey(autoGenerate = false)
    val book_id: String,
    val book_image_url: String,
    val book_name: String,
    val is_pro: String,
    val list: List<ImageStorySave>,
    val priority: String,
    val date:String,
    val time:String
)
