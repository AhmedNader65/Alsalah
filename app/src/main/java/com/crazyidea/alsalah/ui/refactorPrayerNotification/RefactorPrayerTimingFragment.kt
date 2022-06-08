package com.crazyidea.alsalah.ui.refactorPrayerNotification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.crazyidea.alsalah.databinding.FragmentPrayerTimeNotificationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RefactorPrayerTimingFragment : Fragment(){

    private var _binding: FragmentPrayerTimeNotificationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<RefactorPrayerTimingNotificationViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPrayerTimeNotificationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.model = viewModel
        binding.lifecycleOwner = this
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.back.setOnClickListener { requireActivity().onBackPressed() }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}