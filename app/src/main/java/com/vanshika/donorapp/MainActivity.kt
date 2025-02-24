package com.vanshika.donorapp

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.vanshika.donorapp.databinding.ActivityMainBinding
import com.vanshika.donorapp.notification.RetrofitInstance
import com.vanshika.donorapp.notification.NotificationRequestDataClass
import com.vanshika.donorapp.notification.UsersDataClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call

class MainActivity : AppCompatActivity() {
    var binding: ActivityMainBinding? = null
    var navController: NavController? = null
    var appBarConfiguration: AppBarConfiguration? = null
    var auth: FirebaseAuth? = null
    lateinit var donationDatabase: DonationDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        donationDatabase = DonationDatabase.getInstance(this)

        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            saveTokenToRoom(token)
            sendTokenToServer(token)
            Log.d("FCM Token", token)
            Log.d("FCM Token", "Fetched successfully: $token")
        }
            .addOnFailureListener { e ->
                Log.e("FCM Token", "Error fetching token: ${e.message}")
            }

        navController = findNavController(R.id.host)
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        binding?.bottomNavigation?.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navHome -> navController?.navigate(R.id.homeFragment)
                R.id.navDonate -> navController?.navigate(R.id.donateFragment)
                R.id.navRequests -> navController?.navigate(R.id.requestsFragment)
                R.id.navMap -> navController?.navigate(R.id.mapFragment)
                R.id.navProfile -> navController?.navigate(R.id.profileFragment)
            }
            return@setOnItemSelectedListener true
        }
        auth = FirebaseAuth.getInstance()
    }

    private fun saveTokenToRoom(token: String?) {
        if (token == null) return
        val user =
            UsersDataClass(name = "John Doe", fcmToken = token)  // Replace with actual user data

        CoroutineScope(Dispatchers.IO).launch {
            donationDatabase.DonationDao().insertToken(user)
            Log.d("RoomDB", "✅ Token saved to Room Database")// Save token in Room database
        }
    }
    fun sendTokenToServer(token: String) {
        val request = NotificationRequestDataClass(listOf(token))
        val call = RetrofitInstance.api.sendToken(request)

        call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: Call<Void>, response: retrofit2.Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("FCM Token", "Token successfully sent to server")
                } else {
                    Log.e("FCM Token", "Failed to send token: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("FCM Token", "Error sending token: ${t.message}")
            }
        })
    }

}