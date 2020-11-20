package antoniomy82.ecommerce.utils

import android.Manifest
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.*

/**
 *  Created by Antonio J Morales on 12/4/17.
 *  Adaptada a Kotlin 15/04/2020
 *  Puedes descargar el código de mi Github : https://github.com/antoniomy82
*/
open class GPSTracker : Service {

    private var mContext: Context? = null
    private var isGPSEnabled = false        // Flag for GPS status
    private var isNetworkEnabled = false    // Flag for network status

    private var location: Location? = null  // Location
    private var latitude = 0.0              // Latitude
    private var longitude = 0.0             // Longitude

    private var locationManager: LocationManager? = null
    private var activity: Activity? = null

    val address = false //Geodecoder obtain an Address.

    constructor()
    constructor(context: Context?, activity: Activity?) {
        mContext = context
        this.activity = activity
        getLocation()
    }

    companion object {
        // The minimum distance to change Updates in meters
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 1000 // 10 meters

        // The minimum time between updates in milliseconds
        private const val MIN_TIME_BW_UPDATES = 1000 * 30.toLong()  // 30 seconds
    }


    fun gpsIsActive(): Boolean {
        isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

        return isGPSEnabled
    }

    fun getLocation(): Location? {
        try {
            locationManager =
                mContext?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            // Getting GPS status
            isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

            // Getting network status
            isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            // If GPS enabled, get latitude/longitude using GPS Services
            if (isGPSEnabled && isNetworkEnabled) {
                if (location == null) {
                    if (ContextCompat.checkSelfPermission(
                            activity!!,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            activity!!, Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {

                        ActivityCompat.requestPermissions(
                            activity!!,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            50
                        )
                        Log.d("checkPermission", "check")

                    } else {
                        locationManager!!.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                            mLocationListener
                        )
                        Log.d("GPS Enabled", "GPS Enabled")

                        if (locationManager != null) {
                            this.location =
                                locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)

                            if (location != null) {
                                latitude = location!!.latitude
                                longitude = location!!.longitude
                            }
                        }
                    }
                }
            }

        } catch (e: Exception) { e.printStackTrace() }

        return location
    }//getLocation


    private val mLocationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
                latitude = location.latitude
                longitude = location.longitude
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    /**
     * Function to get latitude
     */
    fun getLatitude(): Double {
        if (location != null) {
            latitude = location!!.latitude
        }

        return latitude
    }

    /**
     * Function to get longitude
     */
    fun getLongitude(): Double {
        if (location != null) {
            longitude = location!!.longitude
        }

        return longitude
    }


    override fun onBind(arg0: Intent): IBinder? { return null }

    /**
     * Gives you complete address of the location
     *
     * @return complete address in String
     */
    fun getAddressFromLocation(mlatitude: Double, mlongitude: Double): String {
        //getLocation()!=null || mlongitude!=null && mlatitude!=null
        return run {
            val geocoder = Geocoder(mContext, Locale.getDefault())
            // Get the current location from the input parameter list
            // Create a list to contain the result address
            val addresses: List<Address>?

            addresses = try {
                geocoder.getFromLocation(mlatitude, mlongitude, 1) // Devuelve una dirección

            } catch (e1: IOException) {
                e1.printStackTrace()
                return "IO Exception trying to get address:$e1"

            } catch (e2: IllegalArgumentException) {
                // Error message to post in the log
                val errorString = ("Illegal arguments $mlatitude , $mlongitude passed to address service")
                e2.printStackTrace()
                return errorString
            }
            // If the reverse geocode returned an address
            if (addresses != null && addresses.isNotEmpty()) {

                //this.getAddress=true;

                // Get the first address
                val address = addresses[0]
                /*
                     * Format the first line of address (if available), city, and
                     * country name.
                     */
                /* address.getCountryName());*/
                // Return String format
                String.format(
                    "%s",  // If there's a street address, add it
                    address.getAddressLine(0), "",  // Locality is usually a city
                    address.locality
                )
            } else {
                "DIRECCIÓN NO ENCONTRADA: \n ! GPS sin señal ! \n Intentelo más tarde \n Pruebe en un lugar abierto, evite los interiores."
            }
        }
    }

    fun getLocationFromAddress(myAddress: String): Location {

        val geocoder = Geocoder(mContext, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocationName(myAddress, 1)

        val address: Address = addresses[0]
        val latitude = address.latitude
        val longitude = address.longitude

        val myLocation = Location("locationFromAddress")
        myLocation.latitude = latitude
        myLocation.longitude = longitude

        return myLocation
    }


}