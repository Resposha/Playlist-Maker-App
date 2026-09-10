package com.example.playlistmaker.search.data.storage

import com.example.playlistmaker.search.data.StorageClient
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchHistoryRepositoryImpl(
    private val storage: StorageClient<List<Track>>
) : SearchHistoryRepository {

    override suspend fun getHistory(): List<Track> = withContext(Dispatchers.IO) {
        storage.getData() ?: emptyList()
    }

    override suspend fun addTrack(newTrack: Track) = withContext(Dispatchers.IO) {
        val history = storage.getData()?.toMutableList() ?: arrayListOf()
        history.removeIf { it.trackId == newTrack.trackId }
        history.add(0, newTrack)
        if (history.size > MAX_SIZE) {
            history.removeAt(history.size - 1)
        }
        storage.storeData(history)
    }

    override suspend fun clearHistory() = withContext(Dispatchers.IO) {
        storage.storeData(emptyList())
    }

    companion object {
        private const val MAX_SIZE = 10
    }
}