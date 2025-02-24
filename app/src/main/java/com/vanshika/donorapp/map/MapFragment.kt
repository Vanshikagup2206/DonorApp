package com.vanshika.donorapp.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
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
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.vanshika.donorapp.DonationDatabase
import com.vanshika.donorapp.R
import com.vanshika.donorapp.databinding.FragmentMapBinding
import com.vanshika.donorapp.donate.DonorsDataClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment(), OnMapReadyCallback {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var binding: FragmentMapBinding? = null
    private var mGoogleMap: GoogleMap? = null
    private var fromLatLng: LatLng? = null
    private var toLatLng: LatLng? = null
    private var donorId: Int = 1 // Replace with actual donor ID

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
        // Inflate the layout for this fragment
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
                if (response != null) {
                    if (response.length() > 0) {
                        val locationData = response.getJSONObject(0)
                        val lat = locationData.getDouble("lat").toDouble()
                        val lon = locationData.getDouble("lon").toDouble()
                        val latLng = LatLng(lat, lon)

                        if (isFrom) {
                            fromLatLng = latLng
                        } else {
                            toLatLng = latLng
                        }

                        mGoogleMap?.addMarker(MarkerOptions().position(latLng).title(location))

                        if (fromLatLng != null && toLatLng != null) {
                            drawRoute()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Location not found!", Toast.LENGTH_SHORT)
                            .show()
                    }
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
            JSONArray(inputStream).takeIf { it.length() > 0 }
        } catch (e: Exception) {
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
        val builder = com.google.android.gms.maps.model.LatLngBounds.Builder()
        fromLatLng?.let { builder.include(it) }
        toLatLng?.let { builder.include(it) }
        waypoints.forEach { builder.include(it) }
        val bounds = builder.build()
        val padding = 100
        mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MapFragment.
         */
        // TODO: Rename and change types and number of parameters
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

    private fun loadDonorLocations() {
        CoroutineScope(Dispatchers.IO).launch {
            val donors = DonationDatabase.getInstance(requireContext())
                .DonationDao()
                .getDonatonList()  // Database se saare donors le aao

            withContext(Dispatchers.Main) {
                for (donor in donors) {
                    val location = LatLng(donor.latitude, donor.longitude)
                    addMarker(location, donor.donationType.toString())
                }
            }
        }
    }

    private fun addMarker(location: LatLng, donationType: Any) {
        val icon = when (donationType) {
            "Blood" -> R.drawable.blood_download
            "Medicine" -> R.drawable.download
            "Money" -> R.drawable.money_download
            "Organ" -> R.drawable.organ_download
            else -> R.drawable.defalut
        }
        val smallMarkerIcon = resizeMapIcon(icon, 50, 50)
    }

    private fun loadDonorLocations() {
        CoroutineScope(Dispatchers.IO).launch {
            val donors = DonationDatabase.getInstance(requireContext())
                .DonationDao()
                .getDonatonList()  // Database se saare donors le aao

            withContext(Dispatchers.Main) {
                for (donor in donors) {
                    val location = LatLng(donor.latitude, donor.longitude)
                    addMarker(location, donor.donationType.toString())
                }
            }
        }
    }

    private fun addMarker(location: LatLng, donationType: Any) {
        val icon = when (donationType) {
            "Blood" -> R.drawable.blood
            "Medicine" -> R.drawable.medicine
            "Money" -> R.drawable.money
            "Organ" -> R.drawable.organ
            else -> R.drawable.defalut
        }

        mGoogleMap?.addMarker(
            MarkerOptions()
                .position(location)
                .title("$donationType Donor")
//                .icon(BitmapDescriptorFactory.fromResource(icon))
                .icon(smallMarkerIcon)
                .icon(BitmapDescriptorFactory.fromResource(icon))
        )
    }

}