package com.vanshika.donorapp.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.vanshika.donorapp.DonationDatabase
import com.vanshika.donorapp.R
import com.vanshika.donorapp.databinding.FragmentHomeBinding
import com.vanshika.donorapp.notification.FCMApiServiceInterface
import com.vanshika.donorapp.notification.NotificationRequestDataClass
import com.vanshika.donorapp.requests.RecipientsDataClass
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

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

        sharedPreferences=requireActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
        editor = sharedPreferences?.edit()

        binding?.tvUsername?.setText(sharedPreferences?.getString("name", ""))

        donationDatabase = DonationDatabase.getInstance(requireContext())
        highEmergencyRequestAdapter = HighEmergencyRequestAdapter(emergencyList)
        linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding?.rvRequests?.adapter = highEmergencyRequestAdapter
        binding?.rvRequests?.layoutManager = linearLayoutManager

        binding?.btnHigh?.setOnClickListener {
            getHighEmergencyList()
        }
        binding?.btnMedium?.setOnClickListener {
            getMediumEmergencyList()
        }

        binding?.btnLow?.setOnClickListener {
            getLowEmergencyList()
        }

        binding?.btnSos?.setOnClickListener {
            sendSOSNotification()
        }
    }

//    private fun sendSOSNotification() {
//        val url = "http://192.168.43.185:3000/sendNotification" // Replace with your actual server URL
//
//        val client = OkHttpClient()
//        val requestBody = "{}".toRequestBody("application/json".toMediaTypeOrNull())
//
//        val request = Request.Builder()
//            .url(url)
//            .post(requestBody)
//            .build()
//
//        client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                Log.e("SOS", "Failed to send SOS notification: ${e.message}")
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                Log.d("SOS", "Notification Sent: ${response.body?.string()}")
//            }
//        })
//    }

    private fun sendSOSNotification() {
        val tokens = listOf("token1", "token2", "token3") // Add real FCM tokens

        val request = NotificationRequestDataClass(tokens = tokens, title = "🚨 SOS Alert!",
            body = "Urgent help needed!"
        )

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.43.185:3000/") // ⚠️ Replace with your actual server IP
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService: FCMApiServiceInterface = retrofit.create(FCMApiServiceInterface::class.java)

        apiService.sendNotification(request).enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(
                call: retrofit2.Call<ResponseBody>,
                response: retrofit2.Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Log.d("FCM", "✅ Notification Sent Successfully")
                    Toast.makeText(requireContext(), "SOS Sent!", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("FCM", "❌ Failed to Send Notification: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<ResponseBody>, t: Throwable) {
                Log.e("FCM", "❌ Error: ${t.message}")
            }
        })
    }



    private fun getHighEmergencyList() {
        emergencyList.clear()
        emergencyList.addAll(donationDatabase.DonationDao().getHighEmergencyList(3))
        highEmergencyRequestAdapter.notifyDataSetChanged()
    }

    private fun getMediumEmergencyList() {
        emergencyList.clear()
        emergencyList.addAll(donationDatabase.DonationDao().getHighEmergencyList(2))
        highEmergencyRequestAdapter.notifyDataSetChanged()
    }

    private fun getLowEmergencyList() {
        emergencyList.clear()
        emergencyList.addAll(donationDatabase.DonationDao().getHighEmergencyList(1))
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