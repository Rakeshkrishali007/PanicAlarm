package com.example.panicalarm.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.LocationManager
import android.media.AudioManager
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.panicalarm.R
import com.example.panicalarm.SensitivityViewModelObject
import com.example.panicalarm.classess.*
import com.example.panicalarm.database.Contact
import com.example.panicalarm.database.ContactDatabase
import com.example.panicalarm.database.DatabaseObject
import com.example.panicalarm.fragments.AlarmFragment
import com.example.panicalarm.fragments.ContactDetailsFragment
import com.example.panicalarm.fragments.Motion_Fragment
import com.example.panicalarm.ShakeService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.*
import java.lang.Math.sqrt
import java.util.*

class DashBoardActivity : AppCompatActivity(), LocationBroadcastReceiver.LocationChanged {

    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    lateinit var viewModel: Sensitivity
    private var currentAcceleration = 0f
    lateinit var contactList: List<Contact>
    lateinit var database: ContactDatabase
    private var lastAcceleration = 0f
    var latitude: Double? = null
    var longitude: Double? = null
    private val PERMISSION_SEND_SMS = 100
    private val PERMISSION_ACCESS_LOCATION = 101
    private val PERMISSION_PHONE_CALL = 102

    var locationEnable: Boolean = false
    var isEnable: Boolean = false
    lateinit var fusedLocationClient: FusedLocationProviderClient


    private var soundPool: SoundPool? = null
    private val soundId = 1
    val locationBroadcastReceiver = LocationBroadcastReceiver(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkMessagePermission()


        viewModel = SensitivityViewModelObject.getInstance()
        var bn: BottomNavigationView
        val bottomNav = findViewById<BottomNavigationView>(R.id.bnView)




        database = DatabaseObject.getInstance(this)
        database.contactDao().getContact()
            .observe(this@DashBoardActivity, androidx.lifecycle.Observer {
                contactList = it
            })
        // Creating Object of SoundPool
        soundPool = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
        soundPool!!.load(baseContext, R.raw.alarm_sound, 1)

        startService(Intent(this, ShakeService::class.java))
        //Sesor Object
        SensorObject()

        //method for loading fragments
        loadFragment(AlarmFragment())

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_alarm -> {
                    loadFragment(AlarmFragment())
                    true
                }

                R.id.nav_motion -> {

                    loadFragment(Motion_Fragment())
                    true
                }
                R.id.nav_contact -> {
                    loadFragment(ContactDetailsFragment())
                    true
                }
                else -> {
                    false
                }
            }
        }

    }

    private fun checkCallPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CALL_PHONE),
                PERMISSION_PHONE_CALL
            )
            return
        }
    }

    private fun checkMessagePermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.SEND_SMS),
                PERMISSION_SEND_SMS
            )
            return
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_ACCESS_LOCATION
            )
            return
        }
    }


    @SuppressLint("SuspiciousIndentation")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_PHONE_CALL -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || !locationManager.isProviderEnabled(
                            LocationManager.NETWORK_PROVIDER
                        )
                    ) {
                        openLocationSettings(this)

                    }

                }
            }
            PERMISSION_SEND_SMS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocationPermission()
                }
            }
            PERMISSION_ACCESS_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkCallPermission()
                }
            }
        }
    }

    //method for observing motion
    private fun SensorObject() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        Objects.requireNonNull(sensorManager)!!
            .registerListener(
                sensorListener, sensorManager!!
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
            )

        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH
    }


    //Method to load fragments
    fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu2, menu)
        return true
    }

    // Creating Object of SensorEventListener
    val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {

            // Fetching x,y,z values
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            lastAcceleration = currentAcceleration

            // Getting current accelerations
            // with the help of fetched x,y,z values
            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta

            // Display a Toast message if
            // acceleration value is over 12
            if (acceleration > viewModel.getSensitivity()) {


                /* getLocation()


                 // when phone gets shake application send the message to your contact
                 CoroutineScope(Dispatchers.IO).launch {


                     val currentLocationLink =
                         "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"


                     // Toast.makeText(this@DashBoardActivity, "$currentLocationLink", Toast.LENGTH_SHORT).show()
                     val smsUtils = SmsUtils()
                     smsUtils.sendTextMessage(
                         this@DashBoardActivity,
                         contactList,
                         currentLocationLink
                     )
                 }


                 // make call
                 CoroutineScope(Dispatchers.IO).launch {
                     val makeCall = MakeCall()
                     makeCall.makeCall(this@DashBoardActivity, "7417322551")
                 }*/
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    fun playSound() {
        soundPool?.play(1, 1F, 1F, 0, 0, 1F)
        Toast.makeText(this, "Playing sound. . . .", Toast.LENGTH_SHORT).show()
    }


    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val location = fusedLocationClient.lastLocation
        location.addOnSuccessListener {
            if (it != null) {
                latitude = it.latitude
                longitude = it.longitude
            }
        }
    }

    override fun onResume() {
        // Registering Sensor Listener
        sensorManager?.registerListener(
            sensorListener, sensorManager!!.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER
            ), SensorManager.SENSOR_DELAY_NORMAL
        )

        //Registering Broadcast for location check
        val filter = IntentFilter(LocationManager.MODE_CHANGED_ACTION)
        registerReceiver(locationBroadcastReceiver, filter)
        super.onResume()
    }

    //
    override fun onDestroy() {
        // unregistering broadcast reciever
        unregisterReceiver(locationBroadcastReceiver)
        super.onDestroy()
    }

    override fun onPause() {
        // Unregistering Sensor Listener
        sensorManager!!.unregisterListener(sensorListener)
        super.onPause()
    }

    override fun isLocationOn(isLocationEnabled: Boolean) {

        locationEnable = isLocationEnabled

        if (!locationEnable) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Location Services")
                .setMessage("To use this application you mush have to open locatioin")
                .setPositiveButton("Open") { dialog, _ ->
                    openLocationSettings(this)
                    dialog.dismiss()
                }
                .setCancelable(false)
                .show()
        }

    }

    fun openLocationSettings(context: Context) {


        Log.d("isLocationEnablek", "fun")
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)

        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || !locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        ) {

            Log.d("isLocationEnablek", "still disable")

            openLocationSettings(context)
        }


    }


}
