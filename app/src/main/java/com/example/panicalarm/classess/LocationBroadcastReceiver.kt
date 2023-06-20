package com.example.panicalarm.classess

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import com.example.panicalarm.activity.DashBoardActivity

class LocationBroadcastReceiver(private val listener: DashBoardActivity): BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == LocationManager.MODE_CHANGED_ACTION) {
            // Location setting has been changed
            val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
           // Log.d("isEnable", "$isLocationEnabled")
            if(isLocationEnabled)
            {
                listener.isLocationOn(true)
            }
            else{
                listener.isLocationOn(false)
            }

        }
    }

    interface LocationChanged
    {
        fun isLocationOn(isLocationEnabled: Boolean)
    }
}