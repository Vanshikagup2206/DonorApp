package com.vanshika.donorapp.donate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.vanshika.donorapp.DonationDatabase
import com.vanshika.donorapp.R
import com.vanshika.donorapp.databinding.FragmentBloodDonationBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Blood_donation.newInstance] factory method to
 * create an instance of this fragment.
 */
class Blood_donation : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var binding: FragmentBloodDonationBinding? = null
    var bloodDonation = arrayListOf<DonorsDataClass>()
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
        binding?.submitButton?.setOnClickListener {
            if (binding?.nameEditText?.text.toString().isNullOrEmpty()) {
                binding?.nameEditText?.setError("Fill Your Name")
            } else if (binding?.ageEditText?.text.toString().isNullOrEmpty()) {
                binding?.ageEditText?.setError("Fill Your Age")
            } else if (binding?.addrEditText?.text?.toString().isNullOrEmpty()) {
                binding?.addrEditText?.setError("Fill Your Age")

            } else if (binding?.genderEdittext?.text.toString().isNullOrEmpty()) {
                binding?.genderEdittext?.setError("Your Gender?")
            } else if (binding?.contactEditText?.text.toString().isNullOrEmpty()) {
                binding?.contactEditText?.setError("Enter Your Mobile Number")
            } else if (binding?.contactEditText?.length() != 10) {
                binding?.contactEditText?.setError("Enter Your 10 digit Number")

            } else if (binding?.donationFrequencyEditText?.text.toString().isNullOrEmpty()) {
                binding?.donationFrequencyEditText?.setError("Fill the frequency")
            } else {
                Toast.makeText(
                    requireContext(),
                    "Your Details is Filled Successfuly!",
                    Toast.LENGTH_SHORT
                ).show();

                donorDatabase.DonationDao().insertDonor(
                    DonorsDataClass(
                        donorId = id,
                        donorName = binding?.nameEditText?.text?.toString(),
                        address = binding?.addrEditText?.text?.toString(),
                        age = binding?.ageEditText?.text?.toString(),
                        gender = binding?.genderEdittext?.text?.toString(),
                        number = binding?.contactEditText?.text?.toString(),
                        bloodType = binding?.bloodGroupEditText?.text.toString(),
                        donationfrequency = binding?.donationFrequencyEditText?.text?.toString(),
                        donationType = "Blood"

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
         * @return A new instance of fragment Blood_donation.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Blood_donation().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}