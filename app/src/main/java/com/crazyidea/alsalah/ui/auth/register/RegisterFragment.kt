package com.crazyidea.alsalah.ui.auth.register

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.databinding.FragmentRegisterBinding
import com.crazyidea.alsalah.utils.GlobalPreferences
import com.crazyidea.alsalah.utils.themeColor
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<RegisterViewModel>()

    @Inject
    lateinit var globalPreferences: GlobalPreferences
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disableButtons()
        val text = resources.getString(R.string.agreeToPrivacy)
        val word1 = resources.getString(R.string.privacyPolicy)
        val word2 = resources.getString(R.string.terms)
        binding.back.setOnClickListener { requireActivity().onBackPressed() }

        val ss = SpannableString(text)

        val clickableSpan1: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val i2 = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://hayah.crazyideaco.com/public/policy")
                )
                startActivity(i2)
            }
        }

        val clickableSpan2: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val i2 = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://hayah.crazyideaco.com/public/policy")
                )
                startActivity(i2)
            }
        }


        ss.setSpan(
            clickableSpan1,
            text.indexOf(word1),
            text.indexOf(word1) + word1.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        ss.setSpan(
            clickableSpan2,
            text.indexOf(word2),
            text.indexOf(word2) + word2.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.textPrivacy.setText(ss)
        binding.textPrivacy.setMovementMethod(LinkMovementMethod.getInstance())
        binding.privacyCheckBox.setOnCheckedChangeListener { compoundButton, b ->
            if (b)
                enableButtons()
            else
                disableButtons()

        }

        binding.googleCon.setOnClickListener {
            if (binding.privacyCheckBox.isChecked) {

            } else {
                Toast.makeText(
                    requireContext(),
                    "عليك ان توافق علي الشروط و الاحكام اولا",
                    Toast.LENGTH_SHORT
                )
            }

        }
        binding.facebookCon.setOnClickListener {
            if (binding.privacyCheckBox.isChecked) {

            } else {
                Toast.makeText(
                    requireContext(),
                    "عليك ان توافق علي الشروط و الاحكام اولا",
                    Toast.LENGTH_SHORT
                )
            }

        }
        binding.twitterCon.setOnClickListener {
            if (binding.privacyCheckBox.isChecked) {

            } else {
                Toast.makeText(
                    requireContext(),
                    "عليك ان توافق علي الشروط و الاحكام اولا",
                    Toast.LENGTH_SHORT
                )
            }

        }
    }

    private fun disableButtons() {
        greyOut(binding.facebookCon, false)
        greyOut(binding.googleCon, false)
        greyOut(binding.twitterCon, false)
        disablePics()
    }

    private fun disablePics() {
        binding.facebookImg.setImageDrawable(resources.getDrawable(R.drawable.ic_facebook_grey))
        binding.twitterImg.setImageDrawable(resources.getDrawable(R.drawable.ic_twitter_grey))
        binding.googleIMG.setImageDrawable(resources.getDrawable(R.drawable.ic_google_grey))

    }

    private fun enableButtons() {
        greyOut(binding.facebookCon, true)
        greyOut(binding.googleCon, true)
        greyOut(binding.twitterCon, true)
        enablePics()
    }

    private fun enablePics() {
        binding.facebookImg.setImageDrawable(resources.getDrawable(R.drawable.ic_facebook))
        binding.twitterImg.setImageDrawable(resources.getDrawable(R.drawable.ic_twitter))
        binding.googleIMG.setImageDrawable(resources.getDrawable(R.drawable.ic_google))

    }

    fun greyOut(materialCardView: MaterialCardView, value: Boolean) {
        if (value) {
            materialCardView.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.transparent
                )
            )
            materialCardView.strokeColor = requireContext().themeColor(android.R.attr.colorPrimary)
            materialCardView.isEnabled = true
        } else {
            materialCardView.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.light_grey
                )
            )
            materialCardView.isEnabled = false
            materialCardView.strokeColor = requireContext().themeColor(android.R.color.black)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}