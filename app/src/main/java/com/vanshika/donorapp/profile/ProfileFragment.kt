package com.vanshika.donorapp.profile

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.vanshika.donorapp.R
import com.vanshika.donorapp.databinding.DeleteAccDailogBinding
import com.vanshika.donorapp.databinding.FragmentProfileBinding
import com.vanshika.donorapp.signInLogIn.RegisterActivity
import com.vanshika.donorapp.signInLogIn.SignInActivity

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ProfileFragment : Fragment() {
    var auth:FirebaseAuth?=null
    var binding:FragmentProfileBinding?=null
    var sharedPreferences:SharedPreferences?=null
    var editor:SharedPreferences.Editor?=null

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentProfileBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences("R.string.app_name",AppCompatActivity.MODE_PRIVATE)
        editor = sharedPreferences?.edit()


        binding?.btnLogout?.setOnClickListener {
            auth?.signOut()
            startActivity(Intent(requireContext(),SignInActivity::class.java))
            Toast.makeText(requireContext(),"User Signed Out",Toast.LENGTH_LONG).show()
        }

        binding?.btnEditProfile?.setOnClickListener {
            Dialog(requireContext()).apply {
                var dialogBinding = DeleteAccDailogBinding.inflate(layoutInflater)
                setContentView(dialogBinding.root)
                window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                dialogBinding.etProfileMail.setText(auth?.currentUser?.email.toString())
                dialogBinding.etProfileName.setText(sharedPreferences?.getString("username",""))
                show()
                dialogBinding.imgConfirm.setOnClickListener{
                    if(dialogBinding.etProfileName.text.toString().isNotEmpty() && dialogBinding.etProfileName.text.toString().isNotEmpty()){
                        editor?.putString("username",dialogBinding.etProfileName.text.toString())
                        editor?.putString("email",dialogBinding.etProfileMail.text.toString())
                        val user = FirebaseAuth.getInstance().currentUser
                        user?.delete()
                            ?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("FirebaseAuth", "User account deleted successfully.")
                                    dismiss()
                                    startActivity(Intent(requireContext(),RegisterActivity::class.java))
                                    requireActivity().finish()

                                } else {
                                    Log.e("FirebaseAuth", "Error deleting user", task.exception)
                                    dismiss()
                                }
                            }
                    }else{
                        Toast.makeText(requireContext(),"Fields can't be empty",Toast.LENGTH_LONG).show()
                    }
                }
                dialogBinding?.imgDiscard?.setOnClickListener{
                    dismiss()
                }

            }


        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}