package com.vanshika.donorapp.SignIn_LogIn

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.vanshika.donorapp.R
import com.vanshika.donorapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    var auth:FirebaseAuth ?= null
    var registerBinding:ActivityRegisterBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        registerBinding=ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding?.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        registerBinding?.btnSignup?.setOnClickListener {
            val name = registerBinding?.etName?.text.toString()
            val email = registerBinding?.etEmail?.text.toString()
            val password= registerBinding?.etPassword?.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    registerUser(email, password)
                } else {
                    Toast.makeText(this, "Fill all Fields", Toast.LENGTH_LONG).show()
                }
            }
        }
    private fun registerUser(email:String,password:String){
        auth?.createUserWithEmailAndPassword(email,password)?.addOnCompleteListener(this){task ->
            if(task.isSuccessful){
                Toast.makeText(this,"User Register with ${auth?.currentUser?.email}",
                    Toast.LENGTH_LONG).show()
                startActivity(Intent(this,LogInActivity::class.java))
                finish()
            }else{
                Toast.makeText(
                    this,
                    "SignUp Failed :${task.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }

    }
}
