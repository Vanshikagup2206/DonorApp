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
import com.vanshika.donorapp.databinding.FragmentMedicineDonationBinding
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
        binding?.submitButton?.setOnClickListener {
            if (binding?.editName?.text?.toString().isNullOrEmpty()) {
                binding?.editName?.setError("Enter your Name")
            } else if (binding?.editAmount?.text?.toString().isNullOrEmpty()) {
                binding?.editAmount?.setError("Enter amount")
            } else if (binding?.editMedicineType?.text?.toString().isNullOrEmpty()) {
                binding?.askMedicineType?.setError("Enter type")
            } else if (binding?.editNumber?.length() != 10) {
                binding?.editNumber?.setError("Enter your Name")
            } else if (binding?.editGender?.text?.toString().isNullOrEmpty()) {
                binding?.editGender?.setError("Enter your Name")
            } else if (binding?.editAge?.text?.toString().isNullOrEmpty()) {
                binding?.editAge?.setError("Enter your Name")
            } else {
                Toast.makeText(
                    requireContext(),
                    "Your Details is Filled Successfuly!",
                    Toast.LENGTH_SHORT
                ).show();
                donardatabase.DonationDao().insertDonor(
                    DonorsDataClass(
                        donationType = "Medicine",
                        donorName = binding?.editName?.text?.toString(),
                        age = binding?.editAge?.text?.toString(),
                        donationfrequency = binding?.editAmount?.text?.toString(),
                        gender = binding?.editGender?.text?.toString(),
                        number = binding?.editNumber?.text?.toString(),
                        latitude = 28.6139,
                        longitude = 77.2090
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