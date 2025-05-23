package com.vanshika.donorapp.profile

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.vanshika.donorapp.DonationDatabase
import com.vanshika.donorapp.databinding.DeleteDailogBinding
import com.vanshika.donorapp.databinding.FragmentProfileBinding
import com.vanshika.donorapp.signInLogIn.LogInActivity
import com.vanshika.donorapp.signInLogIn.RegisterActivity
import kotlin.math.absoluteValue


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ProfileFragment : Fragment() {
    var auth: FirebaseAuth? = null
    var binding: FragmentProfileBinding? = null
    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    private var navController: NavController? = null
    var healthRecordsDataClass: HealthRecordsDataClass? = null
    var healthRecords = arrayListOf<HealthRecordsDataClass>()
    var donationDatabase: DonationDatabase? = null
    private var fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var param1: String? = null
    private var param2: String? = null

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
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()
        donationDatabase = DonationDatabase.getInstance(requireContext())
        sharedPreferences = requireActivity().getSharedPreferences(
            "R.string.app_name",
            AppCompatActivity.MODE_PRIVATE
        )
        editor = sharedPreferences?.edit()

        val currentUser = auth?.currentUser
        if (currentUser != null) {
            generateQRCode(currentUser.email ?: "Unknown")
            loadHealthDetails()
        }

        binding?.btnLogout?.setOnClickListener {
            auth?.signOut()
            startActivity(Intent(requireContext(), LogInActivity::class.java))
            Toast.makeText(requireContext(), "User Signed Out", Toast.LENGTH_LONG).show()
        }

        binding?.btnEditProfile?.setOnClickListener {
            Dialog(requireContext()).apply {
                var dialogBinding = DeleteDailogBinding.inflate(layoutInflater)
                setContentView(dialogBinding.root)
                window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                dialogBinding.etProfileMail.setText(auth?.currentUser?.email.toString())
                dialogBinding.etProfileName.setText(sharedPreferences?.getString("name", ""))
                show()
                dialogBinding.imgConfirm.setOnClickListener {
                    if (dialogBinding.etProfileName.text.toString()
                            .isNotEmpty() && dialogBinding.etProfileName.text.toString()
                            .isNotEmpty()
                    ) {
                        editor?.putString("name", dialogBinding.etProfileName.text.toString())
                        editor?.putString("email", dialogBinding.etProfileMail.text.toString())
                        val user = FirebaseAuth.getInstance().currentUser
                        user?.delete()
                            ?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("FirebaseAuth", "User account deleted successfully.")
                                    dismiss()
                                    startActivity(
                                        Intent(
                                            requireContext(),
                                            RegisterActivity::class.java
                                        )
                                    )
                                    requireActivity().finish()

                                } else {
                                    Log.e("FirebaseAuth", "Error deleting user", task.exception)
                                    dismiss()
                                }
                            }
                    } else {
                        Toast.makeText(requireContext(), "Fields can't be empty", Toast.LENGTH_LONG)
                            .show()
                    }
                }
                dialogBinding?.imgDiscard?.setOnClickListener {
                    dismiss()
                }
            }
        }

        binding?.btnAddHealthDetails?.setOnClickListener {
            val bloodGroup = binding?.etBloodGroup?.text.toString()
            val donationStreak = binding?.etDonationStreak?.text.toString()
            val age = binding?.etAge?.text.toString()
            val weight = binding?.etWeight?.text.toString()
            val hemoglobin = binding?.etHemoglobin?.text.toString()
            val bloodPressure = binding?.etBloodPressure?.text.toString()
            val pulserate = binding?.etPulserate?.text.toString()


            if (bloodGroup.isNotEmpty() || donationStreak.isNotEmpty() || age.isNotEmpty() || weight.isNotEmpty()
                || hemoglobin.isNotEmpty() || bloodPressure.isNotEmpty() || pulserate.isNotEmpty()
            ) {

                donationDatabase?.DonationDao()?.insertHealthRecords(
                    HealthRecordsDataClass(
                        donorId = (auth?.currentUser?.uid?.hashCode() ?: 0).absoluteValue,
                        donorBloodGroup = bloodGroup,
                        donorDonationStreak = donationStreak,
                        donorHemoglobin = hemoglobin,
                        donorBp = bloodPressure,
                        donorPulse = pulserate,
                        donorWeight = weight
                    )
                )

                val userMap = hashMapOf(
                    "bloodGroup" to bloodGroup,
                    "donationStreak" to donationStreak,
                    "age" to age,
                    "weight" to weight,
                    "hemoglobin" to hemoglobin,
                    "bloodPressure" to bloodPressure,
                    "pulserate" to pulserate
                )
                fireStore.collection("users").document(auth?.currentUser?.uid.toString())
                    .set(userMap)
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Health details added successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error saving user data", e)

                    }
            }
        }
    }

    private fun loadHealthDetails() {
        val userId = auth?.currentUser?.uid ?: return  // Get current user ID
        fireStore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userEmail = auth?.currentUser?.email ?: ""
                    val bloodGroup = document.getString("bloodGroup") ?: ""
                    binding?.etBloodGroup?.setText(bloodGroup)

                    val donationStreak = document.getString("donationStreak") ?: ""
                    binding?.etDonationStreak?.setText(donationStreak)

                    val age = document.getString("age") ?: ""
                    binding?.etAge?.setText(age)

                    val weight = document.getString("weight") ?: ""
                    binding?.etWeight?.setText(weight)

                    val hemoglobin = document.getString("hemoglobin") ?: ""
                    binding?.etHemoglobin?.setText(hemoglobin)

                    val bloodPressure = document.getString("bloodPressure") ?: ""
                    binding?.etBloodPressure?.setText(bloodPressure)

                    val pulseRate = document.getString("pulserate") ?: ""
                    binding?.etPulserate?.setText(pulseRate)

                    val qrData = """
                        Email: $userEmail
                        Blood Group: $bloodGroup
                        Donation Streak: $donationStreak
                        Age: $age
                        Weight: $weight
                        Hemoglobin: $hemoglobin
                        Blood Pressure: $bloodPressure
                        Pulse Rate: $pulseRate
                    """.trimIndent()

                    generateQRCode(qrData)
                    Log.d("Firestore", "Health details loaded successfully")
                } else {
                    Log.d("Firestore", "No health details found for this user")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error loading health details", e)
            }
    }

    private fun generateQRCode(data: String) {
        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitMatrix: BitMatrix =
                barcodeEncoder.encode(data.toString(), BarcodeFormat.QR_CODE, 400, 400)
            val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
            binding?.ivQrCode?.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            Log.e("QRCode", "Error generating QR Code", e)
            Toast.makeText(requireContext(), "Error generating QR Code", Toast.LENGTH_SHORT).show()
        }

    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}