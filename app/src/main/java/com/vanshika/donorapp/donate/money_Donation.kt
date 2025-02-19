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
import com.vanshika.donorapp.databinding.FragmentMoneyDonationBinding
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [money_Donation.newInstance] factory method to
 * create an instance of this fragment.
 */
class money_Donation : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var binding: FragmentMoneyDonationBinding? = null
    lateinit var donardatabase: DonationDatabase
    var donation = arrayListOf<DonorsDataClass>()

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
        binding?.submitButton?.setOnClickListener {
            if (binding?.editName?.text?.toString().isNullOrEmpty()) {
                binding?.askName?.setError("Your Name")
            } else if (binding?.editAge?.text?.toString().isNullOrEmpty()) {
                binding?.askAge?.setError("Enter Age")
            } else if (binding?.editNumber?.length() != 10) {
                binding?.editNumber?.setError("Enter Number")
            } else if (binding?.editAmount?.text?.toString().isNullOrEmpty()) {
                binding?.editAmount?.setError("enter Amount to donate")
            } else if (binding?.editGender?.text?.toString().isNullOrEmpty()) {
                binding?.editGender?.setError("Enter your gender")
            } else {
                Toast.makeText(
                    requireContext(),
                    "Your Details is Filled Successfuly!",
                    Toast.LENGTH_SHORT
                ).show();
                donardatabase.DonationDao().insertDonor(
                    DonorsDataClass(
                        donorName = binding?.editName?.text?.toString(),
                        age = binding?.editAge?.text?.toString(),
                        number = binding?.editNumber?.text?.toString(),
                        gender = binding?.editGender?.text?.toString(),
                        donationfrequency = binding?.editAmount?.text?.toString(),
                        donationType = "Money",
//                        createddate = Calendar.getInstance().time

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
         * @return A new instance of fragment money_Donation.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            money_Donation().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}