package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.debounce
import com.example.playlistmaker.util.dpToPx
import com.example.playlistmaker.util.getParcelableCompat
import com.example.playlistmaker.util.toFormattedMinutesSeconds
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue

class PlayerFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayerViewModel by viewModel {
        val track = arguments?.getParcelableCompat(TRACK, Track::class.java)
        parametersOf(
            track ?: Track(
                EMPTY_STRING,
                EMPTY_STRING,
                EMPTY_STRING,
                0L,
                EMPTY_STRING,
                null,
                null,
                EMPTY_STRING,
                EMPTY_STRING,
                null
            )
        )
    }

    private lateinit var playlistsAdapter: SmallPlaylistAdapter
    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val track = arguments?.getParcelableCompat(TRACK, Track::class.java)
        if (track == null || track.previewUrl.isNullOrEmpty()) {
            findNavController().popBackStack()
            return
        }

        setTrackDetails(track)

        onPlaylistClickDebounce = debounce<Playlist>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { playlist ->
            viewModel.addTrackToPlaylist(playlist)
        }

        playlistsAdapter = SmallPlaylistAdapter(emptyList(), onPlaylistClickDebounce)
        binding.playerRecyclerviewPlaylists.adapter = playlistsAdapter

        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observePlaylists().observe(viewLifecycleOwner) {
            playlistsAdapter.updatePlaylists(it)
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.playerBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.playerOverlay.isVisible = false
                    }
                    else -> {
                        binding.playerOverlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.playerOverlay.alpha = (slideOffset + 1f) / 2f
            }
        })

        viewModel.observePlaylistAddingStatus().observe(viewLifecycleOwner) { (playlistName, isAdded) ->
            if (isAdded) {
                Toast.makeText(
                    requireContext(),
                    "Добавлено в плейлист $playlistName",
                    Toast.LENGTH_SHORT
                ).show()

                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            } else {
                Toast.makeText(
                    requireContext(),
                    "Трек уже добавлен в плейлист $playlistName",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.playerToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.playerButtonAddToPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.playerOverlay.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.playerButtonPlayAndPause.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        binding.playerButtonAddToFavoriteOrRemove.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        binding.playerButtonNewPlaylist.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.playerFragment) {
                findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updatePlaylists()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun render(state: PlayerState) {
        binding.playerPlaybackProgress.text = state.progress

        when (state) {
            is PlayerState.Default -> {
                with(binding.playerButtonPlayAndPause) {
                    isEnabled = state.isPlayButtonEnabled
                    setImageResource(R.drawable.button_play)
                }
                with(binding.playerButtonAddToFavoriteOrRemove) {
                    isEnabled = false
                    setImageResource(R.drawable.button_add_to_favorite)
                }
            }
            is PlayerState.Prepared -> {
                with(binding.playerButtonPlayAndPause) {
                    isEnabled = state.isPlayButtonEnabled
                    setImageResource(R.drawable.button_play)
                }
                binding.playerButtonAddToFavoriteOrRemove.isEnabled = true
                setFavoriteIcon(state.isFavorite)
            }
            is PlayerState.Playing -> {
                with(binding.playerButtonPlayAndPause) {
                    isEnabled = state.isPlayButtonEnabled
                    setImageResource(R.drawable.button_pause)
                }
                binding.playerButtonAddToFavoriteOrRemove.isEnabled = true
                setFavoriteIcon(state.isFavorite)
            }
            is PlayerState.Paused -> {
                with(binding.playerButtonPlayAndPause) {
                    isEnabled = state.isPlayButtonEnabled
                    setImageResource(R.drawable.button_play)
                }
                binding.playerButtonAddToFavoriteOrRemove.isEnabled = true
                setFavoriteIcon(state.isFavorite)
            }
        }
    }

    private fun setTrackDetails(track: Track) {
        Glide.with(requireContext())
            .load(track.getCoverArtworkUrl512())
            .placeholder(R.drawable.placeholder_album_and_playlist_art)
            .transform(
                CenterCrop(),
                RoundedCorners(requireContext().dpToPx(8f))
            )
            .into(binding.playerAlbumArt)

        binding.playerTrackName.text = track.trackName
        binding.playerArtistName.text = track.artistName
        binding.playerPlaybackProgress.text = 0L.toFormattedMinutesSeconds()
        binding.playerTrackTimeValue.text = track.trackTimeMillis.toFormattedMinutesSeconds()
        binding.playerPrimaryGenreNameValue.text = track.primaryGenreName
        binding.playerCountryValue.text = track.country

        if (track.collectionName != null) {
            binding.playerCollectionName.isVisible = true
            with(binding.playerCollectionNameValue) {
                isVisible = true
                text = track.collectionName
            }
        } else {
            binding.playerCollectionName.isVisible = false
            binding.playerCollectionNameValue.isVisible = false
        }

        if (track.releaseDate != null) {
            binding.playerReleaseDate.isVisible = true
            with(binding.playerReleaseDateValue) {
                isVisible = true
                text = track.releaseDate.take(4)

            }
        } else {
            binding.playerReleaseDate.isVisible = false
            binding.playerReleaseDateValue.isVisible = false
        }
    }

    private fun setFavoriteIcon(isFavorite: Boolean) {
        if (isFavorite) {
            binding.playerButtonAddToFavoriteOrRemove.setImageResource(R.drawable.button_added_to_favorite)
        } else {
            binding.playerButtonAddToFavoriteOrRemove.setImageResource(R.drawable.button_add_to_favorite)
        }
    }

    companion object {
        private const val TRACK = "track"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val EMPTY_STRING = ""
    }
}