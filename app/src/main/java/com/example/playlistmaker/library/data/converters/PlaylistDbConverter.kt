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

    fun mapPlaylistWithTracksToPlaylistAndTracks(playlistWithTracks: PlaylistWithTracks): Pair<Playlist, List<Track>> {
        val tracks = playlistWithTracks.tracks.map {
            trackDbConverter.mapPlaylistTrackEntityToTrack(it)
        }

        val trackIds = playlistWithTracks.tracks.map { it.trackId }

        val playlist = Playlist(
            playlistWithTracks.playlist.playlistId,
            playlistWithTracks.playlist.name,
            playlistWithTracks.playlist.description,
            playlistWithTracks.playlist.artPath,
            trackIds.map { it.toIntOrNull() ?: 0 },
            tracks.size
        )

        return Pair(playlist, tracks)
    }
}