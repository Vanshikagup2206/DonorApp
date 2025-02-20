package com.vanshika.donorapp.requests

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vanshika.donorapp.DonationDatabase
import com.vanshika.donorapp.R
import com.vanshika.donorapp.databinding.FragmentRequestsBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RequestsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RequestsFragment : Fragment(),RequestInterface {

    var binding: FragmentRequestsBinding? = null
    var recipientsDataClass = RecipientsDataClass()
    lateinit var linearLayoutManager: LinearLayoutManager
    var emergencyRequestList = arrayListOf<RecipientsDataClass>()
    lateinit var emergencyRequestAdapter: EmergencyRequestAdapter
    lateinit var donationDatabase: DonationDatabase


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRequestsBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        donationDatabase = DonationDatabase.getInstance(requireContext())
        emergencyRequestAdapter=EmergencyRequestAdapter(emergencyRequestList,this)
        linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding?.rvRecipients?.adapter=emergencyRequestAdapter

        binding?.rvRecipients?.layoutManager = linearLayoutManager
        getEmergencyRequestList()
        binding?.btnEmergencyRequest?.setOnClickListener {
            findNavController().navigate(R.id.emergencyRequestFragment)
        }

        binding?.btnAll?.setOnClickListener {
            getEmergencyRequestList()
        }
        binding?.btnBlood?.setOnClickListener {
            getBloodList()
        }
        binding?.btnOrgans?.setOnClickListener {
            getOrganList()
        }
        binding?.btnMedicine?.setOnClickListener {
            getMedicineList()
        }
        binding?.btnMoney?.setOnClickListener {
            getMoneyList()
        }
    }

    private fun getMoneyList() {
        val typeOfRequirement = "Money"
        emergencyRequestList.clear()
        emergencyRequestList.addAll(donationDatabase.DonationDao().getRecipientListAccToReq(typeOfRequirement))
        emergencyRequestAdapter.notifyDataSetChanged()
    }

    private fun getMedicineList() {
        val typeOfRequirement = "Medicine"
        emergencyRequestList.clear()
        emergencyRequestList.addAll(donationDatabase.DonationDao().getRecipientListAccToReq(typeOfRequirement))
        emergencyRequestAdapter.notifyDataSetChanged()
    }

    private fun getOrganList() {
        val typeOfRequirement = "Organ"
        emergencyRequestList.clear()
        emergencyRequestList.addAll(donationDatabase.DonationDao().getRecipientListAccToReq(typeOfRequirement))
        emergencyRequestAdapter.notifyDataSetChanged()
    }

    private fun getBloodList() {
        val typeOfRequirement = "Blood"
        emergencyRequestList.clear()
        emergencyRequestList.addAll(donationDatabase.DonationDao().getRecipientListAccToReq(typeOfRequirement))
        emergencyRequestAdapter.notifyDataSetChanged()
    }

    private fun getEmergencyRequestList() {
        emergencyRequestList.clear()
        emergencyRequestList.addAll(donationDatabase.DonationDao().getEmergencyRequestList())
        emergencyRequestAdapter.notifyDataSetChanged()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RequestsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) = RequestsFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
        }
    }

    override fun editRequest(position: Int) {
//        val bundle = Bundle().apply {
//            putInt("recipientId", position.recipientId)
//        }
//        findNavController().navigate(R.id.action_editRecipient, bundle)
    }

    override fun deleteRequest(position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this request?")
            .setPositiveButton("Delete") { _, _ ->
                donationDatabase.DonationDao().deleteEmergencyRequest(emergencyRequestList[position])
            }
            .setNegativeButton("Cancel", null)
            .show()
        emergencyRequestAdapter.notifyDataSetChanged()
    }

}