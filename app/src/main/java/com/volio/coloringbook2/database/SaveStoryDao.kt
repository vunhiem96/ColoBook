package com.volio.coloringbook2.database

import androidx.room.*
import com.volio.coloringbook2.model.storybook.saveLocal.ImageStorySave
import com.volio.coloringbook2.model.storybook.saveLocal.StoryBookSave

@Dao
interface SaveStoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    @Insert
    fun insertStoty(story: StoryBookSave)

//    @Query("select list from story_data where book_id =:id ")
//    fun getPercentImage(id: String): List<ImageStorySave>

    @Query("select * from story_data where book_id =:bookId")
    fun getbookId(bookId: String): List<StoryBookSave>

    @Query("select * from story_data" )
    fun getAllStory(): List<StoryBookSave>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateStory(story: StoryBookSave)
}