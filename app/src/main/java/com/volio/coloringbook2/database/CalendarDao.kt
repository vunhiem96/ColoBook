package com.volio.coloringbook2.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.volio.coloringbook2.model.storybook.saveLocal.StoryBookSave
import com.volio.coloringbook2.models.CalendarEntry
import com.volio.coloringbook2.models.ImageModel


@Dao
interface CalendarDao {

//    @Query("select * from weather_location where vitri = $WEATHER_LOCATION_ID")
//    fun getLocation(): LiveData<WeatherLocation>
//
//    @Query("select * from weather_location where vitri = $WEATHER_LOCATION_ID")
//    fun getLocationNonLive(): WeatherLocation?
//
//
//    @Query("select * from weather_location where vitri = :vitri1")
//    fun getLocationByLocation(vitri1: String?): LiveData<WeatherLocation>

//    @Query("select * from calendar")
//    fun getLocationByLocation(vitri1: String?): LiveData<WeatherLocation>


    // database of calendar
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertCalendar(calendar: CalendarEntry)



    @Query("select * from calendar where date =:date1")
    fun getCalendarFromDate(date1: String?): CalendarEntry?


    // database of list image
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertImage(image: ImageModel)

    @Query("select * from image_data where name =:image")
    fun getImageFromName(image: String):List<ImageModel>

    @Query("select * from image_data where urlGoc =:url")
    fun getImageFromUrlGoc(url: String):List<ImageModel>

    @Query("select pixelWhite from image_data where name =:image ")
    fun getPixelWhite(image: String): Int

    @Query("select percent from image_data where name =:image ")
    fun getPercentImage(image: String): Int

    @Query("select * from image_data")
    fun getImage(): List<ImageModel>

//    @Query("update image_data set percent =:percent where name =:percent")
//    fun updatePercentImage(image: String, percent: Int)

    @Query("delete FROM image_data WHERE name = :name")
    fun deleteImage(name: String)


//    UPDATE table_name
//    SET column1 = value1, column2 = value2...., columnN = valueN
//    WHERE [condition];


}