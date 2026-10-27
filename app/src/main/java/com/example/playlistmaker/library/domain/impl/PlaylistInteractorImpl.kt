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

    override suspend fun deletePlaylist(playlistId: Int, trackIds: List<Int>) {
        playlistRepository.deletePlaylist(playlistId, trackIds)
    }

    override suspend fun getPlaylists(): List<Playlist> {
        return playlistRepository.getPlaylists()
    }

    override fun getPlaylistById(playlistId: Int): Flow<Pair<Playlist, List<Track>>?> {
        return playlistRepository.getPlaylistById(playlistId)
    }

    override suspend fun addTrackToPlaylist(
        track: Track,
        playlist: Playlist
    ) {
        playlistRepository.addTrackToPlaylist(track, playlist)
    }

    override suspend fun removeTrackFromPlaylist(trackId: String, playlistId: Int) {
        playlistRepository.removeTrackFromPlaylist(trackId, playlistId)
    }
}