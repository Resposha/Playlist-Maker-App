package com.example.playlistmaker.util

import java.text.SimpleDateFormat
import java.util.Locale

fun Long.toFormattedMinutesSeconds(): String {
    return SimpleDateFormat("mm:ss", Locale.getDefault()).format(this)
}