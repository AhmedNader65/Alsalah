package com.crazyidea.alsalah.ui.quran

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.crazyidea.alsalah.databinding.FragmentQuranBinding
import com.crazyidea.alsalah.utils.GlobalPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class QuranFragment : Fragment() {

    private var _binding: FragmentQuranBinding? = null
    private val viewModel by viewModels<QuranViewModel>()

    @Inject
    lateinit var globalPreferences: GlobalPreferences

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentQuranBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getQuran()
        viewModel.pageContent.observe(viewLifecycleOwner) {
            var text = ""
            it.forEach {
                text += "${it.text}  \uFD3F${it.number}\uFD3E"
            }
            binding.ayah.text = text
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}