package com.example.playlistmaker.library.data

import com.example.playlistmaker.library.data.converters.PlaylistDbConverter
import com.example.playlistmaker.library.data.converters.TrackDbConverter
import com.example.playlistmaker.library.data.db.PlaylistDatabase
import com.example.playlistmaker.library.domain.api.PlaylistRepository
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track

class PlaylistRepositoryImpl(
    private val playlistDatabase: PlaylistDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val trackDbConverter: TrackDbConverter
) : PlaylistRepository {

    override suspend fun createPlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConverter.mapPlaylistToPlaylistEntity(playlist)
        playlistDatabase.playlistDao().insertPlaylist(playlistEntity)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConverter.mapPlaylistToPlaylistEntity(playlist)
        playlistDatabase.playlistDao().updatePlaylist(playlistEntity)
    }

    override suspend fun getPlaylists(): List<Playlist> {
        val playlistEntities = playlistDatabase.playlistDao().getPlaylists()
        return playlistEntities.map { playlistDbConverter.mapPlaylistEntityToPlaylist(it) }
    }

    override suspend fun addTrackToPlaylist(
        track: Track,
        playlist: Playlist
    ) {
        val trackId = track.trackId.toIntOrNull() ?: 0

        val updatedTrackIds = playlist.trackIds.toMutableList().apply { add(trackId) }
        val updatedCount = playlist.numberOfTracks + 1

        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            numberOfTracks = updatedCount
        )

        val playlistEntity = playlistDbConverter.mapPlaylistToPlaylistEntity(updatedPlaylist)
        playlistDatabase.playlistDao().updatePlaylist(playlistEntity)

        val playlistTrackEntity = trackDbConverter.mapTrackToPlaylistTrackEntity(track)
        playlistDatabase.playlistTrackDao().insertTrack(playlistTrackEntity)
    }
}