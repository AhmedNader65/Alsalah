package com.crazyidea.alsalah.ui.azanSetting

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
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
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.adapter.AzanSoundAdapter
import com.crazyidea.alsalah.data.model.Azan
import com.crazyidea.alsalah.databinding.FragmentAzanSettingBinding
import com.crazyidea.alsalah.utils.GlobalPreferences
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject


@AndroidEntryPoint
class AzanSettingFragment : Fragment(), AzanSoundAdapter.AzanListner {

    private var _binding: FragmentAzanSettingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<AzanSettingViewModel>()
    lateinit var mediaPlayer: MediaPlayer
    private val STORAGE_PERMISSION = 3214

    @Inject
    lateinit var globalPreferences: GlobalPreferences
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAzanSettingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.model = viewModel
        binding.lifecycleOwner = this
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.azanRV.adapter = AzanSoundAdapter(createAzans(), this)
        binding.chooseFromGallery.setOnClickListener { openAudioPicker(1) }
        binding.chooseFromGallery2.setOnClickListener { openAudioPicker(2) }


    }


    private fun openAudioPicker(whereFrom: Int) {
        if (!EasyPermissions.hasPermissions(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.permission_read_external_storage),
                STORAGE_PERMISSION,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        } else {
            pickAudio(whereFrom)

        }
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
        arrayList.add(Azan("السديسي", R.raw.azan))
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



}