package com.example.playlistmaker.library.data

import com.example.playlistmaker.library.data.converters.PlaylistDbConverter
import com.example.playlistmaker.library.data.db.PlaylistDatabase
import com.example.playlistmaker.library.domain.api.NewPlaylistRepository
import com.example.playlistmaker.library.domain.models.Playlist

class NewPlaylistRepositoryImpl(
    private val playlistDatabase: PlaylistDatabase,
    private val playlistDbConverter: PlaylistDbConverter
) : NewPlaylistRepository {

    override suspend fun createPlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConverter.map(playlist)
        playlistDatabase.playlistDao().insertPlaylist(playlistEntity)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConverter.map(playlist)
        playlistDatabase.playlistDao().updatePlaylist(playlistEntity)
    }

    override suspend fun getPlaylists(): List<Playlist> {
        val playlistEntities = playlistDatabase.playlistDao().getPlaylists()
        return playlistEntities.map { playlistDbConverter.map(it) }
    }
}