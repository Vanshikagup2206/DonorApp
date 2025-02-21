package com.vanshika.donorapp.signInLogIn

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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
    var auth: FirebaseAuth? = null
    var db: FirebaseFirestore ?=null
    var registerBinding: ActivityRegisterBinding? = null
    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null


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
        db = FirebaseFirestore.getInstance()
        sharedPreferences = getSharedPreferences("R.string.app_name", MODE_PRIVATE)
        editor = sharedPreferences?.edit()

        registerBinding?.tvLogin?.setOnClickListener {
            startActivity(Intent(this, LogInActivity::class.java))
            finish()

        }

        registerBinding?.btnSignup?.setOnClickListener {
            val name = registerBinding?.etName?.text.toString()
            val email = registerBinding?.etEmail?.text.toString()
            val password = registerBinding?.etPassword?.text.toString()
            val aadhaarNumber = registerBinding?.etAadhaarNo?.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && aadhaarNumber.isNotEmpty()) {
                if (isValidAadhaar(aadhaarNumber)){
                    registerUser(name, email, password, aadhaarNumber)
                } else{
                    Toast.makeText(this, "Invalid Aadhaar Number!", Toast.LENGTH_SHORT).show()
                }
//                registerUser(email, password)
//                editor?.putString("username",name)
//                editor?.putString("email",email)
//                editor?.apply()
            } else {
                Toast.makeText(this, "Fill all Fields", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun isValidAadhaar(aadhaarNumber: String): Boolean {
        if (aadhaarNumber.length != 12 || !aadhaarNumber.all { it.isDigit() }){
            return false
        }
        return verHoeffValidate(aadhaarNumber)
    }

    private fun registerUser(name: String, email: String, password: String, aadhaarNumber: String) {
        auth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val userId = auth?.currentUser?.uid ?: return@addOnCompleteListener
                var qrData = generateUniqueQRData(userId, email, aadhaarNumber)
                // Store user data in Fire store
                val userMap = hashMapOf(
                    "userId" to userId,
                    "name" to name,
                    "email" to email,
                    "aadhaarNumber" to aadhaarNumber,
                    "qrData" to qrData
                )
                db?.collection("users")?.document(userId)?.set(userMap)
                    ?.addOnSuccessListener {
                        // Save to shared preferences
                        editor?.putString("userId", userId)
                        editor?.putString("name", name)
                        editor?.putString("email", email)
                        editor?.putString("aadhaarNumber", aadhaarNumber)
                        editor?.putString("qrData", qrData)
                        editor?.apply()
                        Toast.makeText(this, "Registration complete!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LogInActivity::class.java))
                        finish()
                    }
                    ?.addOnFailureListener {e->
                        Log.e("Firestore", "Error saving user data", e)

                    }
            } else {
                Toast.makeText(
                    this,
                    "SignUp Failed: ${task.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun generateUniqueQRData(userId: String, email: String, aadhaarNumber: String): String {
        val timestamp = System.currentTimeMillis()
        return "UserId:$userId | Aadhaar:$aadhaarNumber | Email:$email | Timestamp:$timestamp"
    }

    private val multiplicationTable = arrayOf(
        intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
        intArrayOf(1, 2, 3, 4, 0, 6, 7, 8, 9, 5),
        intArrayOf(2, 3, 4, 0, 1, 7, 8, 9, 5, 6),
        intArrayOf(3, 4, 0, 1, 2, 8, 9, 5, 6, 7),
        intArrayOf(4, 0, 1, 2, 3, 9, 5, 6, 7, 8),
        intArrayOf(5, 9, 8, 7, 6, 0, 4, 3, 2, 1),
        intArrayOf(6, 5, 9, 8, 7, 1, 0, 4, 3, 2),
        intArrayOf(7, 6, 5, 9, 8, 2, 1, 0, 4, 3),
        intArrayOf(8, 7, 6, 5, 9, 3, 2, 1, 0, 4),
        intArrayOf(9, 8, 7, 6, 5, 4, 3, 2, 1, 0)
    )

    private val permutationTable = arrayOf(
        intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
        intArrayOf(1, 5, 7, 6, 2, 8, 3, 0, 9, 4),
        intArrayOf(5, 8, 0, 3, 7, 9, 6, 1, 4, 2),
        intArrayOf(8, 9, 1, 6, 0, 4, 3, 5, 2, 7),
        intArrayOf(9, 4, 5, 3, 1, 2, 6, 8, 7, 0),
        intArrayOf(4, 2, 8, 6, 5, 7, 3, 9, 0, 1),
        intArrayOf(2, 7, 9, 3, 8, 0, 6, 4, 1, 5),
        intArrayOf(7, 0, 4, 6, 9, 1, 3, 2, 5, 8)
    )

    private val inverseTable = intArrayOf(0, 4, 3, 2, 1, 5, 6, 7, 8, 9)

    private fun verHoeffValidate(num: String): Boolean {
        var check = 0
        val numArray = num.reversed().map { it - '0' }

        for (i in numArray.indices) {
            check = multiplicationTable[check][permutationTable[i % 8][numArray[i]]]
        }
        return check == 0
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
