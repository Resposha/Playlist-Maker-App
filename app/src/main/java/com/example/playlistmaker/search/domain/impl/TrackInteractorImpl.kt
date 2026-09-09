package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackInteractorImpl(
    private val repository: TrackRepository
) : TrackInteractor {

    override fun searchTracks(expression: String) : Flow<List<Track>?> {
        return repository.searchTracks(expression).map { resource ->
            when (resource) {
                is Resource.Success -> resource.data
                is Resource.Error -> null
            }
        }
    }
}