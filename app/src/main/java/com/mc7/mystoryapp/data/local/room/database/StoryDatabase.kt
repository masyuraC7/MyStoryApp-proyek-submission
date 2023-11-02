package com.mc7.mystoryapp.data.local.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mc7.mystoryapp.data.local.entity.RemoteKeys
import com.mc7.mystoryapp.data.local.room.dao.RemoteKeysDao
import com.mc7.mystoryapp.data.local.room.dao.StoryDao
import com.mc7.mystoryapp.data.remote.response.StoryItem

@Database(entities = [StoryItem::class, RemoteKeys::class], version = 2, exportSchema = false)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao

    abstract fun remoteKeysDao(): RemoteKeysDao
}