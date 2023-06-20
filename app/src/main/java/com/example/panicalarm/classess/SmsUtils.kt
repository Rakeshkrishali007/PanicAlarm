package com.example.panicalarm.classess

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.panicalarm.database.Contact

 class SmsUtils() {

    fun sendTextMessage(context: Context, contactList: List<Contact>, currentLocationLink: String,) {
        Log.d("currentLocaiton","sent")
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.SEND_SMS),
                PERMISSION_SEND_SMS
            )
        } else {
            val smsManager = SmsManager.getDefault()
          //  Log.d("smssent","permission ")
            for(item in contactList)
            {

                Log.d("currentLocaiton", "$currentLocationLink")
                Log.d("currentLocaiton","permission ")

                smsManager.sendTextMessage(item.phone , null, "Need help", null, null)
                smsManager.sendTextMessage(item.phone , null, currentLocationLink, null, null)

            //    Toast.makeText(context, "Message sent to ${item.phone}", Toast.LENGTH_SHORT).show()
            }

            }

    }

    companion object {
        const val PERMISSION_SEND_SMS = 123 // replace with your own request code
    }




    fun handlePermissionsResult(
        requestCode: Int,
        grantResults: IntArray,
        phoneNumber: String,
        message: String,
        context: Context
    ) {
        when (requestCode) {
            PERMISSION_SEND_SMS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    val smsManager = SmsManager.getDefault()
                    smsManager.sendTextMessage(phoneNumber, null, message, null, null)
                } else {
                    // permission denied, show an error message
                    Toast.makeText(context, "SMS permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            // handle other permission requests here
        }
    }
}