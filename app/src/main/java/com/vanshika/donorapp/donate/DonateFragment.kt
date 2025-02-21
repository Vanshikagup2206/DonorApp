package com.vanshika.donorapp.donate

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vanshika.donorapp.DonationDatabase
import com.vanshika.donorapp.R
import com.vanshika.donorapp.databinding.FragmentDonateBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DonateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DonateFragment : Fragment(),DonationInterfae {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var binding: FragmentDonateBinding? = null
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
        binding = FragmentDonateBinding.inflate(layoutInflater)
        donationDatabase = DonationDatabase.getInstance(requireContext())

        return binding?.root
        // return inflater.inflate(R.layout.fragment_donate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Toast.makeText(requireContext(), "Choose any category to donate", Toast.LENGTH_LONG).show()
        donationAdapter = DonationAdapter(donation, this)
        linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding?.rvMyDonation?.layoutManager =
            linearLayoutManager
        binding?.rvMyDonation?.adapter = donationAdapter
        getDonationList()
        donationAdapter.notifyDataSetChanged()

        binding?.blood?.setOnClickListener {
            findNavController().navigate(R.id.blood_donation)
        }
        binding?.organ?.setOnClickListener {
            findNavController().navigate(R.id.organ_donation)
        }
        binding?.money?.setOnClickListener {
            findNavController().navigate(R.id.money_Donation)
        }
        binding?.medicine?.setOnClickListener {
            findNavController().navigate(R.id.medicine_donation)
        }
//        binding?.btnDonateNow?.setOnClickListener {
//            findNavController().navigate(R.id.donate_Queries)
//        }
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
         * @return A new instance of fragment DonateFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DonateFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun clickInterface(positive: Int) {
        findNavController().navigate(R.id.donationDetailsFragment)
    }
}