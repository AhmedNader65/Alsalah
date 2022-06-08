package com.crazyidea.alsalah.ui.auth.register

import android.content.ContentValues.TAG
import android.content.Intent
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
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<RegisterViewModel>()
//    private lateinit var auth: FirebaseAuth
//    private lateinit var oneTapClient: SignInClient
//    private lateinit var signInRequest: BeginSignInRequest
    @Inject
    lateinit var globalPreferences: GlobalPreferences
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root
//        auth = Firebase.auth
//        oneTapClient = Identity.getSignInClient(requireActivity())
        return root
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disableButtons()
        val text = resources.getString(R.string.agreeToPrivacy)
        val word1 = resources.getString(R.string.privacyPolicy)
        val word2 = resources.getString(R.string.terms)
        binding.back.setOnClickListener { requireActivity().onBackPressed() }

//        setUpBuilder()


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

//    private fun setUpBuilder() {
//
//        signInRequest = BeginSignInRequest.builder()
//            .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
//                .setSupported(true)
//                .build())
//            .setGoogleIdTokenRequestOptions(
//                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                    .setSupported(true)
//                    // Your server's client ID, not your Android client ID.
//                    .setServerClientId(getString(R.string.your_web_client_id))
//                    // Only show accounts previously used to sign in.
//                    .setFilterByAuthorizedAccounts(true)
//                    .build())
//            // Automatically sign in when exactly one credential is retrieved.
//            .setAutoSelectEnabled(true)
//            .build()
//
//
//
//        val googleCredential = oneTapClient.getSignInCredentialFromIntent(data)
//        val idToken = googleCredential.googleIdToken
//        when {
//            idToken != null -> {
//                // Got an ID token from Google. Use it to authenticate
//                // with Firebase.
//                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
//                auth.signInWithCredential(firebaseCredential)
//                    .addOnCompleteListener(requireActivity()) { task ->
//                        if (task.isSuccessful) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success")
//                            val user = auth.currentUser
//                            updateUI(user)
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.exception)
//                            updateUI(null)
//                        }
//                    }
//            }
//            else -> {
//                // Shouldn't happen.
//                Log.d(TAG, "No ID token!")
//            }
//        }
//
//    }

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