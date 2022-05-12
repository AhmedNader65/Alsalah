package com.crazyidea.alsalah.ui.azkar.azkar_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.crazyidea.alsalah.databinding.FragmentAzkarDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext

@AndroidEntryPoint
class AzkarDetailsFragment : Fragment() {

    private var _binding: FragmentAzkarDetailsBinding? = null
    private val args by navArgs<AzkarDetailsFragmentArgs>()

    private val viewModel by viewModels<AzkarDetailsViewModel>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAzkarDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.bottomTools.settings.setOnClickListener {
            findNavController().navigate(AzkarDetailsFragmentDirections.actionNavigationAzkarDetailsToAzkarSettingsFragment())
        }
        binding.model = viewModel
        binding.lifecycleOwner = this
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.title.text = args.category
        GlobalScope.launch(Dispatchers.Main) {
            viewModel.getAzkar(args.category)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}