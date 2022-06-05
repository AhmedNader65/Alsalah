package com.crazyidea.alsalah.ui.fajrlist

import android.Manifest
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.adapter.FajrAdapter
import com.crazyidea.alsalah.data.room.entity.fajr.Fajr
import com.crazyidea.alsalah.databinding.FragmentFajrListBinding
import com.crazyidea.alsalah.utils.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FajrListFragment : Fragment(), PermissionListener {

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
            permissionHelper.checkForPermissions(
                Manifest.permission.READ_CONTACTS

            )

        }
        permissionHelper.checkForPermissions(
            Manifest.permission.CALL_PHONE

        )
        binding.addContacts2.setOnClickListener {
            permissionHelper.checkForPermissions(
                Manifest.permission.READ_CONTACTS
            )

        }
        binding.back.setOnClickListener { requireActivity().onBackPressed() }
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
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.CALL_PHONE,
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

    override fun isPermissionGranted(isGranted: Boolean) {
        val list = mutableListOf<String>()
        for (item in fajrList) {
            list.add(item.number)
        }

        val contacts: List<ContactData> = requireContext().retrieveAllContacts()
        binding.fajrListLayout.visibility = GONE
        binding.emptyList.visibility = GONE
        binding.addContactsLayout.visibility = VISIBLE

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

        binding.done.setOnClickListener {
            viewModel.saveList(fajrList)
            binding.addContactsLayout.visibility = GONE
            binding.fajrListLayout.visibility = VISIBLE
            binding.emptyList.visibility = GONE
            adapter.setData(fajrList)
        }
    }


}