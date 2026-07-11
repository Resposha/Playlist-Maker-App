package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import java.io.IOException

class RetrofitNetworkClient(
    private val iTunesSearchService: TrackSearchApi
) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        return if (dto is TrackSearchRequest) {
            try {
                val response = iTunesSearchService.search(dto.expression).execute()
                val body = response.body() ?: Response()
                body.apply { resultCode = response.code() }
            } catch (e: IOException) {
                Response().apply { resultCode = -1 }
            }
        } else {
            Response().apply { resultCode = 400 }
        }
    }
}