package com.example.playlistmaker.library.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.library.data.db.entity.FavoriteTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: FavoriteTrackEntity)

    @Query("DELETE FROM favorite_track_table WHERE track_id = :trackId")
    suspend fun deleteTrackById(trackId: String)

    @Query("SELECT track_id FROM favorite_track_table")
    suspend fun getTrackIds(): List<String>

    @Query("SELECT * FROM favorite_track_table")
    fun getTracks(): Flow<List<FavoriteTrackEntity>>
}