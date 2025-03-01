package com.vanshika.donorapp

import android.Manifest
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
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
import retrofit2.Callback
import retrofit2.Response

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
            if (token != null && token.isNotEmpty()) {
                val storedToken = getStoredToken()  // Fetch previous token
                if (storedToken != token) {  // ✅ Check if token has changed
                    saveTokenToRoom(token)
                    sendTokenToServer(token)
                    storeTokenLocally(token) // ✅ Save new token to SharedPreferences
                    Log.d("FCM Token", "✅ New token fetched: $token")
                } else {
                    Log.d("FCM Token", "🔹 Token unchanged, not updating.")
                }
            }
        }.addOnFailureListener { e ->
            Log.e("FCM Token", "❌ Error fetching token: ${e.message}")
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

        fetchAndUpdateFCMToken()
        fetchUserTokenFromRoom()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }

    }

    private fun fetchUserTokenFromRoom() {
        val userId = auth?.currentUser?.uid
        CoroutineScope(Dispatchers.IO).launch {
            userId?.let {
                val userToken = donationDatabase.DonationDao().getUserByToken(it)?.fcmToken
                Log.d("RoomDB", "🔹 Retrieved user token: $userToken")
            }
        }
    }

    private fun fetchAndUpdateFCMToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            if (token.isNullOrEmpty()) {
                Log.e("FCM Token", "❌ Failed to fetch token")
                return@addOnSuccessListener
            }

            val storedToken = getStoredToken()
            if (storedToken != token) {  // ✅ Update only if token is changed
                storeTokenLocally(token)
                saveTokenToRoom(token)
                sendTokenToServer(token)
                Log.d("FCM Token", "✅ New token updated: $token")
            } else {
                Log.d("FCM Token", "🔹 Token unchanged, skipping update")
            }
        }.addOnFailureListener { e ->
            Log.e("FCM Token", "❌ Error fetching token: ${e.message}")
        }
    }

    private fun saveTokenToRoom(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val existingUser = donationDatabase.DonationDao().getUserByToken(token)
            if (existingUser == null) {  // ✅ Avoid duplicate inserts
                val user = UsersDataClass(name = "John Doe", fcmToken = token)
                donationDatabase.DonationDao().insertToken(user)
                Log.d("RoomDB", "✅ Token saved to Room Database")
            } else {
                Log.d("RoomDB", "🔹 Token already exists, skipping insert")
            }
        }
    }
    private fun sendTokenToServer(token: String) {
        val request = NotificationRequestDataClass(tokens = listOf(token))

        RetrofitInstance.api.sendToken(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("FCM Token", "✅ Token successfully sent to server")
                } else {
                    Log.e("FCM Token", "❌ Failed to send token: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("FCM Token", "❌ Error sending token: ${t.message}")
            }
        })
    }

    /** ✅ Store Token in SharedPreferences */
    private fun storeTokenLocally(token: String) {
        val sharedPreferences = getSharedPreferences("FCM_PREFS", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("fcm_token", token).apply()
    }

    /** ✅ Get Stored Token from SharedPreferences */
    private fun getStoredToken(): String? {
        val sharedPreferences = getSharedPreferences("FCM_PREFS", Context.MODE_PRIVATE)
        return sharedPreferences.getString("fcm_token", null)
    }

}