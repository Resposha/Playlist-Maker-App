package com.example.playlistmaker.library.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.playlistmaker.library.data.db.entity.PlaylistTrackCrossRef
import com.example.playlistmaker.library.data.db.entity.PlaylistTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistTrackDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackEntity)

    @Query("DELETE FROM playlist_track_table WHERE track_id = :trackId")
    suspend fun deleteTrackById(trackId: String)

    @Query("SELECT * FROM playlist_track_table")
    fun getTracks(): Flow<List<PlaylistTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRef(crossRef: PlaylistTrackCrossRef)

    @Delete
    suspend fun deleteCrossRef(crossRef: PlaylistTrackCrossRef)

    @Query("DELETE FROM playlist_track_cross_ref WHERE playlist_id = :playlistId")
    suspend fun deleteCrossRefsByPlaylistId(playlistId: Int)

    @Query("SELECT * FROM playlist_track_cross_ref WHERE track_id = :trackId")
    suspend fun getCrossRefsByTrackId(trackId: String): List<PlaylistTrackCrossRef>

    @Transaction
    suspend fun removeTrackFromPlaylist(trackId: String, playlistId: Int) {
        deleteCrossRef(
            PlaylistTrackCrossRef(playlistId, trackId)
        )

        val remainingRefs = getCrossRefsByTrackId(trackId)
        if (remainingRefs.isEmpty()) {
            deleteTrackById(trackId)
        }
    }

    @Transaction
    suspend fun deletePlaylistAndUnusedTracks(
        playlistDao: PlaylistDao,
        playlistId: Int,
        trackIds: List<Int>
    ) {
        playlistDao.deletePlaylistById(playlistId)

        deleteCrossRefsByPlaylistId(playlistId)

        trackIds.forEach { trackId ->
            val remainingRefs = getCrossRefsByTrackId(trackId.toString())

            if (remainingRefs.isEmpty()) {
                deleteTrackById(trackId.toString())
            }
        }
    }
}