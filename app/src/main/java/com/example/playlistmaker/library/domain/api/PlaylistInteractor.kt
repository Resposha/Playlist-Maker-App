package com.example.playlistmaker.library.domain.api

import com.example.playlistmaker.library.domain.models.Playlist

interface PlaylistInteractor {
    suspend fun createPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun getPlaylists(): List<Playlist>
}