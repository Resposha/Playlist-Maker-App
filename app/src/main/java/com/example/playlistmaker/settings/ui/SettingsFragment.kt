package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeThemeSettings().observe(viewLifecycleOwner) { themeSettings ->
            binding.settingsSwitch.setOnCheckedChangeListener(null)
            binding.settingsSwitch.isChecked = themeSettings.isDarkThemeEnabled
            setSwitchListener()
        }

        binding.settingsShare.setOnClickListener { viewModel.shareApp() }
        binding.settingsSupport.setOnClickListener { viewModel.openSupport() }
        binding.settingsUserAgreement.setOnClickListener { viewModel.openTerms() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setSwitchListener() {
        binding.settingsSwitch.setOnCheckedChangeListener { _, checked ->
            viewModel.switchTheme(checked)
        }
    }
}