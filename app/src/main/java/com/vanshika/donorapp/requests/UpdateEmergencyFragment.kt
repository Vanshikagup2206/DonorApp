package com.vanshika.donorapp.requests

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vanshika.donorapp.DonationDatabase
import com.vanshika.donorapp.R
import com.vanshika.donorapp.databinding.FragmentUpdateEmergencyBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UpdateEmergencyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UpdateEmergencyFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var binding: FragmentUpdateEmergencyBinding? = null
    lateinit var donationDatabase: DonationDatabase
    var recipientsDataClass = RecipientsDataClass()
    var emergencyRequestList = arrayListOf<RecipientsDataClass>()
    var recipientId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            recipientId = it.getInt("id", 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUpdateEmergencyBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        donationDatabase = DonationDatabase.getInstance(requireContext())
        getRecipientList()

        binding?.tvRecipientName?.setText(recipientsDataClass.recipientName)
        binding?.tvContactHospital?.setText(recipientsDataClass.contact)
        binding?.etMedicine?.setText(recipientsDataClass.medicineMoneyDetails)
        binding?.etMoney?.setText(recipientsDataClass.medicineMoneyDetails)
        when (recipientsDataClass.urgencyLevel) {
            1 -> binding?.rbLowUrgency?.isChecked = true
            2 -> binding?.rbMediumUrgency?.isChecked = true
            3 -> binding?.rbHighUrgency?.isChecked = true
        }
    }

    private fun getRecipientList() {
        recipientsDataClass = donationDatabase.DonationDao().getEmergencyRequestAccToId(recipientId)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UpdateEmergencyFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UpdateEmergencyFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}