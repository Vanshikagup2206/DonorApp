package com.vanshika.donorapp.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vanshika.donorapp.databinding.FragmentHomeBinding
import com.vanshika.donorapp.requests.RecipientsDataClass
import com.vanshika.donorapp.requests.ActiveRequestAdapter  // Assuming you created this adapter

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private val firestore = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private lateinit var activeRequestsAdapter: ActiveRequestAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadActiveRequests()
    }

    private fun setupRecyclerView() {
        binding?.rvActiveRequests?.layoutManager = LinearLayoutManager(requireContext())
        activeRequestsAdapter = ActiveRequestAdapter(arrayListOf())
        binding?.rvActiveRequests?.adapter = activeRequestsAdapter
    }

    private fun loadActiveRequests() {
        firestore.collection("emergency_requests")
            .get()
            .addOnSuccessListener { documents ->
                val activeRequests = mutableListOf<RecipientsDataClass>()

                for (doc in documents) {
                    val request = doc.toObject(RecipientsDataClass::class.java)
                    // Only show requests by other users
                    if (request.userId != currentUserId) {
                        activeRequests.add(request)
                    }
                }

                activeRequestsAdapter.updateList(activeRequests)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load requests", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
