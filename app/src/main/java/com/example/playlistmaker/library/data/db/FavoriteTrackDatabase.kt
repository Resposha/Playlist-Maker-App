package com.example.playlistmaker.library.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.library.data.db.dao.FavoriteTrackDao
import com.example.playlistmaker.library.data.db.entity.FavoriteTrackEntity

@Database(version = 1, entities = [FavoriteTrackEntity::class])
abstract class FavoriteTrackDatabase : RoomDatabase()  {
    abstract fun favoriteTrackDao(): FavoriteTrackDao
}