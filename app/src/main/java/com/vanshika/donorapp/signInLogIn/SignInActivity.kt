package com.vanshika.donorapp.signInLogIn

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.disklrucache.DiskLruCache.Editor
import com.google.firebase.auth.FirebaseAuth
import com.vanshika.donorapp.MainActivity
import com.vanshika.donorapp.R
import com.vanshika.donorapp.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {
    var auth:FirebaseAuth?=null
    var signInBinding:ActivitySignInBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
         signInBinding=ActivitySignInBinding.inflate(layoutInflater)
        setContentView(signInBinding?.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var sharedPreferences =
            getSharedPreferences(resources.getString(R.string.app_name), MODE_PRIVATE)


        if(sharedPreferences?.contains("username") == true && auth?.currentUser!=null) {
                startActivity(
                    Intent(
                        this,
                        MainActivity::class.java
                    )
                )
                finish()
            }

        auth=FirebaseAuth.getInstance()

        signInBinding?.btnSignIn?.setOnClickListener {
            startActivity(Intent(this,LogInActivity::class.java))
            finish()
        }

        signInBinding?.btnRegister?.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
            finish()
        }

    }
}