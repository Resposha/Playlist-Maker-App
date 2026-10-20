package com.example.playlistmaker.library.domain.impl

import com.example.playlistmaker.library.domain.api.PlaylistInteractor
import com.example.playlistmaker.library.domain.api.PlaylistRepository
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : PlaylistInteractor {

    override suspend fun createPlaylist(playlist: Playlist) {
        playlistRepository.createPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistRepository.updatePlaylist(playlist)
    }

    override suspend fun getPlaylists(): List<Playlist> {
        return playlistRepository.getPlaylists()
    }

    override suspend fun addTrackToPlaylist(
        track: Track,
        playlist: Playlist
    ) {
        playlistRepository.addTrackToPlaylist(track, playlist)
    }

    override suspend fun getPlaylistById(playlistId: Int): Playlist {
        return playlistRepository.getPlaylistById(playlistId)
    }

    override fun getTracksByIds(trackIds: List<Int>): Flow<List<Track>> {
        return playlistRepository.getTracksByIds(trackIds)
    }
}