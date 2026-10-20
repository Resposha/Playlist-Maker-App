package com.example.playlistmaker.library.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.playlistmaker.library.data.db.entity.PlaylistEntity
import com.example.playlistmaker.library.data.db.entity.PlaylistWithTracks

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Transaction
    @Query("SELECT * FROM playlist_table")
    fun getPlaylistsWithTracks(): List<PlaylistWithTracks>

    @Transaction
    @Query("SELECT * FROM playlist_table WHERE playlist_id = :playlistId")
    suspend fun getPlaylistWithTracksById(playlistId: Int): PlaylistWithTracks
}