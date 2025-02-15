package com.vanshika.donorapp.signInLogIn

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
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