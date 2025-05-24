package com.vanshika.donorapp.requests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vanshika.donorapp.DonationDatabase
import com.vanshika.donorapp.R
import com.vanshika.donorapp.databinding.FragmentEmergencyRequestBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class EmergencyRequestFragment : Fragment() {

    private var binding: FragmentEmergencyRequestBinding? = null
    private lateinit var donationDatabase: DonationDatabase
    private var selectedUrgency: Int = 1
    private var bloodOrganRequirement: String? = null
    private var selectedLocation: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmergencyRequestBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        donationDatabase = DonationDatabase.getInstance(requireContext())

        val requirementOptions = resources.getStringArray(R.array.requirement_options)
        val requirementAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            requirementOptions
        )
        binding?.spinnerRequirement?.adapter = requirementAdapter

        binding?.spinnerRequirement?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
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

        binding?.spinnerDynamic?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                bloodOrganRequirement = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val locationArray = arrayOf("City Hospital,Delhi", "Capital Hospital,Jalandhar")
        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, locationArray)
        binding?.spinnerLocation?.adapter = arrayAdapter
        binding?.spinnerLocation?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedLocation = locationArray[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
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
            val recipientName = binding?.tvRecipientName?.text?.toString()?.trim()
            val contact = binding?.tvContactHospital?.text?.toString()?.trim()
            val selectedRequirement = binding?.spinnerRequirement?.selectedItem?.toString()
            val medicineMoneyDetails = when(selectedRequirement){
                "Medicine" -> binding?.etMedicine?.text?.toString()?.trim()
                "Money" -> binding?.etMoney?.text?.toString()?.trim()
                else -> null
            }

            if (recipientName.isNullOrEmpty() || selectedRequirement.isNullOrEmpty() || selectedLocation.isEmpty() || contact.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val requestId = UUID.randomUUID().toString()
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous"

            val request = RecipientsDataClass(
                recipientId = requestId,
                name = recipientName,
                requirement = selectedRequirement,
                bloodOrganRequirement = bloodOrganRequirement,
                location = selectedLocation,
                contactNo = contact,
                urgencyLevel = selectedUrgency,
                medicineMoneyDetails = medicineMoneyDetails,
                userId = userId
            )

            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("emergency_requests")
                .document(requestId)
                .set(request)
                .addOnSuccessListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        donationDatabase.DonationDao().insertRequest(request)
                    }
                    Toast.makeText(context, "Request saved successfully", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to save request", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateDynamicSpinner(arrayRes: Int, hint: String) {
        val options = resources.getStringArray(arrayRes)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, options)
        binding?.tvDynamicSelection?.text = hint
        binding?.tvDynamicSelection?.visibility = View.VISIBLE
        binding?.spinnerDynamic?.adapter = adapter
        binding?.spinnerDynamic?.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
