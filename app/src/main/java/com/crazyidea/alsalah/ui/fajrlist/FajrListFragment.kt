package com.crazyidea.alsalah.ui.fajrlist

import android.Manifest
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.crazyidea.alsalah.R
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        permissionHelper = PermissionHelper(this, this)
        _binding = FragmentFajrListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addContacts.setOnClickListener {
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
        val contacts: List<ContactData> = requireContext().retrieveAllContacts()
        binding.emptyList.visibility = GONE
        binding.addContactsLayout.visibility = VISIBLE
        binding.contactsList.withSimpleAdapter(contacts, R.layout.item_checkbox) {
            (itemView as CheckBox).text = it.name
        }
    }
}