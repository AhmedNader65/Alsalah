package com.crazyidea.alsalah.ui.quran

import android.Manifest
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.adapter.DataItem
import com.crazyidea.alsalah.adapter.QuranPageAdapter
import com.crazyidea.alsalah.databinding.FragmentQuranBinding
import com.crazyidea.alsalah.utils.GlobalPreferences
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class QuranFragment : Fragment() {

    private lateinit var alert: AlertDialog
    private var _binding: FragmentQuranBinding? = null
    private val viewModel by viewModels<SharedQuranViewModel>({ requireActivity() })

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
        viewModel.downloadQuran()
        showDialog()
        viewModel.downloaded.observe(viewLifecycleOwner) {
            if (it == true) {
                binding.viewPager.adapter = QuranPageAdapter(this)
                alert.dismiss()
            }
        }
        viewModel.openDrawer.observe(viewLifecycleOwner) {
            if (it) {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            } else {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }

        viewModel.currentPage.observe(viewLifecycleOwner) { page ->
            binding.viewPager.setCurrentItem(page - 1, false)
        }
        binding.viewPager.registerOnPageChangeCallback(callback)
    }

    private var callback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            Timber.e("page selected $position")
            viewModel.setSideDrawerPage(position + 1)
        }
    }

    private fun showDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())

        // set message of alert dialog
        dialogBuilder.setView(layoutInflater.inflate(R.layout.dialog_downloading, null))
            // if the dialog is cancelable
            .setCancelable(false)
        // positive button text and action

        // create dialog box
        alert = dialogBuilder.create()
        // show alert dialog
        alert.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.viewPager.unregisterOnPageChangeCallback(callback)
        _binding = null
    }

}