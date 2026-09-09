package com.example.playlistmaker.search.data.network

import android.util.Log
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.dto.TrackSearchResponse
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(
    private val networkClient: NetworkClient
) : TrackRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
                Log.e("ErrorLogTag", "Отсутствует сетевое подключение")
                emit(Resource.Error())
            }
            200 -> {
                with(response as TrackSearchResponse) {
                    val data = results.map {
                        Track(
                            it.trackId,
                            it.trackName,
                            it.artistName,
                            it.trackTimeMillis,
                            it.artworkUrl100,
                            it.collectionName,
                            it.releaseDate,
                            it.primaryGenreName,
                            it.country,
                            it.previewUrl
                        )
                    }
                    emit(Resource.Success(data))
                }
            }
            400 -> {
                Log.e("ErrorLogTag", "Ошибка запроса")
                emit(Resource.Error())
            }
            else -> {
                Log.e("ErrorLogTag", "Ошибка сервера")
                emit(Resource.Error())
            }
        }
    }
}