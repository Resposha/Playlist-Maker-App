package com.example.playlistmaker.library.domain.api

import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track

interface PlaylistInteractor {
    suspend fun createPlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun getPlaylists(): List<Playlist>

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)
}