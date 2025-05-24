package com.vanshika.donorapp.donate

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vanshika.donorapp.DonationDatabase
import com.vanshika.donorapp.R
import com.vanshika.donorapp.databinding.FragmentOrganDonationBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class OrganDonation : Fragment() {

    private var binding: FragmentOrganDonationBinding? = null
    private lateinit var donationDatabase: DonationDatabase
    private val calendar = Calendar.getInstance()
    private val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        donationDatabase = DonationDatabase.getInstance(requireContext())
        binding = FragmentOrganDonationBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinners()
        setupDatePicker()
        setupSubmitButton()
        setupResetButton()
    }

    private fun setupSpinners() {
        val genderAdapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.gender_types, android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding?.genderSpinner?.adapter = genderAdapter

        val organAdapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.organ_types, android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding?.spinOrgan?.adapter = organAdapter
    }

    private fun setupDatePicker() {
        binding?.donationDate?.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    binding?.donationDate?.setText(simpleDateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            calendar.add(Calendar.YEAR, 1)
            datePickerDialog.datePicker.maxDate = calendar.timeInMillis
            datePickerDialog.show()
        }
    }

    private fun setupResetButton() {
        binding?.resetCheckBox?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                resetForm()
                binding?.resetCheckBox?.isChecked = false
            }
        }
    }

    private fun setupSubmitButton() {
        binding?.submitButton?.setOnClickListener {
            val name = binding?.nameEditText?.text?.toString()
            val age = binding?.ageEditText?.text?.toString()
            val number = binding?.numberEditText?.text?.toString()
            val address = binding?.addrEditText?.text?.toString()
            val date = binding?.donationDate?.text?.toString()
            val gender = binding?.genderSpinner?.selectedItem.toString()
            val organ = binding?.spinOrgan?.selectedItem.toString()
            val consent = binding?.consentEditText?.text?.toString()

            if (name.isNullOrEmpty()) {
                binding?.nameEditText?.error = "Enter your name"
                return@setOnClickListener
            }
            if (age.isNullOrEmpty()) {
                binding?.ageEditText?.error = "Enter age"
                return@setOnClickListener
            }
            if (number?.length != 10) {
                binding?.numberEditText?.error = "Enter 10 digit number"
                return@setOnClickListener
            }
            if (address.isNullOrEmpty()) {
                binding?.addrEditText?.error = "Enter address"
                return@setOnClickListener
            }
            if (date.isNullOrEmpty()) {
                binding?.donationDate?.error = "Select donation date"
                return@setOnClickListener
            }
            if (gender == "Select Gender") {
                Toast.makeText(requireContext(), "Select gender", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (organ == "Select Organ") {
                Toast.makeText(requireContext(), "Select organ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (consent.isNullOrEmpty()) {
                binding?.consentEditText?.error = "Enter your wish"
                return@setOnClickListener
            }

            val isHealthy = binding?.healthYes?.isChecked == true
            val traveled = binding?.travelYes?.isChecked == true
            val medication = binding?.medYes?.isChecked == true
            val alcohol = binding?.alcoholYes?.isChecked == true
            val bp = binding?.bloodPressureYes?.isChecked == true
            val diabetes = binding?.diabetesYes?.isChecked == true
            val surgery = binding?.surgeryYes?.isChecked == true
            val vaccine = binding?.vaccineYes?.isChecked == true

            if (!isHealthy) {
                Toast.makeText(requireContext(), "You must be healthy!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (traveled) {
                Toast.makeText(requireContext(), "Wait 6 months after travel!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (medication) {
                Toast.makeText(requireContext(), "Wait 7 days after medication!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (alcohol) {
                Toast.makeText(requireContext(), "Avoid alcohol 24 hrs before!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (bp || diabetes || surgery || vaccine) {
                Toast.makeText(requireContext(), "Not eligible due to health conditions!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val donationTypeId = binding?.anonymousGroup?.checkedRadioButtonId
            if (donationTypeId == -1) {
                Toast.makeText(requireContext(), "Select donation type!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val donationType = when (donationTypeId) {
                R.id.anonymousYes -> "Anonymous"
                R.id.anonymousNo -> "Public"
                else -> ""
            }

            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Confirm Details")
                .setMessage("Are your details correct?")
                .setPositiveButton("Yes") { _, _ ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val latLng = getLatLngFromAddress(address)
                        if (latLng != null) {
                            val donor = DonorsDataClass(
                                donorName = name,
                                age = age,
                                gender = gender,
                                number = number,
                                donationType = "Organ",
                                address = address,
                                donationfrequency = organ,
                                createdDate = date,
                                isHealthy = true,
                                traveledRecently = false,
                                tookMedication = false,
                                consumesAlcohol = false,
                                hadRecentSurgery = false,
                                tookRecentVaccine = false,
                                diabities = false,
                                bloodPressur = false,
                                lattitude = latLng.latitude,
                                longitude = latLng.longitude,
                                donationMethod = donationType
                            )
                            donationDatabase.DonationDao().insertDonor(donor)
                            saveDonationToFirestore(auth.currentUser?.uid ?: "", latLng.latitude, latLng.longitude, "Organ")
                        }
                        withContext(Dispatchers.Main) {
                            findNavController().navigate(R.id.donateFragment)
                        }
                    }
                }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                .create().show()
        }
    }

    private fun resetForm() {
        binding?.apply {
            nameEditText.setText("")
            ageEditText.setText("")
            numberEditText.setText("")
            consentEditText.setText("")
            addrEditText.setText("")
            donationDate.setText("")
            spinOrgan.setSelection(0)
            genderSpinner.setSelection(0)
            anonymousGroup.clearCheck()
            healthRadioGroup.clearCheck()
            alcoholRadioGroup.clearCheck()
            surgeryRadioGroup.clearCheck()
            vaccineRadioGroup.clearCheck()
            travelRadioGroup.clearCheck()
            diabetesRadioGroup.clearCheck()
            bloodPressureRadioGroup.clearCheck()
        }
        Toast.makeText(requireContext(), "Form reset!", Toast.LENGTH_SHORT).show()
    }

    private fun getLatLngFromAddress(address: String): LatLng? {
        return try {
            val url = URL("https://nominatim.openstreetmap.org/search?format=json&q=$address")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(response)

            if (jsonArray.length() > 0) {
                val obj = jsonArray.getJSONObject(0)
                LatLng(obj.getDouble("lat"), obj.getDouble("lon"))
            } else null
        } catch (e: Exception) {
            Log.e("Geocode", "Error: ${e.message}", e)
            null
        }
    }

    private fun saveDonationToFirestore(userId: String, lat: Double, lon: Double, type: String) {
        val firestore = FirebaseFirestore.getInstance()
        val donation = hashMapOf(
            "userId" to userId,
            "latitude" to lat,
            "longitude" to lon,
            "donationType" to type
        )
        firestore.collection("donations")
            .add(donation)
            .addOnSuccessListener { Log.d("Firestore", "Donation added") }
            .addOnFailureListener { Log.e("Firestore", "Failed", it) }
    }
}
