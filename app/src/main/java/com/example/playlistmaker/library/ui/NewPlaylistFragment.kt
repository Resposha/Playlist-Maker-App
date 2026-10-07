package com.example.playlistmaker.library.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.util.dpToPx
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : Fragment() {
    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private var artUri: Uri? = null

    private val viewModel: NewPlaylistViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observePlaylistCreated().observe(viewLifecycleOwner) {
            Toast.makeText(
                requireContext(),
                it,
                Toast.LENGTH_LONG
            ).show()

            findNavController().popBackStack()
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    artUri = uri

                    binding.newPlaylistArtPlaceholder.visibility = View.GONE

                    Glide.with(this)
                        .load(uri)
                        .transform(CenterCrop(), RoundedCorners(requireContext().dpToPx(8f)))
                        .into(binding.newPlaylistArt)
                } else {
                    Log.d("PhotoPicker", "Ничего не выбрано")
                }
            }

        binding.newPlaylistArt.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.newPlaylistNameEditText.doOnTextChanged { s, _, _, _ ->
            binding.newPlaylistButtonCreate.isEnabled = !s.isNullOrBlank()
        }

        binding.newPlaylistButtonCreate.setOnClickListener {
            val name = binding.newPlaylistNameEditText.text.toString()
            val description = binding.newPlaylistDescriptionEditText.text.toString()

            val artPath = if (artUri != null) {
                saveImageToPrivateStorage(artUri!!)
            } else {
                null
            }

            viewModel.createPlaylist(name, description, artPath)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun saveImageToPrivateStorage(uri: Uri): String {
        val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist_arts")

        if (!filePath.exists()){
            filePath.mkdirs()
        }

        val uniqueFileName = "playlist_art_${System.currentTimeMillis()}.jpg"
        val file = File(filePath, uniqueFileName)

        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        inputStream?.close()
        outputStream.close()

        return file.absolutePath
    }
}