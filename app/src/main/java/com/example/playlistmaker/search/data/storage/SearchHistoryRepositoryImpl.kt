package com.example.playlistmaker.search.data.storage

import com.example.playlistmaker.search.data.StorageClient
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
class SearchHistoryRepositoryImpl(
    private val storage: StorageClient<List<Track>>
) : SearchHistoryRepository {

    override fun getHistory(): List<Track> {
        return storage.getData() ?: emptyList()
    }

    override fun addTrack(newTrack: Track) {
        val history = storage.getData()?.toMutableList() ?: arrayListOf()
        history.removeIf { it.trackId == newTrack.trackId }
        history.add(0, newTrack)
        if (history.size > MAX_SIZE) {
            history.removeAt(history.size - 1)
        }
        storage.storeData(history)
    }

    override fun clearHistory() {
        storage.storeData(emptyList())
    }

    companion object {
        private const val MAX_SIZE = 10
    }
}