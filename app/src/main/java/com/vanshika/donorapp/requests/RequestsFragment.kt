package com.vanshika.donorapp.requests

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vanshika.donorapp.DonationDatabase
import com.vanshika.donorapp.R
import com.vanshika.donorapp.databinding.FragmentRequestsBinding
import java.text.SimpleDateFormat
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RequestsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RequestsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var binding: FragmentRequestsBinding? = null
    var recipientsDataClass = RecipientsDataClass()
    lateinit var linearLayoutManager: LinearLayoutManager

    var emergencyRequestList = arrayListOf<RecipientsDataClass>()
lateinit var emergencyRequestAdapter: EmergencyRequestAdapter
    lateinit var donationDatabase: DonationDatabase


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
        binding = FragmentRequestsBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        donationDatabase = DonationDatabase.getInstance(requireContext())
        emergencyRequestList() = EmergencyRequestAdapter(emergencyRequestList,this)
        linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding?.rvRecipients?.layoutManager = linearLayoutManager
        binding?.rvRecipients?.adapter = emergencyRequestAdapter
        getEmergencyRequestList()
        binding?.btnEmergencyRequest?.setOnClickListener {
            findNavController().navigate(R.id.emergencyRequestFragment)
        }

    }

    private fun getEmergencyRequestList() {
        emergencyRequestList.clear()
        emergencyRequestList.addAll(donationDatabase.DonationDao().getEmergencyRequestList())
        emergencyRequestAdapter.notifyDataSetChanged()
    }






    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RequestsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RequestsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}