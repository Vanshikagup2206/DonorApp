package com.vanshika.donorapp.SignIn_LogIn

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.vanshika.donorapp.MainActivity
import com.vanshika.donorapp.R
import com.vanshika.donorapp.databinding.ActivityLogInBinding

class LogInActivity : AppCompatActivity() {
    var auth:FirebaseAuth?=null
    var logInBinding: ActivityLogInBinding ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        logInBinding=ActivityLogInBinding.inflate(layoutInflater)
        setContentView(logInBinding?.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth=FirebaseAuth.getInstance()

        logInBinding?.btnLogin?.setOnClickListener {
            val email =logInBinding?.etEmail?.text.toString()
            val password =logInBinding?.etPassword?.text.toString()

            if(email.isNotEmpty()&&password.isNotEmpty()){
                loginUser(email,password)
            }
            else{
                logInBinding?.etEmail?.error="Email can't be empty"
                logInBinding?.etPassword?.error="Password can't be empty"
            }
        }
    }

    private fun loginUser(email:String,password:String){

        auth?.signInWithEmailAndPassword(email,password)?.addOnCompleteListener(this){task ->
                if(task.isSuccessful){
                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }

    }
}