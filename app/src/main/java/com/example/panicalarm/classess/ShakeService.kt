package com.example.panicalarm

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.telephony.SmsManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleOwner
import com.example.panicalarm.activity.DashBoardActivity
import com.example.panicalarm.classess.ServiceLifecycleOwner
import com.example.panicalarm.database.Contact
import com.example.panicalarm.database.ContactDatabase
import com.example.panicalarm.database.DatabaseObject
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ShakeService : Service() {

    private lateinit var shakeDetector: ShakeDetector
    private lateinit var mediaPlayer: MediaPlayer
    lateinit var contactList:List<Contact>
    lateinit var database: ContactDatabase
    var latitude:Double? = null
    var longitude:Double? = null
    var shouldSend = false
    lateinit var fusedLocationClient: FusedLocationProviderClient


    companion object {
        private const val CHANNEL_ID = "ShakeServiceChannel"
        private const val NOTIFICATION_ID = 1
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate() {
        super.onCreate()
        contactList = ArrayList()

        database = DatabaseObject.getInstance(this)
        val serviceLifecycleOwner = ServiceLifecycleOwner()
        val lifecycleOwner: LifecycleOwner = serviceLifecycleOwner
        GlobalScope.launch {

            contactList =  database.contactDao().getContact2()
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        shakeDetector = ShakeDetector(this) {

            playSound()
        }
        mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        shakeDetector.start()

        startForeground(NOTIFICATION_ID, createNotification())
        return START_STICKY
    }

    override fun onDestroy() {
        shakeDetector.stop()
        mediaPlayer.release()
        super.onDestroy()
    }

    private fun createNotification(): Notification {
        createNotificationChannel()

        val notificationIntent = Intent(this, DashBoardActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Shake Service")
            .setContentText("Detecting shakes...")
            // .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Shake Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun playSound() {

       if( checkLocationEnable())
       {

       }else
       {
           openLocation(this)
       }
      /*  if(shouldSend)
        {
            return
        }
        shouldSend = true
      if(contactList.isNotEmpty())
      {
          getLocation()
          var currentLocationLink = ""
          CoroutineScope(Dispatchers.IO).launch {



              currentLocationLink = "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"
              sendTextMessage(this@ShakeService,currentLocationLink,contactList)
          }

      }

        GlobalScope.launch {
            kotlinx.coroutines.delay(5000) // Adjust the cooldown period as needed
            shouldSend = false
        }
*/
    }

    private fun openLocation(context: ShakeService) {


    }

    private fun checkLocationEnable(): Boolean {

        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            return true
        }

        return  false
    }

    @RequiresApi(Build.VERSION_CODES.M)
       fun sendTextMessage(
           shakeService: ShakeService,
           currentLocationLink: String,
           contactList: List<Contact>
       ) {

           val smsManager = SmsManager.getDefault()
           for (item in contactList)
           {
               smsManager.sendTextMessage(item.phone,null,"Need help!",null,null)
               smsManager.sendTextMessage(item.phone,null,currentLocationLink,null,null)
           }


       }


    @SuppressLint("MissingPermission")
    private fun getLocation()
    {

        val location = fusedLocationClient.lastLocation
        location.addOnSuccessListener {
            if(it != null)
            {
                latitude = it.latitude
                longitude = it.longitude
            }
        }
    }
}
