package com.example.playlistmaker.search.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.search.data.StorageClient
import com.google.gson.Gson
import java.lang.reflect.Type
import androidx.core.content.edit

class SharedPrefsStorageClient<T>(
    private val context: Context,
    private val sharedPrefsKey: String,
    private val dataKey: String,
    private val type: Type
) : StorageClient<T> {
    private val sharedPrefs: SharedPreferences = context.getSharedPreferences(sharedPrefsKey, Context.MODE_PRIVATE)
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