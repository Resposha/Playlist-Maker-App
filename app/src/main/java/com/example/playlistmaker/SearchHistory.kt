package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import androidx.core.content.edit

class SearchHistory(private val sharedPrefs: SharedPreferences) {
    companion object {
        const val SEARCH_HISTORY = "search_history"
    }

    private val gson = Gson()

    fun get(): ArrayList<Track> {
        val json = sharedPrefs.getString(SEARCH_HISTORY, "")
        return if (json.isNullOrEmpty()) {
            ArrayList()
        } else {
            gson.fromJson(json, Array<Track>::class.java).toCollection(ArrayList())
        }
    }

    fun save(tracks: ArrayList<Track>) {
        val json = gson.toJson(tracks)
        sharedPrefs.edit {
            putString(SEARCH_HISTORY, json)
        }
    }

    fun addTrack(newTrack: Track) {
        val history = get()
        for (track in history) {
            if (track.id == newTrack.id) {
                history.remove(track)
                break
            }
        }
        history.add(0, newTrack)
        if (history.size > 10) history.removeAt(history.size - 1)
        save(history)
    }

    fun clear() {
        sharedPrefs.edit {
            remove(SEARCH_HISTORY)
        }
    }
}