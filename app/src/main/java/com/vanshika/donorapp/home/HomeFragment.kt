package com.vanshika.donorapp.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.vanshika.donorapp.DonationDatabase
import com.vanshika.donorapp.R
import com.vanshika.donorapp.databinding.FragmentHomeBinding
import com.vanshika.donorapp.notification.FCMApiServiceInterface
import com.vanshika.donorapp.notification.NotificationRequestDataClass
import com.vanshika.donorapp.requests.RecipientsDataClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    var binding: FragmentHomeBinding? = null
    lateinit var linearLayoutManager: LinearLayoutManager
    var emergencyList = arrayListOf<RecipientsDataClass>()
    lateinit var donationDatabase: DonationDatabase
    lateinit var highEmergencyRequestAdapter: HighEmergencyRequestAdapter
    private val SERVER_URL = "http://192.168.175.41:3000/send-notification"

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

        sharedPreferences = requireActivity().getSharedPreferences(
            getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
        editor = sharedPreferences.edit()

        binding?.tvUsername?.setText(sharedPreferences.getString("name", ""))

        donationDatabase = DonationDatabase.getInstance(requireContext())
        highEmergencyRequestAdapter = HighEmergencyRequestAdapter(emergencyList)
        linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

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

    private fun sendSOSNotification() {
        val queue: RequestQueue = Volley.newRequestQueue(requireContext())

        val requestBody = JSONObject()
        try {
            val tokensArray = JSONArray().apply {
                put("fH0t20VgRDeh8YadLXnurj:APA91bFpU2qNT-ugwX5dCp8XBVlWbYXFTI7VodS6cyCeA0j3mfCOMqnzbvTeldFpqenocKJt9ArBCkb7YzKRy2Y3jmsKFyRl3LRFiBqgizes_8YPjvZoUj8")
                put("eJ4mX18URmunIH-LoD28Ya:APA91bHYBdzWYYS0dQklJOIuK8O45CjdvhHUV8FbzyRYOCLBsXzndkIx4NY3BJOvzwSlHNC7TqQQ3RnHOQyQaiVnNRFkLj6O2vtt37EErgd0nvPnai-OI98")
            }

            requestBody.put("tokens", tokensArray)
            requestBody.put("title", "🚨 SOS Alert!")
            requestBody.put("body", "Urgent help needed! Click to respond.")

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST,
            SERVER_URL,
            requestBody,
            { response ->
                Toast.makeText(requireContext(), "✅ Notification Sent!", Toast.LENGTH_SHORT).show()
            },
            { error ->
                val statusCode = error.networkResponse?.statusCode ?: "No Status Code"
                val responseData = error.networkResponse?.data?.let { String(it) } ?: "No Response Data"
                Log.e("FCM_ERROR", "Status Code: $statusCode")
                Log.e("FCM_ERROR", "Response Data: $responseData")
                Log.e("FCM_ERROR", "Error Message: ${error.message}")
                Toast.makeText(requireContext(), "❌ Error: $statusCode", Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(jsonObjectRequest)
    }

//    private fun sendSOSNotification() {
//        CoroutineScope(Dispatchers.IO).launch {
//            val users = donationDatabase.DonationDao().getAllUsers()
//            val tokens = users.mapNotNull { it.fcmToken }
//
//            if (tokens.isEmpty()) {
//                Log.e("FCM", "❌ No tokens found!")
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(
//                        requireContext(),
//                        "No registered users to notify!",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//                return@launch
//            }
//
//            val request = NotificationRequestDataClass(
//                tokens = tokens, title = "🚨 SOS Alert!",
//                body = "Urgent help needed!"
//            )
//
//            val retrofit = Retrofit.Builder()
//                .baseUrl("http://192.168.43.185:3000/") // ⚠️ Replace with your actual server IP
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//
//            val apiService: FCMApiServiceInterface =
//                retrofit.create(FCMApiServiceInterface::class.java)
//
//            apiService.sendNotification(request).enqueue(object : retrofit2.Callback<ResponseBody> {
//                override fun onResponse(
//                    call: retrofit2.Call<ResponseBody>,
//                    response: retrofit2.Response<ResponseBody>
//                ) {
//                    if (response.isSuccessful) {
//                        Log.d("FCM", "✅ Notification Sent Successfully")
//                        Toast.makeText(requireContext(), "SOS Sent!", Toast.LENGTH_SHORT).show()
//                    } else {
//                        Log.e(
//                            "FCM",
//                            "❌ Failed to Send Notification: ${response.errorBody()?.string()}"
//                        )
//                    }
//                }
//
//                override fun onFailure(call: retrofit2.Call<ResponseBody>, t: Throwable) {
//                    Log.e("FCM", "❌ Error: ${t.message}")
//                }
//            })
//        }
//    }


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