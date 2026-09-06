package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.dpToPx
import com.example.playlistmaker.util.getParcelableCompat
import com.example.playlistmaker.util.toFormattedMinutesSeconds
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue

class PlayerFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(arguments?.getParcelableCompat(TRACK, Track::class.java)?.previewUrl ?: "")
    }

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

        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.playerButtonPlayAndPause.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }
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
                binding.playerButtonPlayAndPause.apply {
                    isEnabled = state.isPlayButtonEnabled
                    setImageResource(R.drawable.button_play)
                }
            }
            is PlayerState.Prepared -> {
                binding.playerButtonPlayAndPause.apply {
                    isEnabled = state.isPlayButtonEnabled
                    setImageResource(R.drawable.button_play)
                }
            }
            is PlayerState.Playing -> {
                binding.playerButtonPlayAndPause.apply {
                    isEnabled = state.isPlayButtonEnabled
                    setImageResource(R.drawable.button_pause)
                }
            }
            is PlayerState.Paused -> {
                binding.playerButtonPlayAndPause.apply {
                    isEnabled = state.isPlayButtonEnabled
                    setImageResource(R.drawable.button_play)
                }
            }
        }
    }

    private fun setTrackDetails(track: Track) {
        Glide.with(requireContext())
            .load(track.getCoverArtworkUrl512())
            .placeholder(R.drawable.placeholder_album_art_player)
            .centerCrop()
            .transform(RoundedCorners(requireContext().dpToPx(8f)))
            .into(binding.playerAlbumArt)

        binding.playerTrackName.text = track.trackName
        binding.playerArtistName.text = track.artistName
        binding.playerPlaybackProgress.text = 0L.toFormattedMinutesSeconds()
        binding.playerTrackTimeValue.text = track.trackTimeMillis.toFormattedMinutesSeconds()
        binding.playerPrimaryGenreNameValue.text = track.primaryGenreName
        binding.playerCountryValue.text = track.country

        if (track.collectionName != null) {
            binding.playerCollectionName.visibility = View.VISIBLE
            binding.playerCollectionNameValue.apply {
                visibility = View.VISIBLE
                text = track.collectionName
            }
        } else {
            binding.playerCollectionName.visibility = View.GONE
            binding.playerCollectionNameValue.visibility = View.GONE
        }

        if (track.releaseDate != null) {
            binding.playerReleaseDate.visibility = View.VISIBLE
            binding.playerReleaseDateValue.apply {
                visibility = View.VISIBLE
                text = track.releaseDate.take(4)

            }
        } else {
            binding.playerReleaseDate.visibility = View.GONE
            binding.playerReleaseDateValue.visibility = View.GONE
        }
    }

    companion object {
        private const val TRACK = "track"
    }
}