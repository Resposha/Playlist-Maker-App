package com.example.playlistmaker.library.data

import com.example.playlistmaker.library.data.converters.PlaylistDbConverter
import com.example.playlistmaker.library.data.converters.TrackDbConverter
import com.example.playlistmaker.library.data.db.PlaylistDatabase
import com.example.playlistmaker.library.data.db.entity.PlaylistTrackCrossRef
import com.example.playlistmaker.library.domain.api.PlaylistRepository
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    override suspend fun getPlaylists(): List<Playlist> = withContext(Dispatchers.IO) {
        val playlistsWithTracks = playlistDatabase.playlistDao().getPlaylistsWithTracks()
        playlistsWithTracks.map { playlistDbConverter.mapPlaylistWithTracksToPlaylist(it) }
    }

    override suspend fun addTrackToPlaylist(
        track: Track,
        playlist: Playlist
    ) {
        val playlistTrackEntity = trackDbConverter.mapTrackToPlaylistTrackEntity(track)
        playlistDatabase.playlistTrackDao().insertTrack(playlistTrackEntity)

        val crossRef = PlaylistTrackCrossRef(
            playlist.playlistId,
            track.trackId
        )

        playlistDatabase.playlistTrackDao().insertCrossRef(crossRef)
    }
}