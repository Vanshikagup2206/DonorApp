package com.vanshika.donorapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.vanshika.donorapp.signInLogIn.LogInActivity
import com.vanshika.donorapp.signInLogIn.RegisterActivity

class SplashScreen : AppCompatActivity() {
    var sharedPreferences:SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        sharedPreferences = getSharedPreferences(resources.getString(R.string.app_name), MODE_PRIVATE)

        Handler(Looper.getMainLooper()).postDelayed({
            if(sharedPreferences?.contains("username") == true) {
                startActivity(
                    Intent(
                        this,
                        MainActivity::class.java
                    )
                )
                finish()
            }else{
                startActivity(
                    Intent(
                        this,
                        LogInActivity::class.java
                    )
                )
                finish()
            }
        }, 2000)
    }
}