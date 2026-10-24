package com.example.playlistmaker.library.domain.api

import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun deletePlaylist(playlistId: Int, trackIds: List<Int>)

    suspend fun getPlaylists(): List<Playlist>

    fun getPlaylistById(playlistId: Int): Flow<Pair<Playlist, List<Track>>?>

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)

    suspend fun removeTrackFromPlaylist(trackId: String, playlistId: Int)

    fun getTracksByIds(trackIds: List<Int>): Flow<List<Track>>
}