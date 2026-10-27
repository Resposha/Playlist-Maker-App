package com.example.playlistmaker.library.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "playlist_track_cross_ref",
    primaryKeys = ["playlist_id", "track_id"]
)
data class PlaylistTrackCrossRef(
    @ColumnInfo("playlist_id")
    val playlistId: Int,
    @ColumnInfo("track_id")
    val trackId: String,
    @ColumnInfo("added_at")
    val addedAt: Long = System.currentTimeMillis()
)