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
import com.vanshika.donorapp.DonationDatabase
import com.vanshika.donorapp.R
import com.vanshika.donorapp.databinding.FragmentMedicineDonationBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MedicineDonation.newInstance] factory method to
 * create an instance of this fragment.
 */
class MedicineDonation : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var binding: FragmentMedicineDonationBinding? = null
    lateinit var donardatabase: DonationDatabase
    var simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
    var calendar = android.icu.util.Calendar.getInstance()
    var donationlist = arrayListOf<DonorsDataClass>()


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
        donardatabase = DonationDatabase.getInstance(requireContext())
        binding = FragmentMedicineDonationBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val genderSpinner = binding?.genderSpinner
        val genderAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender_types,
            android.R.layout.simple_spinner_item
        )
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner?.adapter = genderAdapter
        val selectedGender = genderSpinner?.selectedItem.toString()
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
            if (binding?.editName?.text?.toString().isNullOrEmpty()) {
                binding?.editName?.setError("Enter your Name")
            } else if (binding?.editAmount?.text?.toString().isNullOrEmpty()) {
                binding?.editAmount?.setError("Enter amount")
            } else if (binding?.editMedicineType?.text?.toString().isNullOrEmpty()) {
                binding?.askMedicineType?.setError("Enter type")
            } else if (binding?.donationDate?.text?.toString().isNullOrEmpty()) {
                binding?.donationDate?.setError("Choose Date")
            } else if (binding?.editNumber?.length() != 10) {
                binding?.editNumber?.setError("Enter your Name")
            } else if (binding?.etAddress?.length() != 10) {
                binding?.etAddress?.setError("Enter your Address")
            } else if (selectedGender == "Select Your Gender") {
                Toast.makeText(
                    requireContext(),
                    "Please select your Gender",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (binding?.editAge?.text?.toString().isNullOrEmpty()) {
                binding?.editAge?.setError("Enter your Name")
            }
            val selectedRadioButtonId = binding?.anonymousGroup?.checkedRadioButtonId
            if (selectedRadioButtonId == -1) {
                Toast.makeText(
                    requireContext(),
                    "Please select a donation type (Anonymous or Public)!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val selectedDonationType = when (selectedRadioButtonId) {
                    R.id.anonymousYes -> "Anonymous"
                    R.id.anonymousNo -> "Public"
                    else -> ""
                }
                Toast.makeText(
                    requireContext(),
                    "Your Details is Filled Successfuly!",
                    Toast.LENGTH_SHORT
                ).show();
                val address = binding?.etAddress?.text?.toString()
                if (!address.isNullOrEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val latLng = getLatLngFromAddress(address)

                        withContext(Dispatchers.Main) {
                            if (latLng != null) {
                                donardatabase.DonationDao().insertDonor(
                                    DonorsDataClass(
                                        donationType = "Medicine",
                                        donorName = binding?.editName?.text?.toString(),
                                        age = binding?.editAge?.text?.toString(),
                                        address = binding?.etAddress?.text?.toString(),
                                        donationfrequency = binding?.editAmount?.text?.toString(),
                                        gender = selectedGender,
                                        number = binding?.editNumber?.text?.toString(),
                                        createdDate = binding?.donationDate?.text?.toString(),
                                        lattitude = latLng.latitude,
                                        longitude = latLng.longitude,
                                        donationMethod = selectedDonationType
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MedicineDonation.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MedicineDonation().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}