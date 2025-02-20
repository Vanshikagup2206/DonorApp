package com.vanshika.donorapp.home

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.vanshika.donorapp.DonationDatabase
import com.vanshika.donorapp.R
import com.vanshika.donorapp.databinding.FragmentHomeBinding
import com.vanshika.donorapp.requests.RecipientsDataClass

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var sharedPreferences:SharedPreferences?=null
    var editor:SharedPreferences.Editor?=null
    var binding:FragmentHomeBinding?= null
    lateinit var linearLayoutManager: LinearLayoutManager
    var emergencyList = arrayListOf<RecipientsDataClass>()
    lateinit var donationDatabase: DonationDatabase
    lateinit var highEmergencyRequestAdapter: HighEmergencyRequestAdapter

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
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences=requireActivity().getSharedPreferences("R.string.app_name", AppCompatActivity.MODE_PRIVATE)
        editor=sharedPreferences?.edit()

        binding?.tvUsername?.setText("Hi! ${sharedPreferences?.getString("username", "")}")

        donationDatabase = DonationDatabase.getInstance(requireContext())
        highEmergencyRequestAdapter = HighEmergencyRequestAdapter(emergencyList)
        linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding?.rvRequests?.adapter = highEmergencyRequestAdapter
        binding?.rvRequests?.layoutManager = linearLayoutManager
        getHighEmergencyList()
    }

    private fun getHighEmergencyList() {
        emergencyList.clear()
        emergencyList.addAll(donationDatabase.DonationDao().getHighEmergencyList(3))
        highEmergencyRequestAdapter.notifyDataSetChanged()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}