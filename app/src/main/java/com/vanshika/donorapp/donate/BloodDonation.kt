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
import com.google.android.gms.maps.model.StreetViewPanoramaLocation
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
    val bloodGroupSpinner = binding?.bloodGroupSpinner
    val genderSpinner = binding?.genderSpinner
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
        binding = FragmentBloodDonationBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
// for gender
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
            R.array.blood_types,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bloodGroupSpinner?.adapter = adapter
        val selectedBloodGroup = bloodGroupSpinner?.selectedItem.toString()
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
            if (binding?.nameEditText?.text.toString().isNullOrEmpty()) {
                binding?.nameEditText?.error = "Fill Your Name"
            } else if (binding?.ageEditText?.text.toString().isNullOrEmpty()) {
                binding?.ageEditText?.error = "Fill Your Age"
            } else if (binding?.addrEditText?.text?.toString().isNullOrEmpty()) {
                binding?.addrEditText?.error = "Fill Your Age"

            } else if (binding?.contactEditText?.text.toString().isNullOrEmpty()) {
                binding?.contactEditText?.error = "Enter Your Mobile Number"
            } else if (binding?.contactEditText?.length() != 10) {
                binding?.contactEditText?.error = "Enter Your 10 digit Number"

            } else if (binding?.donationDate?.text.isNullOrEmpty()) {
                binding?.donationDate?.error = "Please select a donation date"
                Toast.makeText(requireContext(), "Donation date is required!", Toast.LENGTH_SHORT)
                    .show()
            } else if (binding?.donationFrequencyEditText?.text.toString().isNullOrEmpty()) {
                binding?.donationFrequencyEditText?.setError("Fill the frequency")
            } else if (selectedBloodGroup == "Select Blood Group") {
                Toast.makeText(
                    requireContext(),
                    "Please select your blood group",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (selectedGender == "Select Your Gender") {
                Toast.makeText(
                    requireContext(),
                    "Please select your Gender",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (binding?.donationDate?.text.toString().isEmpty()) {
                binding?.donationDate?.error = resources.getString(R.string.Enter_date)
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
                    "Your Details is Filled Successfully!",
                    Toast.LENGTH_SHORT
                ).show();


                val address = binding?.addrEditText?.text?.toString()
                if (!address.isNullOrEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val latLng = getLatLngFromAddress(address)

                        withContext(Dispatchers.Main) {
                            if (latLng != null) {
                                donorDatabase.DonationDao().insertDonor(
                                    DonorsDataClass(
                                        donorName = binding?.nameEditText?.text?.toString(),
                                        address = binding?.addrEditText?.text?.toString(),
                                        age = binding?.ageEditText?.text?.toString(),
                                        gender = selectedGender,
                                        number = binding?.contactEditText?.text?.toString(),
//                                                gender = binding?.genderEdittext?.text?.toString(),

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
                                        bloodPressur = hasBloodPressureIssue
                                    )
                                )
                            }
                            findNavController().navigate(R.id.donateFragment)
                        }
                    }
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