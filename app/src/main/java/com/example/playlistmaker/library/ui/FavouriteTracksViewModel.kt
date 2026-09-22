package com.example.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.api.FavouriteTracksInteractor
import kotlinx.coroutines.launch

class FavouriteTracksViewModel(
    private val favouriteTracksInteractor: FavouriteTracksInteractor
) : ViewModel() {
    private val favouriteTracksLiveData = MutableLiveData<FavouriteTracksState>()
    fun observeFavouriteTracksState(): LiveData<FavouriteTracksState> = favouriteTracksLiveData

    init {
        getFavouriteTracks()
    }

    private fun getFavouriteTracks() {
        viewModelScope.launch {
            favouriteTracksInteractor
                .getFavouriteTracks()
                .collect { tracks ->
                    if (tracks.isEmpty()) {
                        renderState(FavouriteTracksState.Empty)
                    } else {
                        renderState(FavouriteTracksState.Content(tracks))
                    }
                }
        }
    }

    private fun renderState(state: FavouriteTracksState) {
        favouriteTracksLiveData.postValue(state)
    }
}