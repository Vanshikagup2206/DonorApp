package com.vanshika.donorapp

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.google.firebase.auth.FirebaseAuth
import com.vanshika.donorapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    var binding : ActivityMainBinding ?= null
    var navController : NavController ?= null
    var appBarConfiguration : AppBarConfiguration ?= null
    var auth :FirebaseAuth?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        navController = findNavController(R.id.host)
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        binding?.bottomNavigation?.setOnItemSelectedListener {
            when(it.itemId){
                R.id.navHome -> navController?.navigate(R.id.homeFragment)
                R.id.navDonate -> navController?.navigate(R.id.donateFragment)
                R.id.navRequests -> navController?.navigate(R.id.requestsFragment)
                R.id.navMap -> navController?.navigate(R.id.mapFragment)
                R.id.navProfile -> navController?.navigate(R.id.profileFragment)
            }
            return@setOnItemSelectedListener true
        }
        auth=FirebaseAuth.getInstance()
    }
}