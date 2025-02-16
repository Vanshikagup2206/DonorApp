package com.vanshika.donorapp.signInLogIn

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vanshika.donorapp.MainActivity
import com.vanshika.donorapp.R
import com.vanshika.donorapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    var auth:FirebaseAuth ?= null
    var registerBinding:ActivityRegisterBinding?=null
    var sharedPreferences: SharedPreferences?= null
    var editor: SharedPreferences.Editor?=null
    private var fireStore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding?.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("R.string.app_name", MODE_PRIVATE)
        editor = sharedPreferences?.edit()

        registerBinding?.btnSignup?.setOnClickListener {
            val name = registerBinding?.etName?.text.toString()
            val email = registerBinding?.etEmail?.text.toString()
            val password= registerBinding?.etPassword?.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                registerUser(name, email, password)
//                registerUser(email, password)
//                editor?.putString("username",name)
//                editor?.putString("email",email)
//                editor?.apply()
                } else {
                    Toast.makeText(this, "Fill all Fields", Toast.LENGTH_LONG).show()
                }
            }
        }

    private fun registerUser(name: String, email: String, password: String) {
        auth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener(this){ task ->
            if (task.isSuccessful){
                val userId = auth?.currentUser?.uid ?: return@addOnCompleteListener
                var qrData = generateUniqueQRData(userId, email)
                // Store user data in Firestore
                val userMap = hashMapOf(
                    "userId" to userId,
                    "name" to name,
                    "email" to email,
                    "qrData" to qrData
                )
                fireStore.collection("users").document(userId).set(userMap)
                    .addOnSuccessListener {
                        Toast.makeText(this, "User Registered Successfully!", Toast.LENGTH_SHORT).show()
                        // Save to shared preferences
                        editor?.putString("userId",userId)
                        editor?.putString("name", name)
                        editor?.putString("email", email)
                        editor?.putString("qrData", qrData)
                        editor?.apply()
                        Toast.makeText(this, "Registration complete!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to store user data", Toast.LENGTH_SHORT).show()
                    }
            } else{
                Toast.makeText(this, "SignUp Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateUniqueQRData(userId: String, email: String): String {
        val timestamp = System.currentTimeMillis()
        return "UserId:$userId | Email:$email | Timestamp:$timestamp"
    }
//    private fun registerUser(email:String,password:String){
//        auth?.createUserWithEmailAndPassword(email,password)?.addOnCompleteListener(this){task ->
//            if(task.isSuccessful){
//                Toast.makeText(this,"User Register with ${auth?.currentUser?.email}",
//                    Toast.LENGTH_LONG).show()
//                startActivity(Intent(this,LogInActivity::class.java))
//                finish()
//            }else{
//                Toast.makeText(
//                    this,
//                    "SignUp Failed :${task.exception?.message}",
//                    Toast.LENGTH_SHORT
//                ).show()
//
//            }
//        }
//
//    }
}
