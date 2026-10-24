package com.example.playlistmaker.sharing.domain.api

interface SharingInteractor {
    fun shareApp()
    fun sharePlaylist(message: String)
    fun openTerms()
    fun openSupport()
}