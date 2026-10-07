package com.example.playlistmaker.library.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.library.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Query("DELETE FROM track_table WHERE track_id = :trackId")
    suspend fun deleteTrackById(trackId: String)

    @Query("SELECT track_id FROM track_table")
    suspend fun getTrackIds(): List<String>

    @Query("SELECT * FROM track_table")
    fun getTracks(): Flow<List<TrackEntity>>
}