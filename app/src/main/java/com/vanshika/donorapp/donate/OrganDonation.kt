package com.vanshika.donorapp.donate

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.vanshika.donorapp.DonationDatabase
import com.vanshika.donorapp.R
import com.vanshika.donorapp.databinding.FragmentOrganDonationBinding
import java.text.SimpleDateFormat

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OrganDonation.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrganDonation : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var binding: FragmentOrganDonationBinding? = null
    lateinit var donarDatabase: DonationDatabase
    var donation = arrayListOf<DonorsDataClass>()
    val genderSpinner = binding?.genderSpinner
    val organSpinner = binding?.spinOrgan
    var calendar = android.icu.util.Calendar.getInstance()
    var simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")


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
        // Inflate the layout for this fragment
        donarDatabase = DonationDatabase.getInstance(requireContext())
        binding = FragmentOrganDonationBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // date
        binding?.donationDate?.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
//                R.style.MyDatePickerStyle,
                { _, year, month, date ->
                    calendar.set(year, month, date)
                    binding?.donationDate?.setText(simpleDateFormat.format(calendar.time))
                },
                android.icu.util.Calendar.getInstance().get(android.icu.util.Calendar.YEAR),
                android.icu.util.Calendar.getInstance().get(android.icu.util.Calendar.MONTH),
                android.icu.util.Calendar.getInstance().get(android.icu.util.Calendar.DATE),
            )
            val calendar = android.icu.util.Calendar.getInstance()
            datePickerDialog.datePicker.minDate = calendar.timeInMillis
            calendar.add(android.icu.util.Calendar.YEAR, +1)
            datePickerDialog.datePicker.maxDate = calendar.timeInMillis
            datePickerDialog.show()
        }
        val genderAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender_types,
            android.R.layout.simple_spinner_item
        )
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner?.adapter = genderAdapter
        val selectedGender = genderSpinner?.selectedItem.toString()
        val organAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender_types,
            android.R.layout.simple_spinner_item
        )
        organAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        organSpinner?.adapter = organAdapter
        val selectedOrgan = organSpinner?.selectedItem.toString()

        binding?.submitButton?.setOnClickListener {
            val isHealthy = binding?.healthYes?.isChecked ?: false
            val traveledRecently = binding?.travelYes?.isChecked ?: false
            val tookMedication = binding?.medYes?.isChecked ?: false
            val consumesAlcohol = binding?.alcoholYes?.isChecked ?: false
            val hasBloodPressureIssue =
                binding?.bloodPressureYes?.isChecked ?: false // true if "Yes" is selected
            val isDiabetic =
                binding?.diabetesYes?.isChecked ?: false // true if "Yes" is selected
            val hadRecentSurgery =
                binding?.surgeryYes?.isChecked ?: false // true if "Yes" is selected
            val tookRecentVaccine =
                binding?.vaccineYes?.isChecked ?: false // true if "Yes" is selected
            if (binding?.nameEditText?.text?.toString().isNullOrEmpty()) {
                binding?.nameEditText?.setError("Your Name")
            } else if (binding?.ageEditText?.text?.toString().isNullOrEmpty()) {
                binding?.ageEditText?.setError("Enter age")
            } else if (binding?.numberEditText?.length() != 10) {
                binding?.numberEditText?.setError("Enter 10 digit number")
            } else if (binding?.addrEditText?.text?.toString().isNullOrEmpty()) {
                binding?.addrEditText?.setError("Enter your Address")
            } else if (binding?.donationDate?.text.isNullOrEmpty()) {
                binding?.donationDate?.error = "Please select a donation date"
                Toast.makeText(requireContext(), "Donation date is required!", Toast.LENGTH_SHORT)
                    .show()
            } else if (selectedOrgan == "Select Organ") {
                Toast.makeText(
                    requireContext(),
                    "Please select Organ to Donate",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (selectedGender == "Select Gender") {
                Toast.makeText(
                    requireContext(),
                    "Please select Your Gender",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (binding?.consentEditText?.text?.toString().isNullOrEmpty()) {
                binding?.consentEditText?.setError("Enter Your Wish")
            } else if (!isHealthy) {
                Toast.makeText(
                    requireContext(),
                    "You must be healthy to donate blood!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (traveledRecently) {
                Toast.makeText(
                    requireContext(),
                    "Please wait 6 months after international travel!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (tookMedication) {
                Toast.makeText(
                    requireContext(),
                    "Wait 7 days after taking medication!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (consumesAlcohol) {
                Toast.makeText(
                    requireContext(),
                    "Avoid alcohol 24 hours before donating!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (hasBloodPressureIssue) {
                Toast.makeText(
                    requireContext(),
                    "You can't donate blood if you have blood pressure",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (isDiabetic) {
                Toast.makeText(
                    requireContext(),
                    "You can't donate blood if you have diabetes",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (hadRecentSurgery) {
                Toast.makeText(
                    requireContext(),
                    "You can't donate blood if you had Surgery",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (tookRecentVaccine) {
                Toast.makeText(
                    requireContext(),
                    "You can't donate blood if you had vaccination",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Your Details is Filled Successfuly!",
                    Toast.LENGTH_SHORT
                ).show();
                donarDatabase.DonationDao().insertDonor(
                    DonorsDataClass(
                        donorName = binding?.nameEditText?.text?.toString(),
                        age = binding?.ageEditText?.text?.toString(),
                        gender = selectedGender,
                        number = binding?.numberEditText?.text?.toString(),
                        donationType = "Organ",
                        address = binding?.addrEditText?.text?.toString(),
                        donationfrequency = selectedOrgan,
                        createdDate = binding?.donationDate?.text?.toString(),
                        isHealthy = isHealthy,
                        traveledRecently = traveledRecently,
                        tookMedication = tookMedication,
                        consumesAlcohol = consumesAlcohol,
                        hadRecentSurgery = hadRecentSurgery,
                        tookRecentVaccine = tookRecentVaccine,
                        diabities = isDiabetic,
                        bloodPressur = hasBloodPressureIssue

                    )
                )
                findNavController().navigate(R.id.donateFragment)
            }

        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OrganDonation.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrganDonation().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}