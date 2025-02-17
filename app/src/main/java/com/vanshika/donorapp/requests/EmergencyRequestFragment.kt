package com.vanshika.donorapp.requests

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.vanshika.donorapp.DonationDatabase
import com.vanshika.donorapp.R
import com.vanshika.donorapp.databinding.FragmentEmergencyRequestBinding
import com.vanshika.donorapp.databinding.FragmentRequestsBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EmergencyRequestFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EmergencyRequestFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var binding: FragmentEmergencyRequestBinding? = null
    var requiredArray = arrayOf("Blood", "Organ")
    var locationArray = arrayOf("City Hospital,Delhi","Capital Hospital,Jalandhar")
    var recipientsDataClass = RecipientsDataClass()
    lateinit var donationDatabase: DonationDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmergencyRequestBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        donationDatabase = DonationDatabase.getInstance(requireContext())
        // Load requirement options (Blood, Organ)
        val requirementOptions = resources.getStringArray(R.array.requirement_options)
        val requirementAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            requirementOptions
        )
        binding?.spinnerRequirement?.adapter = requirementAdapter

        // Set a listener to dynamically update the second Spinner
        binding?.spinnerRequirement?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedRequirement = requirementOptions[position]

                    when (selectedRequirement) {
                        "Blood" -> updateDynamicSpinner(R.array.blood_groups, "Select Blood Group")
                        "Organ" -> updateDynamicSpinner(R.array.organ_types, "Select Organ Type")
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        binding?.btnSubmitRequest?.setOnClickListener {
            if (binding?.tvRecipientName?.text?.isEmpty() == true) {
                binding?.tvRecipientName?.error = resources.getString(R.string.enter_recipient_name)
            } else if (binding?.tvContactHospital?.text?.isEmpty() == true) {
                binding?.tvContactHospital?.error =
                    resources.getString(R.string.enter_hospital_contact)

            } else {
                donationDatabase.DonationDao().insertEmergencyRequest(
                    RecipientsDataClass(
//                            recipientId = ,
                        recipientName = binding?.tvRecipientName?.text?.toString(),
                        requestedItem = binding?.spinnerRequirement?.toString(),
                        location = binding?.spinnerLocation?.toString(),
                        contact = binding?.tvContactHospital?.text?.toString(),
                    )
                )
                findNavController().popBackStack()
            }
        }
    }

    private fun updateDynamicSpinner(bloodGroups: Int, s: String) {
        val options = resources.getStringArray(bloodGroups)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, options)

        binding?.tvDynamicSelection?.text = s
        binding?.tvDynamicSelection?.visibility = View.VISIBLE
        binding?.spinnerDynamic?.adapter = adapter
        binding?.spinnerDynamic?.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EmergencyRequestFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EmergencyRequestFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}