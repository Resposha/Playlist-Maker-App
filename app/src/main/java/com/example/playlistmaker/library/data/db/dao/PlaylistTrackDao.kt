package com.example.playlistmaker.library.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.library.data.db.entity.PlaylistTrackCrossRef
import com.example.playlistmaker.library.data.db.entity.PlaylistTrackEntity

@Dao
interface PlaylistTrackDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackEntity)

    @Query("DELETE FROM playlist_track_table WHERE track_id = :trackId")
    suspend fun deleteTrackById(trackId: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRef(crossRef: PlaylistTrackCrossRef)

    @Delete
    suspend fun deleteCrossRef(crossRef: PlaylistTrackCrossRef)

    @Query("DELETE FROM playlist_track_cross_ref WHERE playlist_id = :playlistId")
    suspend fun deleteCrossRefsByPlaylistId(playlistId: Int)

    @Query("SELECT * FROM playlist_track_cross_ref WHERE track_id = :trackId")
    suspend fun getCrossRefsByTrackId(trackId: String): List<PlaylistTrackCrossRef>

    @Query("SELECT track_id FROM playlist_track_cross_ref WHERE playlist_id = :playlistId ORDER BY added_at DESC")
    suspend fun getTrackIdsSortedByAddedAt(playlistId: Int): List<String>
}