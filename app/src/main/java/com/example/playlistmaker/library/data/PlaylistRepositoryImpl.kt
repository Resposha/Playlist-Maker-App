package com.example.playlistmaker.library.data

import com.example.playlistmaker.library.data.converters.PlaylistDbConverter
import com.example.playlistmaker.library.data.converters.TrackDbConverter
import com.example.playlistmaker.library.data.db.PlaylistDatabase
import com.example.playlistmaker.library.data.db.entity.PlaylistTrackCrossRef
import com.example.playlistmaker.library.domain.api.PlaylistRepository
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    override suspend fun getPlaylistById(playlistId: Int): Playlist = withContext(Dispatchers.IO) {
        val playlistWithTracks = playlistDatabase.playlistDao().getPlaylistWithTracksById(playlistId)
        playlistDbConverter.mapPlaylistWithTracksToPlaylist(playlistWithTracks)
    }

    override fun getTracksByIds(trackIds: List<Int>): Flow<List<Track>> {
        return playlistDatabase.playlistTrackDao().getTracks().map { playlistTrackEntityList ->
            playlistTrackEntityList
                .filter { playlistTrackEntity ->
                    val trackId = playlistTrackEntity.trackId.toIntOrNull()
                    trackId != null && trackIds.contains(trackId)
                }
                .map { playlistTrackEntity -> trackDbConverter.mapPlaylistTrackEntityToTrack(playlistTrackEntity) }
        }
    }
}