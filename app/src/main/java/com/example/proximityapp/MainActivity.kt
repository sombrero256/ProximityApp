package com.example.proximityapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt


class MainActivity : AppCompatActivity() {
    val LogTag = "GPS"
    private val REQUEST_LOCATION = 123
    var locationManager: LocationManager? = null
    var providers: List<String>? = null
    var preferred: String = LocationManager.GPS_PROVIDER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContentView(R.layout.activity_main)
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), PackageManager.PERMISSION_GRANTED)
        }
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        for (prov:String in locationManager!!.allProviders){
            Log.i(LogTag, "Provider: " + prov)
        }

        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_COARSE
        providers= locationManager!!.getProviders(criteria, true);

        if (providers == null || providers!!.isEmpty()) {
            Log.e(LogTag, "cannot_get_gps_service")
            Toast.makeText(
                this, "Could not open GPS service",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        preferred = providers!![0] // first == preferred
    }


    override fun onResume() {
        super.onResume()
        Log.i(LogTag, "onResume")

        val provider = preferred

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION ), PackageManager.PERMISSION_GRANTED)
        }
        else {

            locationManager!!.requestLocationUpdates(
                provider, 2000, 10f,
                locationListener
            )
            val location = locationManager!!.getLastKnownLocation(provider)
            updateWithNewLocation(location)
        }
    }

    override fun onPause() {
        super.onPause()
        locationManager!!.removeUpdates(locationListener)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Location Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Location Permission Denied", Toast.LENGTH_SHORT).show()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions!!, grantResults)
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            updateWithNewLocation(location)
        }

        override fun onProviderDisabled(provider: String) {
            updateWithNewLocation(null)
        }

        override fun onProviderEnabled(provider: String) {}
        override fun onStatusChanged(
            provider: String, status: Int,
            extras: Bundle
        ) {
        }
    }

    private fun updateWithNewLocation(location: Location?) {
        val myLocationText: TextView
        myLocationText = findViewById<View>(R.id.myLocationText) as TextView
        /*val latLongString: String

        var addressString = "No address found"
        if (location != null) {
            val lat = location.latitude
            val lng = location.longitude
            latLongString = "Lat:$lat\nLong:$lng"
            val latitude = location.latitude
            val longitude = location.longitude
            val gc = Geocoder(this, Locale.getDefault())
            try {
                val addresses = gc.getFromLocation(latitude, longitude, 1)
                val sb = StringBuilder()
                if (addresses.size > 0) {
                    val address = addresses[0]
                    sb.append(address.getAddressLine(0)).append("\n")
                    sb.append(address.featureName).append("\n")
                    sb.append(address.locality).append("\n")
                    sb.append(address.postalCode).append("\n")
                    sb.append(address.countryName)
                }
                addressString = sb.toString()
                Log.i(LogTag,addresses.toString())
            } catch (e: IOException) {
            }
        } else {
            latLongString = "No location found"
        }

        */

        //My house coords 42.0006, -91.6543
        var distance = 0.0f

        val homeLat = 42.0006
        val homeLong = -91.6543

        if (location != null) {
            distance = sqrt((homeLat - location.latitude).toFloat().pow(2) + (homeLong - location.longitude).toFloat().pow(2))
        }

        if (distance != null) {
            if (distance <= 0.0001f) {
                myLocationText.text = "You're close to my house!"
            } else {
                myLocationText.text = "You don't know where I live, do you?"
            }
            }

    }
}