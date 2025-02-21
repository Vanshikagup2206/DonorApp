package com.vanshika.donorapp.requests

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.vanshika.donorapp.DonationDatabase
import com.vanshika.donorapp.R
import com.vanshika.donorapp.databinding.FragmentEmergencyRequestBinding
import com.vanshika.donorapp.databinding.FragmentRequestsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    var requiredArray = arrayOf("Blood", "Organ", "Medicine", "Money")
    var locationArray = arrayOf("City Hospital,Delhi", "Capital Hospital,Jalandhar")
    var recipientsDataClass = RecipientsDataClass()
    lateinit var donationDatabase: DonationDatabase
    private var selectedLocation: String = ""
    private var selectedRequirement: String = ""
    private var bloodOrganRequirement: String? = null
    private var additionalDetails: String? = null


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
        setupUrgencyButtons()
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


        binding?.btnSubmitRequest?.setOnClickListener {


                if (binding?.tvRecipientName?.text?.isEmpty() == true) {
                    binding?.tvRecipientName?.error =
                        resources.getString(R.string.enter_recipient_name)
                } else if (binding?.tvContactHospital?.text?.isEmpty() == true) {
                    binding?.tvContactHospital?.error =
                        resources.getString(R.string.enter_hospital_contact)
                } else if (binding?.urgencyRadioGroup?.checkedRadioButtonId == -1) {
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.select_Urgency),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            val requestedItem = binding?.spinnerRequirement?.selectedItem.toString()
            val recipientName = binding?.tvRecipientName?.text?.toString()?.trim() ?:" "
            val selectedRequirement = binding?.spinnerRequirement?.selectedItem?.toString()
            val selectedLocation = binding?.spinnerLocation?.selectedItem?.toString()
            val contact = binding?.tvContactHospital?.text?.toString()?.trim()

            val bloodOrganRequirement =
                if (selectedRequirement == "Blood" || selectedRequirement == "Organ") {
                    binding?.spinnerDynamic?.selectedItem?.toString()
                } else null

            val medicineMoneyDetails = if (selectedRequirement == "Medicine") {
                binding?.etMedicine?.text?.toString()?.trim()
            } else if (selectedRequirement == "Money") {
                binding?.etMoney?.text?.toString()?.trim()
            } else null

            if (recipientName.isNullOrEmpty() || selectedRequirement.isNullOrEmpty() || selectedLocation.isNullOrEmpty() || contact.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val recipient = RecipientsDataClass(
                recipientName = binding?.tvRecipientName?.text?.toString(),
                requestedItem = requestedItem,
                bloodOrganRequirement = bloodOrganRequirement,
                location = selectedLocation,
                contact = binding?.tvContactHospital?.text?.toString(),
                urgencyLevel = selectedUrgency,
                medicineMoneyDetails = medicineMoneyDetails

            )

            CoroutineScope(Dispatchers.IO).launch {
                donationDatabase.DonationDao().insertEmergencyRequest(recipient)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Request Submitted", Toast.LENGTH_SHORT)
                        .show()
                    findNavController().popBackStack()
                }

            }
        }
    }

    private fun setupUrgencyButtons() {
        val radioGroup = view?.findViewById<RadioGroup>(R.id.urgencyRadioGroup)
        val lowUrgency = view?.findViewById<RadioButton>(R.id.rbLowUrgency)
        val mediumUrgency = view?.findViewById<RadioButton>(R.id.rbMediumUrgency)
        val highUrgency = view?.findViewById<RadioButton>(R.id.rbHighUrgency)

        radioGroup?.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbLowUrgency -> {
                    animateButton(lowUrgency, 1.1f)
                    animateButton(mediumUrgency, 1.0f)
                    animateButton(highUrgency, 1.0f)
                }

                R.id.rbMediumUrgency -> {
                    animateButton(lowUrgency, 1.0f)
                    animateButton(mediumUrgency, 1.1f)
                    animateButton(highUrgency, 1.0f)
                }

                R.id.rbHighUrgency -> {
                    animateButton(lowUrgency, 1.0f)
                    animateButton(mediumUrgency, 1.0f)
                    animateButton(highUrgency, 1.1f)
                }
            }
        }
    }

    private fun animateButton(lowUrgency: RadioButton?, fl: Float) {
        lowUrgency?.animate()?.scaleX(fl)?.scaleY(fl)?.setDuration(200)?.start()
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