package com.volio.coloringbook2.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.volio.coloringbook2.models.CalendarEntry
import com.volio.coloringbook2.models.ImageModel


@Database(
    entities = [CalendarEntry::class, ImageModel::class],
    version = 3
)
abstract class CalendarDatabase : RoomDatabase() {
    abstract fun calendarDao(): CalendarDao

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