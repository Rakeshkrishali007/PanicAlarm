package com.example.panicalarm.classess

import android.content.Context
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.example.panicalarm.R

class Dailog(private val context: Context): LocationBroadcastReceiver.LocationChanged {

    var locationEnable: Boolean = false
        override fun isLocationOn(isLocationEnabled: Boolean) {

        locationEnable = isLocationEnabled
        Log.d("isEnable", "in, $isLocationEnabled")
        val dailog = AlertDialog.Builder(context)
        val view = R.layout.location_dailog
        dailog.setView(view)
        val alertDialog: AlertDialog = dailog.create()
        alertDialog.setCancelable(false)
        if (!isLocationEnabled)
            alertDialog.show()
        val allow = alertDialog.findViewById<Button>(R.id.btn_allow)
        val deny = alertDialog.findViewById<Button>(R.id.btn_deny)
        allow?.setOnClickListener()
        {

            Log.d("isEnable", "on,$locationEnable")
            if (locationEnable) {

            }

        }

    }

}