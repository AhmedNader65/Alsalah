package com.crazyidea.alsalah.ui.azanSetting

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Rect
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.adapter.AzanSoundAdapter
import com.crazyidea.alsalah.data.model.Azan
import com.crazyidea.alsalah.databinding.FragmentAzanSettingBinding
import com.crazyidea.alsalah.utils.GlobalPreferences
import com.crazyidea.alsalah.utils.PermissionHelper
import com.crazyidea.alsalah.utils.PermissionListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class AzanSettingFragment : Fragment(), AzanSoundAdapter.AzanListner, PermissionListener {

    private var _binding: FragmentAzanSettingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<AzanSettingViewModel>()
    lateinit var mediaPlayer: MediaPlayer
    private var whereFrom = 0
    private lateinit var permissionHelper: PermissionHelper

    @Inject
    lateinit var globalPreferences: GlobalPreferences
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        permissionHelper = PermissionHelper(this, this)
        _binding = FragmentAzanSettingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.model = viewModel
        binding.lifecycleOwner = this
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.azanRV.adapter = AzanSoundAdapter(createAzans(), this)
        binding.chooseFromGallery.setOnClickListener {
            whereFrom = 1
            openAudioPicker()
        }
        binding.chooseFromGallery2.setOnClickListener {
            whereFrom = 2
            openAudioPicker()
        }
        binding.back.setOnClickListener { requireActivity().onBackPressed() }


    }


    private fun openAudioPicker() {
        permissionHelper.checkForMultiplePermissions(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
    }

    fun pickAudio(whereFrom: Int) {
        val videoIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(Intent.createChooser(videoIntent, "Select Audio"), whereFrom)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === 1) {
            if (resultCode === RESULT_OK) {

                //the selected audio.
                val uri: Uri? = data?.data
                if (uri != null) {

                    globalPreferences.saveAzan(uri.toString())
                }
            }
        } else {
            if (resultCode === RESULT_OK) {

                //the selected audio.
                val uri: Uri? = data?.data
                if (uri != null) {
                    Log.e("TAG", "onActivityResult: " + uri.toString())
                    globalPreferences.saveAzan(uri.toString())
                }

            }
        }
    }

    private fun createAzans(): ArrayList<Azan> {
        var arrayList = ArrayList<Azan>()
        arrayList.add(Azan("الاذان المكي", R.raw.azan))
        arrayList.add(Azan("الاذان المدني", R.raw.azan))
        arrayList.add(Azan("اذان الاقصى", R.raw.azan))
        arrayList.add(Azan("الاذان المكي تكبيرتان", R.raw.azan))
        arrayList.add(Azan("الاذان المدني تكبيرتان", R.raw.azan))
        arrayList.add(Azan("اذان الاقصى تكبيرتان", R.raw.azan))
        arrayList.add(Azan("محمد صديق المنشاوي", R.raw.azan))
        arrayList.add(Azan("عبدالباسط عبدالصمد", R.raw.azan))
        arrayList.add(Azan("السديسي", R.raw.azan, false))
        return arrayList

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (this::mediaPlayer.isInitialized)
            mediaPlayer.stop()
    }

    override fun onAzanPicked(azan: Azan) {
        Log.e("TAG", "onAzanPicked: " + azan.shortcut)
        globalPreferences.saveAzan(azan.shortcut.toString())

    }

    override fun onPlayClicked(azan: Azan) {


        if (this::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer = MediaPlayer.create(context, azan.shortcut)
        if (azan.isPlaying)
            mediaPlayer.start()
        else
            mediaPlayer.stop()

    }

    override fun shouldShowRationaleInfo() {
        val dialogBuilder = AlertDialog.Builder(requireContext())

        // set message of alert dialog
        dialogBuilder.setMessage("External Storage permission is Required")
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton("OK") { dialog, _ ->
                dialog.cancel()
                permissionHelper.launchPermissionDialogForMultiplePermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                    )
                )
            }
            // negative button text and action
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("Storage Permission")
        // show alert dialog
        alert.show()
    }

    override fun isPermissionGranted(isGranted: Boolean) {
        if (isGranted) {
            pickAudio(whereFrom)
        }
    }


}