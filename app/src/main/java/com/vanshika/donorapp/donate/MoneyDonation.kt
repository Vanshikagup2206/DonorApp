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
import com.vanshika.donorapp.databinding.FragmentMoneyDonationBinding
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
 * Use the [MoneyDonation.newInstance] factory method to
 * create an instance of this fragment.
 */
class MoneyDonation : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var binding: FragmentMoneyDonationBinding? = null
    lateinit var donardatabase: DonationDatabase
    var donation = arrayListOf<DonorsDataClass>()
    var calendar = android.icu.util.Calendar.getInstance()
    var auth: FirebaseAuth? = null
    var db: FirebaseFirestore? = null
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
        donardatabase = DonationDatabase.getInstance(requireContext())
        binding = FragmentMoneyDonationBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.resetCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                resetForm()
                binding?.resetCheckbox?.isChecked = false // Auto uncheck kar dena reset ke baad
            }
        }
        val genderSpinner = binding?.spinGender
        val paymentSpinner = binding?.paymentMethodSpinner
        val paymentAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.payment_methods,
            android.R.layout.simple_spinner_item
        )
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        paymentSpinner?.adapter = paymentAdapter
        val selectedPayment = paymentSpinner?.selectedItem.toString()
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
                binding?.askName?.setError("Your Name")
            } else if (binding?.editAge?.text?.toString().isNullOrEmpty()) {
                binding?.askAge?.setError("Enter Age")
            } else if (binding?.editNumber?.length() != 10) {
                binding?.editNumber?.setError("Enter Number")
            } else if (binding?.editAmount?.text?.toString().isNullOrEmpty()) {
                binding?.editAmount?.setError("enter Amount to donate")
            } else if (selectedGender == "Select Gender") {
                Toast.makeText(
                    requireContext(),
                    "Please select gender",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (binding?.donationDate?.text.isNullOrEmpty()) {
                binding?.donationDate?.error = "Please select a donation date"
                Toast.makeText(requireContext(), "Donation date is required!", Toast.LENGTH_SHORT)
                    .show()
            } else if (selectedPayment == "Select Payment Method") {
                Toast.makeText(
                    requireContext(),
                    "Please select a payment method!",
                    Toast.LENGTH_SHORT
                ).show()

            }
            val selectedRadioButtonId = binding?.anonymousGroup?.checkedRadioButtonId
            if (selectedRadioButtonId == -1) {
                Toast.makeText(
                    requireContext(),
                    "Please select a donation type (Anonymous or Public)!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                builder.setTitle("Check Your Details")
                builder.setMessage("Are you sure your details are correct? This can't be edited later.")
                builder.setPositiveButton("Yes") { _, _ ->
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
                    val address = binding?.editAddr?.text?.toString()
                    if (!address.isNullOrEmpty()) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val latLng = getLatLngFromAddress(address)

                            withContext(Dispatchers.Main) {
                                if (latLng != null) {
                                    donardatabase.DonationDao().insertDonor(
                                        DonorsDataClass(
                                            donorName = binding?.editName?.text?.toString(),
                                            age = binding?.editAge?.text?.toString(),
                                            number = binding?.editNumber?.text?.toString(),
                                            gender = selectedGender,
                                            donationfrequency = binding?.editAmount?.text?.toString(),
                                            donationType = "Money",
                                            createdDate = binding?.donationDate?.text?.toString(),
                                            paymentMethod = selectedPayment,
                                            donationMethod = selectedDonationType,
                                            lattitude = latLng.latitude,
                                            longitude = latLng.longitude,
                                            address = binding?.editAddr?.text?.toString(),
                                        )
                                    )
                                }
                                saveDonationToFirestore(currentUserId = auth?.currentUser.toString(),latLng!!.latitude,latLng.longitude,"Money")
                                findNavController().navigate(R.id.donateFragment)
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

    private fun resetForm() {
        binding?.editName?.setText("")
        binding?.editAge?.setText("")
        binding?.editNumber?.setText("")
        binding?.editAmount?.setText("")
        binding?.donationDate?.setText("")
        binding?.editAddr?.setText("")
        binding?.donationDate?.setText("")
//        // Spinner reset
        binding?.spinGender?.setSelection(0)
        binding?.paymentMethodSpinner?.setSelection(0)
        binding?.anonymousGroup?.clearCheck()
        Toast.makeText(requireContext(), "Form reset successfully!", Toast.LENGTH_SHORT).show()
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
    fun saveDonationToFirestore(
        currentUserId: String,
        donationLat: Double,
        donationLon: Double,
        donationType: String
    ) {
        val firestore = FirebaseFirestore.getInstance()

        val donationData = hashMapOf(
            "donorId" to currentUserId,
            "latitude" to donationLat,
            "longitude" to donationLon,
            "donationType" to donationType
        )

        firestore.collection("donations")
            .add(donationData)
            .addOnSuccessListener {
                Log.d("Firestore", "Donation added successfully!")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error adding donation", e)
            }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MoneyDonation.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MoneyDonation().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}