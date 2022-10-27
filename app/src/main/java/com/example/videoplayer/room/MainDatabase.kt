package com.example.videoplayer.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.videoplayer.VideoDataEntity

@Database(entities = [VideoDataEntity::class], version = 1, exportSchema = false)
abstract class MainDatabase: RoomDatabase() {
    abstract fun databaseDao(): DatabaseDao
}