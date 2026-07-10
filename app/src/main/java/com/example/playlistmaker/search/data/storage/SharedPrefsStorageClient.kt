package com.example.playlistmaker.search.data.storage

import android.content.SharedPreferences
import com.example.playlistmaker.search.data.StorageClient
import com.google.gson.Gson
import java.lang.reflect.Type
import androidx.core.content.edit

class SharedPrefsStorageClient<T>(
    private val sharedPrefs: SharedPreferences,
    private val dataKey: String,
    private val type: Type
) : StorageClient<T> {
    private val gson = Gson()

    override fun storeData(data: T) {
        sharedPrefs.edit { putString(dataKey, gson.toJson(data, type)) }
    }

    override fun getData(): T? {
        val dataJson = sharedPrefs.getString(dataKey, null)
        return if (dataJson.isNullOrEmpty()) {
            null
        } else {
            gson.fromJson(dataJson, type)
        }
    }
}