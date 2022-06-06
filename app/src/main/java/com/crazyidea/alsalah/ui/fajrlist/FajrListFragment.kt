package com.crazyidea.alsalah.ui.fajrlist

import android.Manifest.permission.*
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.crazyidea.alsalah.MainActivity
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.adapter.FajrAdapter
import com.crazyidea.alsalah.data.room.entity.fajr.Fajr
import com.crazyidea.alsalah.databinding.FragmentFajrListBinding
import com.crazyidea.alsalah.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*


@AndroidEntryPoint
class FajrListFragment : Fragment(), PermissionListener {
    private var contactsJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + contactsJob)

    private var _binding: FragmentFajrListBinding? = null
    private lateinit var permissionHelper: PermissionHelper

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<FajrListViewModel>()
    lateinit var adapter: FajrAdapter
    lateinit var fajrList: MutableList<Fajr>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        permissionHelper = PermissionHelper(this, this)
        _binding = FragmentFajrListBinding.inflate(inflater, container, false)
        fajrList = mutableListOf()
        adapter = FajrAdapter(fajrList)
        observeData()
        return binding.root
    }

    private fun observeData() {
        viewModel.fajrList.observe(viewLifecycleOwner) {

            binding.emptyList.isVisible = it.isEmpty()
            binding.fajrListLayout.isVisible = it.isNotEmpty()
            fajrList = it.toMutableList()
            adapter.setData(fajrList)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getList()
        binding.fajrList.adapter = adapter
        binding.addContacts.setOnClickListener {
            permissionHelper.checkForMultiplePermissions(
                arrayOf(READ_CONTACTS, CALL_PHONE)
            )
        }
        binding.addContacts2.setOnClickListener {
            permissionHelper.checkForPermissions(
                READ_CONTACTS
            )

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val overlayEnabled = Settings.canDrawOverlays(requireContext())
            Log.e("showTaskDetailPopup==", "overlayEnabled$overlayEnabled")

            if (!overlayEnabled) {
                showDOARationaleInfo()
            }
        }
        binding.back.setOnClickListener { requireActivity().onBackPressed() }
    }

    private fun requestOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + requireActivity().packageName)
        )
        resultLauncher.launch(intent)
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//        }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun shouldShowRationaleInfo() {
        val dialogBuilder = AlertDialog.Builder(requireContext())

        // set message of alert dialog
        dialogBuilder.setMessage(getString(R.string.read_contacts))
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton(getString(R.string.موافق)) { dialog, _ ->
                dialog.cancel()
                permissionHelper.launchPermissionDialogForMultiplePermissions(
                    arrayOf(
                        READ_EXTERNAL_STORAGE,
                        READ_CONTACTS,
                        CALL_PHONE,
                    )
                )
            }
            // negative button text and action
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle(getString(R.string.read_contacts_permission))
        // show alert dialog
        alert.show()
    }

    fun showDOARationaleInfo() {
        val dialogBuilder = AlertDialog.Builder(requireContext())

        // set message of alert dialog
        dialogBuilder.setMessage(getString(R.string.display_over_apps))
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton(getString(R.string.موافق)) { dialog, _ ->
                dialog.cancel()
                requestOverlayPermission()
            }
            // negative button text and action
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle(getString(R.string.read_contacts_permission))
        // show alert dialog
        alert.show()
    }

    override fun isPermissionGranted(isGranted: Boolean) {
        val list = mutableListOf<String>()
        for (item in fajrList) {
            list.add(item.number)
        }
        (requireActivity() as MainActivity).showLoading(true)
        uiScope.launch {
            withContext(Dispatchers.IO) {
                val contacts: List<ContactData> = requireContext().retrieveAllContacts()
                withContext(Dispatchers.Main) {
                    (requireActivity() as MainActivity).showLoading(false)
                    displayContacts(contacts)
                }
            }
        }
        binding.fajrListLayout.visibility = GONE
        binding.emptyList.visibility = GONE
        binding.addContactsLayout.visibility = VISIBLE



        binding.done.setOnClickListener {
            viewModel.saveList(fajrList)
            binding.addContactsLayout.visibility = GONE
            binding.fajrListLayout.visibility = VISIBLE
            binding.emptyList.visibility = GONE
            adapter.setData(fajrList)
        }
    }

    private fun displayContacts(contacts: List<ContactData>) {
        binding.contactsList.withSimpleAdapter(contacts, R.layout.item_checkbox) {
            (itemView as CheckBox).text = it.name
            val index = fajrList.indexOfFirst { fajr -> fajr.number == it.phoneNumber.first() }
            Log.e("check is ${it.name}", " index $index")
            (itemView as CheckBox).isChecked = index != -1
            (itemView as CheckBox).setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked)
                    fajrList.add(Fajr(id = null, name = it.name, number = it.phoneNumber.first()))
                else {
                    if (index != -1)
                        fajrList.removeAt(index)
                }
            }
        }
    }


    override fun onDestroy() {
        (requireActivity() as MainActivity).showLoading(false)
        contactsJob.cancel()
        super.onDestroy()
    }
}