package com.vanshika.donorapp.requests

import android.os.Bundle
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
    lateinit var arrayAdapter: ArrayAdapter<String>
    var locationArray = arrayOf("City Hospital,Delhi", "Capital Hospital,Jalandhar")

    var recipientsDataClass = RecipientsDataClass()
    var emergencyRequestList = arrayListOf<RecipientsDataClass>()
    private var selectedUrgency: Int = 1
    private var selectedLocation: String = ""
    private var bloodOrganRequirement: String? = null
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
                        "Blood" -> {
                            updateDynamicSpinner(R.array.blood_groups, "Select Blood Group")
                            binding?.llMedicine?.visibility = View.GONE
                            binding?.llMoney?.visibility = View.GONE
                            binding?.spinnerDynamic?.visibility = View.VISIBLE
                        }

                        "Organ" -> {
                            updateDynamicSpinner(R.array.organ_types, "Select Organ Type")
                            binding?.llMedicine?.visibility = View.GONE
                            binding?.llMoney?.visibility = View.GONE
                            binding?.spinnerDynamic?.visibility = View.VISIBLE
                        }

                        "Medicine" -> {
                            binding?.llMedicine?.visibility = View.VISIBLE
                            binding?.llMoney?.visibility = View.GONE
                            binding?.spinnerDynamic?.visibility = View.GONE
                        }

                        "Money" -> {
                            binding?.llMoney?.visibility = View.VISIBLE
                            binding?.llMedicine?.visibility = View.GONE
                            binding?.spinnerDynamic?.visibility = View.GONE
                        }

                        else -> {
                            binding?.llMedicine?.visibility = View.GONE
                            binding?.llMoney?.visibility = View.GONE
                            binding?.spinnerDynamic?.visibility = View.GONE
                        }
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        binding?.spinnerDynamic?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    bloodOrganRequirement = parent?.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        arrayAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, locationArray)
        binding?.spinnerLocation?.adapter = arrayAdapter
        binding?.spinnerLocation?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
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

        // Populate the spinner
        setupSpinner()
        binding?.tvRecipientName?.setText(recipientsDataClass.name)
        binding?.tvContactHospital?.setText(recipientsDataClass.contactNo)
        binding?.etMedicine?.setText(recipientsDataClass.medicineMoneyDetails)
        binding?.etMoney?.setText(recipientsDataClass.medicineMoneyDetails)
        when (recipientsDataClass.urgencyLevel) {
            1 -> binding?.rbLowUrgency?.isChecked = true
            2 -> binding?.rbMediumUrgency?.isChecked = true
            3 -> binding?.rbHighUrgency?.isChecked = true
        }
        binding?.btnUpdate?.setOnClickListener {
            if (binding?.tvRecipientName?.text?.toString()?.trim()?.isEmpty() == true) {
                binding?.tvRecipientName?.error = resources.getString(R.string.enter_recipient_name)
            } else if (binding?.tvContactHospital?.text?.trim()?.isEmpty() == true) {
                binding?.tvContactHospital?.error =
                    resources.getString(R.string.enter_hospital_contact)
            } else if (binding?.urgencyRadioGroup?.checkedRadioButtonId == -1) {
                Toast.makeText(
                    requireContext(), "Please select an urgency level",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val requestedItem = binding?.spinnerRequirement?.selectedItem.toString()
                val selectedRequirement = binding?.spinnerRequirement?.selectedItem?.toString()
                val selectedLocation = binding?.spinnerLocation?.selectedItem?.toString()
                val bloodOrganRequirement =
                    if (selectedRequirement == "Blood" || selectedRequirement == "Organ") {
                        binding?.spinnerDynamic?.selectedItem?.toString()
                    } else null

                val medicineMoneyDetails = if (selectedRequirement == "Medicine") {
                    binding?.etMedicine?.text?.toString()?.trim()
                } else if (selectedRequirement == "Money") {
                    binding?.etMoney?.text?.toString()?.trim()
                } else null
                donationDatabase.DonationDao().updateEmergencyRequest(
                    RecipientsDataClass(
                        name = binding?.tvRecipientName?.text?.toString(),
                        requirement = requestedItem,
                        bloodOrganRequirement = bloodOrganRequirement,
                        medicineMoneyDetails = medicineMoneyDetails,
                        location = selectedLocation,
                        contactNo = binding?.tvContactHospital?.text?.toString(),
                        urgencyLevel = selectedUrgency
                        )
                )
                findNavController().popBackStack()
            }
        }
        getRecipientList()
    }

    private fun updateDynamicSpinner(bloodGroups: Int, s: String) {
        val options = resources.getStringArray(bloodGroups)
        val adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                options
            )

        binding?.tvDynamicSelection?.text = s
        binding?.tvDynamicSelection?.visibility = View.VISIBLE
        binding?.spinnerDynamic?.adapter = adapter
        binding?.spinnerDynamic?.visibility = View.VISIBLE
    }

    private fun setupSpinner() {
        val spinnerItems = listOf("Blood","Organ","Medicine", "Money") // Customize as needed
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.spinnerRequirement?.adapter = adapter

        // Set the previously selected value in the spinner
        val selectedIndex =
            spinnerItems.indexOf(recipientsDataClass.bloodOrganRequirement) // Adjust field name accordingly
        if (selectedIndex >= 0) {
            binding?.spinnerRequirement?.setSelection(selectedIndex)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


    private fun getRecipientList() {
        recipientsDataClass = donationDatabase.DonationDao().getEmergencyRequestAccToId(userId = recipientId)
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