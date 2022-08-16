package com.crazyidea.alsalah.ui.auth.register

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender
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
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.databinding.FragmentRegisterBinding

import com.crazyidea.alsalah.utils.themeColor
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var googleSignInClient: GoogleSignInClient
    private var _binding: FragmentRegisterBinding? = null

    private lateinit var callbackManager: CallbackManager
    private lateinit var buttonFacebookLogin: LoginButton


    // Control whether user declined One Tap UI
    private var userDeclinedOneTap = false

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<RegisterViewModel>()
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root
        auth = Firebase.auth
        callbackManager = CallbackManager.Factory.create()
        return root
    }

    private var oneTapLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    // This credential contains a googleIdToken which
                    // we can use to authenticate with FirebaseAuth
                    credential.googleIdToken?.let {
                        firebaseAuthWithGoogle(it)
                    }
                } catch (e: ApiException) {
                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> {
                            // The user closed the dialog
                            userDeclinedOneTap = true
                        }
                        CommonStatusCodes.NETWORK_ERROR -> {
                            // No Internet connection ?
                        }
                        else -> {
                            // Some other error
                        }
                    }
                }
            }
        }
    private var signInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Google sign in failed", e)
                    // ...
                }
            }
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disableButtons()
        val text = resources.getString(R.string.agreeToPrivacy)
        val word1 = resources.getString(R.string.privacyPolicy)
        val word2 = resources.getString(R.string.terms)
        binding.back.setOnClickListener { requireActivity().onBackPressed() }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_web_client_id))
            .requestEmail()
            .build()

        // Initialize lateinit vars
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        auth = Firebase.auth

        oneTapClient = Identity.getSignInClient(requireContext())
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build()
            )
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.google_web_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            .build()


        // Set up the Facebook login button
        FacebookSdk.sdkInitialize(requireContext())
//        AppEventsLogger.activateApp(requireActivity().application);
        buttonFacebookLogin = binding.facebookBTN
        buttonFacebookLogin.setFragment(this)
        buttonFacebookLogin.setReadPermissions("email", "public_profile", "user_friends")
        buttonFacebookLogin.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
            }
        })


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

        binding.textPrivacy.text = ss
        binding.textPrivacy.movementMethod = LinkMovementMethod.getInstance()
        binding.privacyCheckBox.setOnCheckedChangeListener { compoundButton, b ->
            if (b)
                enableButtons()
            else
                disableButtons()

        }

        binding.googleCon.setOnClickListener {
            if (binding.privacyCheckBox.isChecked) {
                signIn()
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
                buttonFacebookLogin.performClick()
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
                twitterFirebase()
            } else {
                Toast.makeText(
                    requireContext(),
                    "عليك ان توافق علي الشروط و الاحكام اولا",
                    Toast.LENGTH_SHORT
                )
            }
        }

    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }


    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            // If currentUser != null, let's go to the next screen
            viewModel.login(currentUser.uid, currentUser.displayName ?: "")
        } else {
            // If the user hasn't already declined to use One Tap sign-in
            if (!userDeclinedOneTap) {
                // Check if the user has saved credentials on our app
                // and display the One Tap UI
                oneTapClient.beginSignIn(signInRequest)
                    .addOnSuccessListener { result ->
                        // This listener will be triggered if the
                        // user does have saved credentials
                        try {
                            val intentSenderRequest =
                                IntentSenderRequest.Builder(result.pendingIntent.intentSender)
                                    .build()

                            oneTapLauncher.launch(intentSenderRequest)
                        } catch (e: IntentSender.SendIntentException) {
                            Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                        }

                    }.addOnFailureListener { e ->
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        Log.e(TAG, "No saved credentials: ${e.localizedMessage}")
                    }
            }
        }

        requireActivity().onBackPressed()
    }

    private fun firebaseAuthWithGoogle(googleIdToken: String) {
        val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Snackbar.make(
                        requireView(),
                        "Authentication Failed.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }

            }
    }


    private fun twitterFirebase() {
        val provider = OAuthProvider.newBuilder("twitter.com")
        provider.addCustomParameter("lang", "en")
        val pendingResultTask: Task<AuthResult>? = auth.pendingAuthResult
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                .addOnSuccessListener(
                    OnSuccessListener<AuthResult?> {
                        Log.e(TAG, "twitterFirebase: " + it.user?.email)
                        updateUI(it.user)


                        // User is signed in.
                        // IdP data available in
                        // authResult.getAdditionalUserInfo().getProfile().
                        // The OAuth access token can also be retrieved:
                        // authResult.getCredential().getAccessToken().
                        // The OAuth secret can be retrieved by calling:
                        // authResult.getCredential().getSecret().
                    })
                .addOnFailureListener(
                    OnFailureListener {
                        // Handle failure.
                        Log.e(TAG, "twitterFirebase: " + it.localizedMessage)
                    })
        } else {
            auth
                .startActivityForSignInWithProvider( /* activity= */requireActivity(),
                    provider.build()
                )
                .addOnSuccessListener(
                    OnSuccessListener<AuthResult?> {
                        updateUI(it.user)

                        // User is signed in.
                        // IdP data available in
                        // authResult.getAdditionalUserInfo().getProfile().
                        // The OAuth access token can also be retrieved:
                        // authResult.getCredential().getAccessToken().
                        // The OAuth secret can be retrieved by calling:
                        // authResult.getCredential().getSecret().
                    })
                .addOnFailureListener(
                    OnFailureListener {
                        // Handle failure.
                        Log.e(TAG, "twitterFirebase: " + it.localizedMessage)
                    })
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

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        var currentUser = auth.currentUser
//        updateUI(currentUser);
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result back to the Facebook SDK
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }

}