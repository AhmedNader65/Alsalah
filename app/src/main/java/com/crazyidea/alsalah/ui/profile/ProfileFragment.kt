package com.crazyidea.alsalah.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.crazyidea.alsalah.DataStoreCollector
import com.crazyidea.alsalah.MainActivity
import com.crazyidea.alsalah.data.model.PrimaryColor
import com.crazyidea.alsalah.databinding.FragmentProfileBinding
import com.crazyidea.alsalah.ui.setting.AppSettings
import com.crazyidea.alsalah.utils.getPrimaryColor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val viewModel by viewModels<ProfileViewModel>()
    private lateinit var auth: FirebaseAuth


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
        auth = Firebase.auth

        return root
    }


    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        currentUser?.let {
            binding.register.visibility = View.GONE
            binding.profileName.visibility = View.VISIBLE
            binding.profileName.text = it.displayName
            binding.profileEmail.text = it.email
            binding.profileEmail.visibility = View.VISIBLE
            binding.profileImgContainer.visibility = View.VISIBLE
            Picasso.get().load(it.photoUrl).into(binding.profileImg)
        } ?: run {
            binding.register.visibility = View.VISIBLE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkAvailablity(DataStoreCollector.loggedIn)

        setSelectedSecondaryColor(getPrimaryColor(DataStoreCollector.accentColor))
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            binding.nightModeSwitch.isChecked = true else false
        binding.blueColor.setOnClickListener {
            viewModel.update(AppSettings.ACCENT_COLOR, 2)
            setSelectedSecondaryColor(PrimaryColor.BLUE)
            (requireActivity() as MainActivity).restartActivity()
        }
        binding.orangeColor.setOnClickListener {
            viewModel.update(AppSettings.ACCENT_COLOR, 0)
            setSelectedSecondaryColor(PrimaryColor.ORANGE)
            (requireActivity() as MainActivity).restartActivity()
        }
        binding.pinkColor.setOnClickListener {
            viewModel.update(AppSettings.ACCENT_COLOR, 1)
            setSelectedSecondaryColor(PrimaryColor.PINK)
            (requireActivity() as MainActivity).restartActivity()
        }

        binding.nightModeSwitch.setOnCheckedChangeListener { view, isChecked ->
            if (isChecked)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        }

        binding.aboutTv.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToWhoAreWeFragment())
        }
        binding.helpTv.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToTechnicalSupportFragment())
        }
        binding.register.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToRegisterFragment())

        }
    }

    private fun checkAvailablity(logged: Boolean) {
        if (logged) {
            binding.register.visibility = View.GONE
            binding.profileImgContainer.visibility = View.VISIBLE
            binding.profileName.visibility = View.VISIBLE
            binding.profileEmail.visibility = View.VISIBLE
        } else {
            binding.register.visibility = View.VISIBLE
            binding.profileImgContainer.visibility = View.INVISIBLE
            binding.profileName.visibility = View.INVISIBLE
            binding.profileEmail.visibility = View.INVISIBLE

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setSelectedSecondaryColor(primaryColor: PrimaryColor) {
        when (primaryColor) {
            PrimaryColor.ORANGE -> {
                binding.selectedBlue.visibility = View.GONE
                binding.selectedPink.visibility = View.GONE
                binding.selectedOrange.visibility = View.VISIBLE
            }
            PrimaryColor.BLUE -> {
                binding.selectedBlue.visibility = View.VISIBLE
                binding.selectedPink.visibility = View.GONE
                binding.selectedOrange.visibility = View.GONE
            }
            PrimaryColor.PINK -> {
                binding.selectedBlue.visibility = View.GONE
                binding.selectedPink.visibility = View.VISIBLE
                binding.selectedOrange.visibility = View.GONE
            }
        }
    }
}