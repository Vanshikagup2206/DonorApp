package com.vanshika.donorapp.donate

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.vanshika.donorapp.DonationDatabase
import com.vanshika.donorapp.R
import com.vanshika.donorapp.databinding.FragmentMyDonationBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyDonation.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyDonation : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var binding: FragmentMyDonationBinding? = null
    lateinit var donationDatabase: DonationDatabase
    lateinit var donationAdapter: DonationAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
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
        binding = FragmentMyDonationBinding.inflate(layoutInflater)
//        binding?.rvMyDonation?.layoutManager = linearLayoutManager

        return inflater.inflate(R.layout.fragment_my_donation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        donationDatabase = DonationDatabase.getInstance(requireContext())
        donationAdapter = DonationAdapter(donation)
        linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding?.rvMyDonation?.layoutManager =
            linearLayoutManager  // Missing line to set layout manager

        binding?.rvMyDonation?.adapter = donationAdapter
        getDonationList()
        donationAdapter.notifyDataSetChanged()
        getDonationList()


    }

    private fun getDonationList() {
        donation.clear()
        donation.addAll(donationDatabase.DonationDao().getDonatonList())
        Log.d("MyDonation", "Donation List: $donation") // Log the donation list
        donationAdapter.notifyDataSetChanged()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyDonation.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyDonation().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}