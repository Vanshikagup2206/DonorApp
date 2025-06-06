package com.vanshika.donorapp.donate

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.StreetViewPanoramaLocation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vanshika.donorapp.DonationDao
import com.vanshika.donorapp.DonationDatabase
import com.vanshika.donorapp.R
import com.vanshika.donorapp.databinding.FragmentBloodDonationBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BloodDonation.newInstance] factory method to
 * create an instance of this fragment.
 */
class BloodDonation : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var binding: FragmentBloodDonationBinding? = null
    var bloodDonation = arrayListOf<DonorsDataClass>()
    var auth = FirebaseAuth.getInstance()
    var db = FirebaseFirestore.getInstance()
    lateinit var donorDao: DonationDao
    var calendar = android.icu.util.Calendar.getInstance()
    var simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
    lateinit var donorDatabase: DonationDatabase

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
        donorDatabase = DonationDatabase.getInstance(requireContext())
        donorDao= donorDatabase.DonationDao()
        binding = FragmentBloodDonationBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
// for gender
        val bloodGroupSpinner = binding?.bloodGroupSpinner
        val genderSpinner = binding?.genderSpinner
        val genderAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender_types,
            android.R.layout.simple_spinner_item
        )
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner?.adapter = genderAdapter
        val selectedGender = genderSpinner?.selectedItem.toString()
        // for blood
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.blood_groups,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bloodGroupSpinner?.adapter = adapter
        val selectedBloodGroup = bloodGroupSpinner?.selectedItem.toString()
        binding?.donationDate?.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
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
        binding?.resetCheckBox?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                resetForm()
                binding?.resetCheckBox?.isChecked = false // Auto uncheck kar dena reset ke baad
            }
        }
        binding?.submitButton?.setOnClickListener {
            val isHealthy = binding?.healthYes?.isChecked ?: false
            val traveledRecently = binding?.travelYes?.isChecked ?: false
            val tookMedication = binding?.medYes?.isChecked ?: false
            val consumesAlcohol = binding?.alcoholYes?.isChecked ?: false
            val hasBloodPressureIssue = binding?.bloodPressureYes?.isChecked ?: false
            val isDiabetic = binding?.diabetesYes?.isChecked ?: false
            val hadRecentSurgery = binding?.surgeryYes?.isChecked ?: false
            val tookRecentVaccine = binding?.vaccineYes?.isChecked ?: false

            when {
                binding?.nameEditText?.text.toString().isEmpty() ->
                    binding?.nameEditText?.error = "Fill Your Name"

                binding?.ageEditText?.text.toString().isEmpty() ->
                    binding?.ageEditText?.error = "Fill Your Age"

                binding?.addrEditText?.text.toString().isEmpty() ->
                    binding?.addrEditText?.error = "Fill Your Address"

                binding?.contactEditText?.text.toString().isEmpty() ->
                    binding?.contactEditText?.error = "Enter Your Mobile Number"

                binding?.contactEditText?.length() != 10 ->
                    binding?.contactEditText?.error = "Enter Your 10 digit Number"

                binding?.donationDate?.text.isNullOrEmpty() -> {
                    binding?.donationDate?.error = "Please select a donation date"
                    Toast.makeText(
                        requireContext(),
                        "Donation date is required!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                binding?.donationFrequencyEditText?.text.toString().isEmpty() ->
                    binding?.donationFrequencyEditText?.error = "Fill the frequency"

                selectedBloodGroup == "Select Blood Group" ->
                    Toast.makeText(
                        requireContext(),
                        "Please select your blood group",
                        Toast.LENGTH_SHORT
                    ).show()

                selectedGender == "Select Your Gender" ->
                    Toast.makeText(
                        requireContext(),
                        "Please select your Gender",
                        Toast.LENGTH_SHORT
                    ).show()

                !isHealthy ->
                    Toast.makeText(
                        requireContext(),
                        "You must be healthy to donate blood!",
                        Toast.LENGTH_SHORT
                    ).show()

                traveledRecently ->
                    Toast.makeText(
                        requireContext(),
                        "Please wait 6 months after international travel!",
                        Toast.LENGTH_SHORT
                    ).show()

                tookMedication ->
                    Toast.makeText(
                        requireContext(),
                        "Wait 7 days after taking medication!",
                        Toast.LENGTH_SHORT
                    ).show()

                consumesAlcohol ->
                    Toast.makeText(
                        requireContext(),
                        "Avoid alcohol 24 hours before donating!",
                        Toast.LENGTH_SHORT
                    ).show()

                hasBloodPressureIssue ->
                    Toast.makeText(
                        requireContext(),
                        "You can't donate blood if you have blood pressure",
                        Toast.LENGTH_SHORT
                    ).show()

                isDiabetic ->
                    Toast.makeText(
                        requireContext(),
                        "You can't donate blood if you have diabetes",
                        Toast.LENGTH_SHORT
                    ).show()

                hadRecentSurgery ->
                    Toast.makeText(
                        requireContext(),
                        "You can't donate blood if you had Surgery",
                        Toast.LENGTH_SHORT
                    ).show()

                tookRecentVaccine ->
                    Toast.makeText(
                        requireContext(),
                        "You can't donate blood if you had vaccination",
                        Toast.LENGTH_SHORT
                    ).show()

                binding?.anonymousGroup?.checkedRadioButtonId == -1 ->
                    Toast.makeText(
                        requireContext(),
                        "Please select a donation type (Anonymous or Public)!",
                        Toast.LENGTH_SHORT
                    ).show()

                else -> {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Check Your Details")
                    builder.setMessage("Are you sure your details are correct? This can't be edited later.")
                    builder.setPositiveButton("Yes") { _, _ ->
                        val address = binding?.addrEditText?.text?.toString() ?: ""

                        CoroutineScope(Dispatchers.IO).launch {
                            val latLng = getLatLngFromAddress(address)
                            withContext(Dispatchers.Main) {
                                if (latLng == null) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Invalid address. Please enter a valid location.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    return@withContext
                                }

                                val selectedDonationType =
                                    when (binding?.anonymousGroup?.checkedRadioButtonId) {
                                        R.id.anonymousYes -> "Anonymous"
                                        R.id.anonymousNo -> "Public"
                                        else -> ""
                                    }

                                val donor = DonorsDataClass(
                                    donorName = binding?.nameEditText?.text?.toString(),
                                    address = address,
                                    age = binding?.ageEditText?.text?.toString(),
                                    gender = selectedGender,
                                    number = binding?.contactEditText?.text?.toString(),
                                    donationfrequency = binding?.donationFrequencyEditText?.text?.toString(),
                                    donationType = "Blood",
                                    createdDate = binding?.donationDate?.text?.toString(),
                                    lattitude = latLng.latitude,
                                    longitude = latLng.longitude,
                                    isHealthy = isHealthy,
                                    bloodType = selectedBloodGroup,
                                    traveledRecently = traveledRecently,
                                    tookMedication = tookMedication,
                                    consumesAlcohol = consumesAlcohol,
                                    hadRecentSurgery = hadRecentSurgery,
                                    tookRecentVaccine = tookRecentVaccine,
                                    diabities = isDiabetic,
                                    bloodPressur = hasBloodPressureIssue,
                                    donationMethod = selectedDonationType
                                )

                                lifecycleScope.launch(Dispatchers.IO) {
                                    donorDao.insertDonor(donor)
                                    saveDonationToFirestore(auth.currentUser?.uid ?: "", latLng.latitude, latLng.longitude, "Blood")


                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            requireContext(),
                                            "Blood Donation Details Submitted",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        findNavController().navigate(R.id.donateFragment)
                                    }
                                }
                            }
                        }
                    }
                    builder.setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                        Toast.makeText(
                            requireContext(),
                            "Please review your details.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    builder.create().show()
                }
            }
        }
    }



    private fun getLatLngFromAddress(address: String): LatLng? {
        return try {
            val url = "https://nominatim.openstreetmap.org/search?format=json&q=$address"
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            val inputStream = connection.inputStream.bufferedReader().use { it.readText() }
            val responseArray = JSONArray(inputStream)

            if (responseArray.length() > 0) {
                val locationData = responseArray.getJSONObject(0)
                val lat = locationData.getDouble("lat")
                val lon = locationData.getDouble("lon")
                Log.d("Geocode", "Address: $address -> Lat: $lat, Lon: $lon")
                LatLng(lat, lon)
            } else {
                Log.e("Geocode", "No location found for address: $address")
                null
            }
        } catch (e: Exception) {
            Log.e("Geocode", "Error fetching coordinates", e)
            null
        }
    }

    private fun resetForm() {
        binding?.nameEditText?.setText("")
        binding?.ageEditText?.setText("")
        binding?.contactEditText?.setText("")
        binding?.donationFrequencyEditText?.setText("")
        binding?.addrEditText?.setText("")
        binding?.donationDate?.setText("")
        binding?.bloodGroupSpinner?.setSelection(0)
        binding?.genderSpinner?.setSelection(0)
        binding?.anonymousGroup?.clearCheck()
        binding?.healthRadioGroup?.clearCheck()
        binding?.alcoholRadioGroup?.clearCheck()
        binding?.surgeryRadioGroup?.clearCheck()
        binding?.vaccineRadioGroup?.clearCheck()
        binding?.travelRadioGroup?.clearCheck()
        binding?.diabetesRadioGroup?.clearCheck()
        binding?.bloodPressureRadioGroup?.clearCheck()
        binding?.anonymousGroup?.clearCheck()
        Toast.makeText(requireContext(), "Form reset successfully!", Toast.LENGTH_SHORT).show()
    }

    fun saveDonationToFirestore(userId: String, lat: Double, lng: Double, donationType: String) {
        val donationData = hashMapOf(
            "userId" to userId, // 🔑 required!
            "latitude" to lat,
            "longitude" to lng,
            "donationType" to donationType
        )

        FirebaseFirestore.getInstance()
            .collection("donations")
            .add(donationData)
            .addOnSuccessListener {
                Log.d("Firestore", "Donation saved!")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error saving donation", e)
            }
    }


    private fun searchLocation(
        location: String,
        isFrom: Boolean,
        onResult: (LatLng?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(
                    "https://nominatim.openstreetmap.org/search?format=json&q=${
                        URLEncoder.encode(
                            location,
                            "UTF-8"
                        )
                    }"
                )
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(response)

                withContext(Dispatchers.Main) {
                    if (jsonArray.length() > 0) {
                        val locationData = jsonArray.getJSONObject(0)
                        val lat = locationData.getDouble("lat")
                        val lon = locationData.getDouble("lon")
                        val latLng = LatLng(lat, lon)

                        Log.d("LocationDebug", "Address: $location -> LatLng: $latLng")

                        onResult(latLng)
                    } else {
                        Log.e("LocationDebug", "Location not found: $location")
                        Toast.makeText(
                            requireContext(),
                            "Location not found!",
                            Toast.LENGTH_SHORT
                        ).show()
                        onResult(null)
                    }
                }
            } catch (e: Exception) {
                Log.e("LocationError", "Error fetching location: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Failed to fetch location!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                onResult(null)
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
         * @return A new instance of fragment BloodDonation.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BloodDonation().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}