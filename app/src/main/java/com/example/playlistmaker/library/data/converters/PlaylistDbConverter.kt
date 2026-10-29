package com.example.playlistmaker.library.data.converters

import com.example.playlistmaker.library.data.db.entity.PlaylistEntity
import com.example.playlistmaker.library.data.db.entity.PlaylistWithTracks
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track

class PlaylistDbConverter {
    private val trackDbConverter = TrackDbConverter()

    fun mapPlaylistToPlaylistEntity(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.playlistId,
            playlist.name,
            playlist.description,
            playlist.artPath,
        )
    }

    fun mapPlaylistWithTracksToPlaylistAndTracks(
        playlistWithTracks: PlaylistWithTracks,
        trackIdsOrder: List<String> = emptyList()
    ): Pair<Playlist, List<Track>> {
        val rawTracks = playlistWithTracks.tracks.map {
            trackDbConverter.mapPlaylistTrackEntityToTrack(it)
        }

        val finalTracks = if (trackIdsOrder.isNotEmpty()) {
            rawTracks.sortedBy { track -> trackIdsOrder.indexOf(track.trackId) }
        } else {
            rawTracks
        }

        val finalTrackIds = if (trackIdsOrder.isNotEmpty()) {
            trackIdsOrder.map { it.toIntOrNull() ?: 0 }
        } else {
            playlistWithTracks.tracks.map { it.trackId.toIntOrNull() ?: 0 }
        }

        val playlist = Playlist(
            playlistWithTracks.playlist.playlistId,
            playlistWithTracks.playlist.name,
            playlistWithTracks.playlist.description,
            playlistWithTracks.playlist.artPath,
            finalTrackIds,
            finalTracks.size
        )

        return Pair(playlist, finalTracks)
    }
}