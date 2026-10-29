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
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.util.dpToPx
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

open class NewPlaylistFragment : Fragment() {
    private var _binding: FragmentNewPlaylistBinding? = null
    protected val binding get() = _binding!!

    protected open val viewModel: NewPlaylistViewModel by viewModel()

    protected var artUri: Uri? = null

    private lateinit var confirmDialog: MaterialAlertDialogBuilder

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

        viewModel.observePlaylistCreated().observe(viewLifecycleOwner) { name ->
            val message = getString(R.string.playlist_created, name)
            Toast.makeText(
                requireContext(),
                message,
                Toast.LENGTH_LONG
            ).show()

            findNavController().popBackStack()
        }

        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.new_playlist_dialog_title)
            .setMessage(R.string.new_playlist_dialog_message)
            .setNeutralButton(R.string.dialog_neutral_button_cancel) { _, _ ->
                // empty
            }.setPositiveButton(R.string.new_playlist_dialog_positive_button) { _, _ ->
                findNavController().popBackStack()
            }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    artUri = uri

                    binding.newPlaylistArtPlaceholder.isVisible = false

                    Glide.with(this)
                        .load(uri)
                        .placeholder(R.drawable.placeholder_album_and_playlist_art)
                        .transform(
                            CenterCrop(),
                            RoundedCorners(requireContext().dpToPx(8f))
                        )
                        .into(binding.newPlaylistArt)
                } else {
                    Log.d(TAG, "PhotoPicker: ничего не выбрано")
                }
            }

        binding.newPlaylistToolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
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

            viewLifecycleOwner.lifecycleScope.launch {
                val artPath = if (artUri != null) {
                    saveImageToPrivateStorage(artUri!!)
                } else {
                    null
                }

                viewModel.createPlaylist(name, description, artPath)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (hasUnsavedChanges()) {
                    confirmDialog.show()
                } else {
                    findNavController().popBackStack()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun hasUnsavedChanges(): Boolean {
        val isArtSelected = artUri != null
        val isNameFilled = !binding.newPlaylistNameEditText.text.isNullOrBlank()
        val isDescriptionFilled = !binding.newPlaylistDescriptionEditText.text.isNullOrBlank()

        return isArtSelected || isNameFilled || isDescriptionFilled
    }

    protected suspend fun saveImageToPrivateStorage(uri: Uri): String? = withContext(Dispatchers.IO) {
        val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist_arts")

        if (!filePath.exists()){
            filePath.mkdirs()
        }

        val uniqueFileName = "playlist_art_${System.currentTimeMillis()}.jpg"
        val file = File(filePath, uniqueFileName)

        try {
            val bitmap = requireContext().contentResolver.openInputStream(uri).use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
            if (bitmap != null) {
                FileOutputStream(file).use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
                }
                file.absolutePath
            } else {
                Log.e(TAG, "Сохранение обложки: BitmapFactory вернул null")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Сохранение обложки: ошибка при записи файла", e)
            null
        }
    }

    companion object {
        private const val TAG = "NewPlaylistFragment"
    }
}