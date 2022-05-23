package com.crazyidea.alsalah.ui.azanSetting

import android.media.MediaPlayer
import android.os.Bundle
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
import java.util.ArrayList
import javax.inject.Inject

@AndroidEntryPoint
class AzanSettingFragment : Fragment(), AzanSoundAdapter.AzanListner {

    private var _binding: FragmentAzanSettingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<AzanSettingViewModel>()
    lateinit var mediaPlayer: MediaPlayer

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
        globalPreferences.saveAzan(azan.Name)

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