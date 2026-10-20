package com.example.playlistmaker.library.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.library.data.db.entity.PlaylistTrackCrossRef
import com.example.playlistmaker.library.data.db.entity.PlaylistTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistTrackDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRef(crossRef: PlaylistTrackCrossRef)

    @Query("SELECT * FROM playlist_track_table")
    fun getTracks(): Flow<List<PlaylistTrackEntity>>
}