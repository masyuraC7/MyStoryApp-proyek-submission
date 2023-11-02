package com.mc7.mystoryapp.data.local.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mc7.mystoryapp.data.remote.response.StoryItem

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(story: List<StoryItem>)

    @Query("SELECT * FROM story")
    fun getStories(): PagingSource<Int, StoryItem>

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}