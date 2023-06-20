package com.example.panicalarm.classess

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.panicalarm.database.Contact

class MakeCall {
    fun makeCall(context: Context, phone: String) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity, arrayOf(Manifest.permission.CALL_PHONE),
                PERMISSION_MAKE_CALL
            )
        } else {
            val dialIntent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$phone")
            }
            context.startActivity(dialIntent)
        }
    }

    companion object {
        const val PERMISSION_MAKE_CALL = 1 // replace with your own request code
    }

    fun handlePermissionResult(
        requestCode: Int,
        grantResults: IntArray,
        phone: String,
        context: Context
    ) {
       when(requestCode)
       {
           PERMISSION_MAKE_CALL ->
           {
               if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                   makeCall(context, "7414322551")
               } else {
               // permission denied, show an error message
               Toast.makeText(context, "SMS permission denied", Toast.LENGTH_SHORT).show()
           }
           }
       }
    }
}