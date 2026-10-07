package com.example.playlistmaker.library.domain.impl

import com.example.playlistmaker.library.domain.api.NewPlaylistInteractor
import com.example.playlistmaker.library.domain.api.NewPlaylistRepository
import com.example.playlistmaker.library.domain.models.Playlist

class NewPlaylistInteractorImpl(
    private val newPlaylistRepository: NewPlaylistRepository
) : NewPlaylistInteractor {

    override suspend fun createPlaylist(playlist: Playlist) {
        newPlaylistRepository.createPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        newPlaylistRepository.updatePlaylist(playlist)
    }

    override suspend fun getPlaylists(): List<Playlist> {
        return newPlaylistRepository.getPlaylists()
    }
}