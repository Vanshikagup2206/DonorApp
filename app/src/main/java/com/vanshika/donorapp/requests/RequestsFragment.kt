package com.vanshika.donorapp.requests

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.vanshika.donorapp.DonationDatabase
import com.vanshika.donorapp.R
import com.vanshika.donorapp.databinding.FragmentRequestsBinding

class RequestsFragment : Fragment(), RequestInterface {

    private var binding: FragmentRequestsBinding? = null
    private lateinit var donationDatabase: DonationDatabase
    private lateinit var emergencyRequestAdapter: EmergencyRequestAdapter
    private var emergencyRequestList = arrayListOf<RecipientsDataClass>()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRequestsBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        donationDatabase = DonationDatabase.getInstance(requireContext())

        emergencyRequestAdapter = EmergencyRequestAdapter(emergencyRequestList, this)
        binding?.rvRecipients?.layoutManager = LinearLayoutManager(requireContext())
        binding?.rvRecipients?.adapter = emergencyRequestAdapter

        // Initial load: show my requests
        getMyRequests()

        binding?.btnEmergencyRequest?.setOnClickListener {
            findNavController().navigate(R.id.emergencyRequestFragment)
        }

        // Filter buttons
        binding?.btnAll?.setOnClickListener { getEmergencyRequestList() }
        binding?.btnBlood?.setOnClickListener { getFilteredList("Blood") }
        binding?.btnOrgans?.setOnClickListener { getFilteredList("Organ") }
        binding?.btnMedicine?.setOnClickListener { getFilteredList("Medicine") }
        binding?.btnMoney?.setOnClickListener { getFilteredList("Money") }

        // Show only requests created by current user
        getMyRequests()
    }

    private fun getMyRequests() {
        emergencyRequestList.clear()
        emergencyRequestList.addAll(
            donationDatabase.DonationDao().getEmergencyRequestList().filter {
                it.userId == currentUserId
            }
        )
        emergencyRequestAdapter.notifyDataSetChanged()
    }

    private fun getFilteredList(typeOfRequirement: String) {
        emergencyRequestList.clear()
        emergencyRequestList.addAll(
            donationDatabase.DonationDao().getRecipientListAccToReq(typeOfRequirement)
        )
        emergencyRequestAdapter.notifyDataSetChanged()
    }

    private fun getEmergencyRequestList() {
        emergencyRequestList.clear()
        emergencyRequestList.addAll(donationDatabase.DonationDao().getEmergencyRequestList())
        emergencyRequestAdapter.notifyDataSetChanged()
    }

    override fun editRequest(position: Int) {
        val convertToString = Gson().toJson(emergencyRequestList[position])
        val bundle = bundleOf(
            "id" to emergencyRequestList[position].recipientId,
            "name" to convertToString,
            "requirement" to convertToString,
            "location" to convertToString,
            "contactNo" to convertToString,
            "urgencyLevel" to convertToString
        )
        findNavController().navigate(R.id.updateEmergencyFragment, bundle)
    }

    override fun deleteRequest(position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this request?")
            .setPositiveButton("Delete") { _, _ ->
                donationDatabase.DonationDao().deleteEmergencyRequest(emergencyRequestList[position])
                getMyRequests() // Refresh only my requests after deletion
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
