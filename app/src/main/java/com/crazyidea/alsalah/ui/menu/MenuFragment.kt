package com.crazyidea.alsalah.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.databinding.FragmentMenuBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val menuViewModel =
            ViewModelProvider(this).get(MenuViewModel::class.java)

        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setting.setOnClickListener {
            findNavController().navigate(MenuFragmentDirections.actionNavigationMenuToSettingFragment())
        }
        binding.compass.setOnClickListener {
            findNavController().navigate(MenuFragmentDirections.actionNavigationMenuToCompassFragment())
        }
        binding.fajrList.setOnClickListener {
            findNavController().navigate(MenuFragmentDirections.actionNavigationMenuToFajrListFragment())
        }
        binding.followUs.setOnClickListener {
            findNavController().navigate(MenuFragmentDirections.actionNavigationMenuToFollowUsFragment2())
        }
        binding.whoAreWe.setOnClickListener {
            findNavController().navigate(MenuFragmentDirections.actionNavigationMenuToWhoAreWeFragment())
        }
        binding.itSupport.setOnClickListener {
            findNavController().navigate(MenuFragmentDirections.actionNavigationMenuToTechnicalSupportFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}