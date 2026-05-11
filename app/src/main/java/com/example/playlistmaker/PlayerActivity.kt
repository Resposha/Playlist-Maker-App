package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    companion object {
        const val TRACK = "track"
    }

    private lateinit var track: Track
    private lateinit var playerToolbar: MaterialToolbar
    private lateinit var albumArtwork: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var addToPlaylist: ImageButton
    private lateinit var playAndPause: ImageButton
    private lateinit var addToFavourite: ImageButton
    private lateinit var playbackProgress: TextView
    private lateinit var trackTimeValue: TextView
    private lateinit var collectionName: TextView
    private lateinit var collectionNameValue: TextView
    private lateinit var releaseDate: TextView
    private lateinit var releaseDateValue: TextView
    private lateinit var primaryGenreNameValue: TextView
    private lateinit var countryValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        track = Gson().fromJson(intent.getStringExtra(TRACK), Track::class.java)

        playerToolbar = findViewById<MaterialToolbar>(R.id.player_toolbar)
        albumArtwork = findViewById<ImageView>(R.id.player_album_art)
        trackName = findViewById<TextView>(R.id.player_track_name)
        artistName = findViewById<TextView>(R.id.player_artist_name)
        addToPlaylist = findViewById<ImageButton>(R.id.player_button_add_to_playlist)
        playAndPause = findViewById<ImageButton>(R.id.player_button_play_and_pause)
        addToFavourite = findViewById<ImageButton>(R.id.player_button_add_to_favourite)
        playbackProgress = findViewById<TextView>(R.id.player_playback_progress)
        trackTimeValue = findViewById<TextView>(R.id.player_track_time_value)
        collectionName = findViewById<TextView>(R.id.player_collection_name)
        collectionNameValue = findViewById<TextView>(R.id.player_collection_name_value)
        releaseDate = findViewById<TextView>(R.id.player_release_date)
        releaseDateValue = findViewById<TextView>(R.id.player_release_date_value)
        primaryGenreNameValue = findViewById<TextView>(R.id.player_primary_genre_name_value)
        countryValue = findViewById<TextView>(R.id.player_country_value)

        playerToolbar.setNavigationOnClickListener {
            finish()
        }

        // временная заглушка
        playbackProgress.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toLong())

        setTrackDetails(track)
    }

    private fun setTrackDetails(track: Track) {
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder_album_art_player)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(8f,this)))
            .into(albumArtwork)

        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTimeValue.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toLong())
        primaryGenreNameValue.text = track.primaryGenreName
        countryValue.text = track.country

        if (track.collectionName != null) {
            collectionName.visibility = View.VISIBLE
            collectionNameValue.visibility = View.VISIBLE
            collectionNameValue.text = track.collectionName
        } else {
            collectionName.visibility = View.GONE
            collectionNameValue.visibility = View.GONE
        }

        if (track.releaseDate != null) {
            releaseDate.visibility = View.VISIBLE
            releaseDateValue.visibility = View.VISIBLE
            releaseDateValue.text = track.releaseDate.take(4)
        } else {
            releaseDate.visibility = View.GONE
            releaseDateValue.visibility = View.GONE
        }
    }

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}