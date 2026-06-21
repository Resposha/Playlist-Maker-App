package com.example.playlistmaker.data.network

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson

class SearchHistoryRepositoryImpl(private val sharedPrefs: SharedPreferences) : SearchHistoryRepository {
    private val gson = Gson()

    override fun getHistory(): List<Track> {
        val json = sharedPrefs.getString(SEARCH_HISTORY, "")
        return if (json.isNullOrEmpty()) {
            emptyList()
        } else {
            gson.fromJson(json, Array<Track>::class.java).toList()
        }
    }

    override fun addTrack(newTrack: Track) {
        val history = getHistory().toMutableList()
        history.removeIf { it.trackId == newTrack.trackId }
        history.add(0, newTrack)
        if (history.size > MAX_SIZE) history.removeAt(history.size - 1)
        saveHistory(history)
    }

    override fun clearHistory() {
        sharedPrefs.edit {
            remove(SEARCH_HISTORY)
        }
    }

    private fun saveHistory(tracks: List<Track>) {
        val json = gson.toJson(tracks)
        sharedPrefs.edit {
            putString(SEARCH_HISTORY, json)
        }
    }

    companion object {
        private const val SEARCH_HISTORY = "search_history"
        private const val MAX_SIZE = 10
    }
}