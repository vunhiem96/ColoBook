package com.volio.coloringbook2.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.volio.coloringbook2.model.storybook.saveLocal.StoryBookSave
import com.volio.coloringbook2.models.CalendarEntry
import com.volio.coloringbook2.models.ImageModel


@Database(
    entities = [CalendarEntry::class, ImageModel::class, StoryBookSave::class],
    version = 4
)
abstract class CalendarDatabase : RoomDatabase() {
    abstract fun calendarDao(): CalendarDao
    abstract fun saveStoryDao(): SaveStoryDao
    companion object {
        @Volatile
        private var instance: CalendarDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                CalendarDatabase::class.java, "calendar.db"
            )
                .build()
    }
}