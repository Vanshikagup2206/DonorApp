package com.vanshika.donorapp.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.vanshika.donorapp.R
import com.vanshika.donorapp.databinding.FragmentMapBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import com.google.firebase.firestore.FirebaseFirestore
import com.vanshika.donorapp.donate.DonorsDataClass

class MapFragment : Fragment(), OnMapReadyCallback {
    private var param1: String? = null
    private var param2: String? = null
    private var binding: FragmentMapBinding? = null
    private var mGoogleMap: GoogleMap? = null
    private var fromLatLng: LatLng? = null
    private var toLatLng: LatLng? = null
    private var donorId: Int = 1
    private val waypoints = mutableListOf<LatLng>()

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
        binding = FragmentMapBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding?.btnSearch?.setOnClickListener {
            val fromLocation = binding?.etFromLocation?.text.toString()
            val toLocation = binding?.etToLocation?.text.toString()
            if (fromLocation.isNotEmpty() && toLocation.isNotEmpty()) {
                searchLocation(fromLocation, isFrom = true)
                searchLocation(toLocation, isFrom = false)
            }
        }
    }

    private fun searchLocation(location: String, isFrom: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            val url = "https://nominatim.openstreetmap.org/search?format=json&q=$location"
            val response = fetchLocationData(url)
            withContext(Dispatchers.Main) {
                if (response != null && response.length() > 0) {
                    val locationData = response.getJSONObject(0)
                    val lat = locationData.getDouble("lat")
                    val lon = locationData.getDouble("lon")
                    val latLng = LatLng(lat, lon)
                    Log.d("LocationDebug", "Location: $location, Lat: $lat, Lon: $lon")
                    if (isFrom) {
                        fromLatLng = latLng
                    } else {
                        toLatLng = latLng
                    }

                    mGoogleMap?.addMarker(MarkerOptions().position(latLng).title(location))
                    mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))

                    if (fromLatLng != null && toLatLng != null) {
                        drawRoute()
                    }
                } else {
                    Toast.makeText(requireContext(), "Location not found!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchLocationData(urlString: String): JSONArray? {
        return try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()
            val inputStream = connection.inputStream.bufferedReader().use { it.readText() }
            Log.d("LocationAPI", "Response: $inputStream")
            JSONArray(inputStream).takeIf { it.length() > 0 }
        } catch (e: Exception) {
            Log.e("LocationAPI", "Error fetching location", e)
            null
        }
    }

    private fun drawRoute() {
        if (fromLatLng != null && toLatLng != null) {
            val polylineOptions = PolylineOptions()
                .add(fromLatLng)
                .addAll(waypoints)
                .add(toLatLng)
                .width(8f)
                .color(android.graphics.Color.BLUE)
            mGoogleMap?.addPolyline(polylineOptions)
            zoomOnMap()
        }
    }

    private fun zoomOnMap() {
        val builder = LatLngBounds.Builder()
        fromLatLng?.let { builder.include(it) }
        toLatLng?.let { builder.include(it) }
        waypoints.forEach { builder.include(it) }
        val bounds = builder.build()
        val padding = 100
        mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MapFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        Toast.makeText(requireContext(), "Map Loaded Successfully!", Toast.LENGTH_SHORT).show()
        println("Google Map is ready!")
        loadDonorLocations()
    }

    private fun resizeMapIcon(iconResId: Int, width: Int, height: Int): BitmapDescriptor {
        val imageBitmap = BitmapFactory.decodeResource(resources, iconResId)
        val resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false)
        return BitmapDescriptorFactory.fromBitmap(resizedBitmap)
    }

    // 🔄 Firestore-based donor marker loading
    private fun loadDonorLocations() {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("donations")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirestoreError", "Error fetching donations: ", error)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    mGoogleMap?.clear()
                    for (doc in snapshot.documents) {
                        val lat = doc.getDouble("latitude") ?: continue
                        val lon = doc.getDouble("longitude") ?: continue
                        val donationType = doc.getString("donationType") ?: "Unknown"
                        val location = LatLng(lat, lon)
                        Log.d("FirestoreDonor", "Lat: $lat, Lon: $lon, Type: $donationType")
                        addMarker(DonorsDataClass(lattitude = lat, longitude = lon, donationType = donationType))
                    }
                } else {
                    Log.d("Firestore", "No donor data found in Firestore.")
                }
            }
    }

    private fun addMarker(donor: DonorsDataClass) {
        val location = LatLng(donor.lattitude, donor.longitude)
        val donationType = donor.donationType

        val icon = when (donationType.toString().trim().capitalize()) {
            "Blood" -> R.drawable.blood_download
            "Medicine" -> R.drawable.medicine
            "Money" -> R.drawable.money_download
            "Organ" -> R.drawable.organ_download
            else -> R.drawable.blood_download
        }

        val smallMarkerIcon = resizeMapIcon(icon, width = 50, height = 50)

        mGoogleMap?.addMarker(
            MarkerOptions()
                .position(location)
                .title("${donor.donationType} by ${donor.donorName}")
                .icon(smallMarkerIcon)
        )
    }
}
