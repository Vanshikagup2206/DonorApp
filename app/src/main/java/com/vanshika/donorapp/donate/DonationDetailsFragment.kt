package com.vanshika.donorapp.donate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vanshika.donorapp.DonationDatabase
import com.vanshika.donorapp.R
import com.vanshika.donorapp.databinding.FragmentDonationDetailsBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DonationDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DonationDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var binding: FragmentDonationDetailsBinding? = null
    var donorsDataClass = DonorsDataClass( lattitude = 28.6139,
        longitude = 77.2090 )
    lateinit var donationDatabase: DonationDatabase
    var donorId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            donorId = it.getInt("id", 0)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDonationDetailsBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        donationDatabase = DonationDatabase.getInstance(requireContext())
        getDonorList()
        binding?.donorName?.text = donorsDataClass.donorName
        binding?.donationType?.text = donorsDataClass.donationType
        binding?.donorGender?.text = donorsDataClass.gender
        binding?.donorAddress?.text = donorsDataClass.address
        binding?.donorHealth?.text = booleanToYesNo(donorsDataClass.isHealthy)
        binding?.travelHistory?.text = booleanToYesNo(donorsDataClass.traveledRecently)
        binding?.medicineHistory?.text = booleanToYesNo(donorsDataClass.tookMedication)
        binding?.alcholHistory?.text = booleanToYesNo(donorsDataClass.consumesAlcohol)
        binding?.bloodpressureHistory?.text = booleanToYesNo(donorsDataClass.bloodPressur)
        binding?.diabitiesHistory?.text = booleanToYesNo(donorsDataClass.diabities)
        binding?.surgeryHistory?.text = booleanToYesNo(donorsDataClass.hadRecentSurgery)
        binding?.vaccineHistory?.text = booleanToYesNo(donorsDataClass.tookRecentVaccine)
        binding?.dontionMethod?.text = donorsDataClass.donationMethod
        binding?.paymentMethod?.text = donorsDataClass.paymentMethod


    }

    private fun getDonorList() {
        donorsDataClass = donationDatabase.DonationDao().getDonorById(donorId)
    }

    private fun booleanToYesNo(value: Boolean?): String {
        return if (value == true) "Yes" else "Not Required "
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DonationDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DonationDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}