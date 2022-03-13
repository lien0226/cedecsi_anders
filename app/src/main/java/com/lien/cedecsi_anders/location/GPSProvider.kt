package com.lien.cedecsi_anders.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices


class GPSProvider (private var context: AppCompatActivity, private var provider: GpsProviderType = GpsProviderType.Internal){
    companion object{
        const val LOG_TAG = "GPS Provider"
        const val FINE_LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
        const val COARSE_LOCATION_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION
        const val TIME_UPDATE: Long = 10*1000
        const val DISTANCE_UPDATE = 0f
    }

    private val requestPermissionLauncher = context.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    )
    {
        if (it[FINE_LOCATION_PERMISSION]==true && it[COARSE_LOCATION_PERMISSION]==true){
            getLocation()
        }else{
            Toast.makeText(context, "Debe conceder los permisos", Toast.LENGTH_SHORT).show()
        }
    }

    var onLocation: (Location)-> Unit = {}
    private var locationByGps: Location?=null
    private var locationByNetwork: Location?=null

    var locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val gpsLocationListener: LocationListener = object:LocationListener{
        override fun onLocationChanged(location: Location) {
            locationByNetwork=location
        }
    }
    val networkLocationListener:LocationListener = object:LocationListener{
        override fun onLocationChanged(location: Location) {
            locationByNetwork = location
        }
    }

    fun checkPermission(){
        if (ContextCompat.checkSelfPermission(context, FINE_LOCATION_PERMISSION)!=PackageManager.PERMISSION_GRANTED ||ContextCompat.checkSelfPermission(context, COARSE_LOCATION_PERMISSION)!=PackageManager.PERMISSION_GRANTED){
            requestPermissionLauncher.launch(arrayOf(FINE_LOCATION_PERMISSION, COARSE_LOCATION_PERMISSION))
        }else{
            getLocation()
        }
    }
    private fun getLocation(){
        if (provider==GpsProviderType.Internal){
            getGPSLocation()
        }else{
            getProviderLocation()
        }
    }
    fun onDestroy(){
        locationManager.removeUpdates(gpsLocationListener)
        locationManager.removeUpdates(networkLocationListener)
    }

    @SuppressLint("MissingPermission")
    private fun getGPSLocation(){
        val hasGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNetWork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!hasGPS && !hasNetWork){
            Toast.makeText(context, "Debe activar su GPS", Toast.LENGTH_SHORT).show();
            return
        }
        if (hasGPS){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME_UPDATE, DISTANCE_UPDATE, gpsLocationListener)
        }
        if (hasNetWork){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TIME_UPDATE, DISTANCE_UPDATE, gpsLocationListener)
        }

        val lastKnownLocationByGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (lastKnownLocationByGps!=null){
            locationByGps = lastKnownLocationByGps
        }

        var lastKnownLocationByNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (lastKnownLocationByNetwork!=null){
            locationByNetwork = lastKnownLocationByNetwork
        }

        if (locationByGps !=null && locationByNetwork!=null){
            if (locationByGps!!.accuracy>locationByNetwork!!.accuracy){
                onLocation(locationByGps!!)
            }else{
                onLocation(locationByNetwork!!)
            }
        }

    }

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)
    private lateinit var locationRequest: LocationRequest
    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            if (p0===null){
                Toast.makeText(context, "Información no disponible", Toast.LENGTH_SHORT).show()
            }else{
                onLocation(p0.lastLocation)
            }
        }
    }
    private val removeTask = fusedClient.removeLocationUpdates(locationCallback)

    @SuppressLint("MissingPermission")
    private fun getProviderLocation(){
        locationRequest = LocationRequest.create()
        locationRequest.interval = TIME_UPDATE*2
        locationRequest.fastestInterval = TIME_UPDATE
        locationRequest.maxWaitTime = TIME_UPDATE * 6
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        fusedClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper()!!)
        removeTask.addOnCompleteListener { task ->
            if (task.isSuccessful){
                Toast.makeText(context, "Actualizaciones detenidas", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context,"No se puedo remover la actualizacion", Toast.LENGTH_SHORT).show()
            }
        }
    }

}

enum class GpsProviderType{
    Internal, External
}

