package com.volio.coloringbook2.database

import androidx.room.*
import com.volio.coloringbook2.model.storybook.saveLocal.StoryBookSave

@Dao
interface SaveStoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)

    fun insertStoty(story: StoryBookSave)

    @Query("select * from story_data where book_id =:bookId")
    fun getbookId(bookId: String): List<StoryBookSave>

    @Query("select * from story_data" )
    fun getAllStory(): List<StoryBookSave>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateStory(story: StoryBookSave)

    @Query("DELETE FROM story_data WHERE book_id =:bookId")
    fun deleteById(bookId: String)
}