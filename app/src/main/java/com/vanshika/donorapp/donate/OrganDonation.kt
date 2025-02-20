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
import com.vanshika.donorapp.databinding.FragmentOrganDonationBinding

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
        binding?.submitButton?.setOnClickListener {
            if (binding?.nameEditText?.text?.toString().isNullOrEmpty()) {
                binding?.nameEditText?.setError("Your Name")
            } else if (binding?.ageEditText?.text?.toString().isNullOrEmpty()) {
                binding?.ageEditText?.setError("Enter age")
            } else if (binding?.numberEditText?.length() != 10) {
                binding?.numberEditText?.setError("Enter 10 digit number")
            } else if (binding?.GenderEditText?.text?.toString().isNullOrEmpty()) {
                binding?.GenderEditText?.setError("Enter gender")
            } else if (binding?.consentEditText?.text?.toString().isNullOrEmpty()) {
                binding?.consentEditText?.setError("Enter Your Wish")
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
                        gender = binding?.GenderEditText?.text.toString(),
                        number = binding?.numberEditText?.text?.toString(),
                        donationType = "Organ",
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