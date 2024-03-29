package com.crazyidea.alsalah.ui.quran

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.crazyidea.alsalah.DataStoreCollector
import com.crazyidea.alsalah.MainActivity
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.adapter.QuranPageAdapter
import com.crazyidea.alsalah.databinding.FragmentQuranBinding
import com.crazyidea.alsalah.ui.setting.AppSettings
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class QuranFragment : Fragment() {

    private lateinit var navController: NavController
    private val args by navArgs<QuranFragmentArgs>()
    private lateinit var alert: AlertDialog
    private var _binding: FragmentQuranBinding? = null
    private val viewModel by viewModels<SharedQuranViewModel>({ requireActivity() })


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
        navController =
            Navigation.findNavController(requireActivity(), R.id.quran_categories_host_fragment)

        viewModel.downloadQuran()
        showDialog()
        viewModel.downloaded.observe(viewLifecycleOwner) {
            if (it == true) {
                binding.viewPager.adapter = QuranPageAdapter(this)
                alert.dismiss()
                if (args.type != "quran")
                    args.khatma?.let {
                        viewModel.setCurrentPage(it.read.plus(1))
                    }
                else
                    if (DataStoreCollector.lastReading != 0) {
                        viewModel.setCurrentPage(DataStoreCollector.lastReading)
                    }
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
        binding.juzs.setOnClickListener {
            navController.navigate(R.id.juzsFragment)
        }
        binding.fehres.setOnClickListener {
            navController.navigate(R.id.fehresFragment)
        }

        binding.bookmarks.setOnClickListener {
            navController.navigate(R.id.bookmarksFragment)
        }

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {
            }

            override fun onDrawerClosed(drawerView: View) {
                (requireActivity() as MainActivity).hideKeyboard()
            }

            override fun onDrawerStateChanged(newState: Int) {
            }
        })
    }

    private var callback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            if (args.type != "quran")
                args.khatma?.let {
                    viewModel.khatma.value = it
                    viewModel.updateKhatma(it.id!!.toLong(), position + 1)
                }
            viewModel.update(AppSettings.LAST_READING, position + 1)
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

    override fun onStart() {
        super.onStart()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onStop() {
        super.onStop()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

}