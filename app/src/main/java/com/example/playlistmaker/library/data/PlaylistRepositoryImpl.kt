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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
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

    override suspend fun deletePlaylist(playlistId: Int, trackIds: List<Int>) = withContext(Dispatchers.IO) {
        playlistDatabase.playlistTrackDao().deletePlaylistAndUnusedTracks(
            playlistDatabase.playlistDao(),
            playlistId,
            trackIds
        )
    }

    override suspend fun getPlaylists(): List<Playlist> = withContext(Dispatchers.IO) {
        val playlistsWithTracks = playlistDatabase.playlistDao().getPlaylistsWithTracks()
        playlistsWithTracks.map { playlistWithTracks ->
            val (playlist, _) = playlistDbConverter.mapPlaylistWithTracksToPlaylistAndTracks(playlistWithTracks)
            playlist
        }
    }

    override fun getPlaylistById(playlistId: Int): Flow<Pair<Playlist, List<Track>>?> {
        return playlistDatabase.playlistDao().getPlaylistWithTracksById(playlistId)
            .flatMapLatest { playlistWithTracks ->
                flow {
                    if (playlistWithTracks == null) {
                        emit(null)
                    } else {
                        val sortedIds = playlistDatabase.playlistTrackDao().getTrackIdsSortedByAddedAt(playlistId)
                        val playlistWithSortedTracks = playlistDbConverter.mapPlaylistWithTracksToPlaylistAndTracks(playlistWithTracks, sortedIds)
                        emit(playlistWithSortedTracks)
                    }
                }
            }
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

    override suspend fun removeTrackFromPlaylist(trackId: String, playlistId: Int) = withContext(Dispatchers.IO) {
        playlistDatabase.playlistTrackDao().removeTrackFromPlaylist(trackId, playlistId)
    }
}