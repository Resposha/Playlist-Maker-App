package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.util.dpToPx
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class EditPlaylistFragment : NewPlaylistFragment() {
    override val viewModel: EditPlaylistViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newPlaylistToolbar.title = getString(R.string.edit)
        binding.newPlaylistButtonCreate.text = getString(R.string.save)

        val playlistId = arguments?.getInt(PLAYLIST_ID) ?: 0
        viewModel.showPlaylist(playlistId)

        viewModel.observePlaylistEdited().observe(viewLifecycleOwner) { playlist ->
            if (playlist == null) {
                findNavController().popBackStack()
            } else {
                binding.newPlaylistNameEditText.setText(playlist.name)
                binding.newPlaylistDescriptionEditText.setText(playlist.description)

                if (!playlist.artPath.isNullOrBlank()) {
                    binding.newPlaylistArtPlaceholder.isVisible = false

                    val file = File(playlist.artPath)
                    Glide.with(this)
                        .load(file)
                        .placeholder(R.drawable.placeholder_album_and_playlist_art)
                        .transform(
                            CenterCrop(),
                            RoundedCorners(requireContext().dpToPx(8f))
                        )
                        .into(binding.newPlaylistArt)
                }
            }
        }

        binding.newPlaylistToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.newPlaylistButtonCreate.setOnClickListener {
            val newName = binding.newPlaylistNameEditText.text.toString()
            val newDescription = binding.newPlaylistDescriptionEditText.text.toString()

            viewLifecycleOwner.lifecycleScope.launch {
                val newArtPath = if (artUri != null) {
                    saveImageToPrivateStorage(artUri!!)
                } else {
                    null
                }

                viewModel.editPlaylist(newName, newDescription, newArtPath)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })
    }

    companion object {
        private const val PLAYLIST_ID = "playlist_id"
    }
}