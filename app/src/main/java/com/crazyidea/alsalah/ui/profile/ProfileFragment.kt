package com.crazyidea.alsalah.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.crazyidea.alsalah.databinding.FragmentProfileBinding
import com.crazyidea.alsalah.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val viewModel by viewModels<ProfileViewModel>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.blueColor.setOnClickListener {
            setSelectedSecondaryColor(binding.selectedBlue)
        }
        binding.orangeColor.setOnClickListener {
            setSelectedSecondaryColor(binding.selectedOrange)
        }
        binding.pinkColor.setOnClickListener {
            setSelectedSecondaryColor(binding.selectedPink)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setSelectedSecondaryColor(imageView: ImageView) {
        binding.selectedPink.visibility = View.GONE
        binding.selectedOrange.visibility = View.GONE
        binding.selectedBlue.visibility = View.GONE
        imageView.visibility = View.VISIBLE
    }
}