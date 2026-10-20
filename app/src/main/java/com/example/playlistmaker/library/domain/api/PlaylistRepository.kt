package com.example.playlistmaker.library.domain.api

import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun getPlaylists(): List<Playlist>

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)

    suspend fun getPlaylistById(playlistId: Int): Playlist

    fun getTracksByIds(trackIds: List<Int>): Flow<List<Track>>
}