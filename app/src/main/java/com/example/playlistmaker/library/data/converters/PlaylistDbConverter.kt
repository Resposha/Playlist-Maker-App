package com.example.playlistmaker.library.data.converters

import com.example.playlistmaker.library.data.db.entity.PlaylistEntity
import com.example.playlistmaker.library.data.db.entity.PlaylistWithTracks
import com.example.playlistmaker.library.domain.models.Playlist

class PlaylistDbConverter {
    fun mapPlaylistToPlaylistEntity(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.playlistId,
            playlist.name,
            playlist.description,
            playlist.artPath,
        )
    }

    fun mapPlaylistWithTracksToPlaylist(playlistWithTracks: PlaylistWithTracks): Playlist {
        val trackIds = playlistWithTracks.tracks.map { it.trackId.toIntOrNull() ?: 0 }

        return Playlist(
            playlistWithTracks.playlist.playlistId,
            playlistWithTracks.playlist.name,
            playlistWithTracks.playlist.description,
            playlistWithTracks.playlist.artPath,
            trackIds,
            trackIds.size
        )
    }
}