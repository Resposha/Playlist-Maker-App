package com.example.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.api.FavoriteTracksInteractor
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {
    private val favoriteTracksLiveData = MutableLiveData<FavoriteTracksState>()
    fun observeFavoriteTracksState(): LiveData<FavoriteTracksState> = favoriteTracksLiveData

    init {
        getFavoriteTracks()
    }

    private fun getFavoriteTracks() {
        viewModelScope.launch {
            favoriteTracksInteractor
                .getFavoriteTracks()
                .collect { tracks ->
                    if (tracks.isEmpty()) {
                        renderState(FavoriteTracksState.Empty)
                    } else {
                        renderState(FavoriteTracksState.Content(tracks))
                    }
                }
        }
    }

    private fun renderState(state: FavoriteTracksState) {
        favoriteTracksLiveData.postValue(state)
    }
}