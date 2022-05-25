package com.crazyidea.alsalah.ui.azkar.azkar_menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.databinding.FragmentAzkarDetailsBinding
import com.crazyidea.alsalah.databinding.FragmentAzkarListBinding
import com.crazyidea.alsalah.utils.withSimpleAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager


@AndroidEntryPoint
class AzkarMenuFragment : Fragment() {

    private var _binding: FragmentAzkarListBinding? = null
    private val args by navArgs<AzkarMenuFragmentArgs>()

    private val viewModel by viewModels<AzkarMenuModel>()


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAzkarListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.lifecycleOwner = this
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dividerItemDecoration = DividerItemDecoration(
            binding.azkarList.context,
            (binding.azkarList.layoutManager as LinearLayoutManager).orientation
        )
        binding.azkarList.addItemDecoration(dividerItemDecoration)
        binding.title.text = args.category
        setupObserver()
        GlobalScope.launch(Dispatchers.Main) {
            viewModel.getAzkar(args.category)
        }

    }

    private fun setupObserver() {
        viewModel.allAzkar.observe(viewLifecycleOwner) {
            binding.azkarList.withSimpleAdapter(it, R.layout.item_azkar) {
                val azkarText = itemView.findViewById<TextView>(R.id.azkar)
                val azkarCount = itemView.findViewById<TextView>(R.id.count)
                azkarText.text = it.content
                azkarCount.text = " عدد مرات التكرار : ${it.count}"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}