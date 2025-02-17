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
    lateinit var arrayAdapter: ArrayAdapter<String>
    private var selectedUrgency: Int = 1
    var requiredArray = arrayOf("Blood", "Organ")
    var locationArray = arrayOf("City Hospital,Delhi", "Capital Hospital,Jalandhar")
    var recipientsDataClass = RecipientsDataClass()
    lateinit var donationDatabase: DonationDatabase
    var selectedLocation = ""


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
        arrayAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, locationArray)
        binding?.spinnerLocation?.adapter = arrayAdapter
        binding?.spinnerLocation?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedLocation = locationArray[position]
                println("selectedLocation: $selectedLocation")

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        binding?.urgencyRadioGroup?.setOnCheckedChangeListener { _, checkedId ->
            selectedUrgency = when (checkedId) {
                R.id.rbLowUrgency -> 1
                R.id.rbMediumUrgency -> 2
                R.id.rbHighUrgency -> 3
                else -> 1
            }
        }


        binding?.btnSubmitRequest?.setOnClickListener {
            val recipientName = binding?.tvRecipientName?.text?.toString()?.trim() ?: ""
            val requestedItem = binding?.spinnerRequirement?.selectedItem.toString()
            val specificRequirement = binding?.spinnerDynamic?.selectedItem.toString()
            val hospitalLocation = binding?.tvContactHospital?.text?.toString()?.trim() ?: ""

            if (binding?.tvRecipientName?.text?.isEmpty() == true) {
                binding?.tvRecipientName?.error = resources.getString(R.string.enter_recipient_name)
            } else if (binding?.tvContactHospital?.text?.isEmpty() == true) {
                binding?.tvContactHospital?.error =
                    resources.getString(R.string.enter_hospital_contact)
            } else if (binding?.urgencyRadioGroup?.checkedRadioButtonId == -1) {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.select_Urgency),
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                donationDatabase.DonationDao().insertEmergencyRequest(
                    RecipientsDataClass(

                        recipientName = binding?.tvRecipientName?.text?.toString(),
                        requestedItem = requestedItem,
                        specificRequirement = specificRequirement,
                        location = selectedLocation,
                        contact = binding?.tvContactHospital?.text?.toString(),
                        urgencyLevel = selectedUrgency
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