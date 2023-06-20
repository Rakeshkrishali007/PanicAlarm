package com.example.panicalarm.fragments

import android.annotation.SuppressLint
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.panicalarm.R
import com.example.panicalarm.classess.MakeCall
import com.example.panicalarm.classess.SmsUtils
import com.example.panicalarm.database.Contact
import com.example.panicalarm.database.ContactDatabase
import com.example.panicalarm.database.DatabaseObject
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmFragment : Fragment() {

    private var soundPool: SoundPool? = null
    private val soundId = 1
    lateinit var database: ContactDatabase
    lateinit var contactList:List<Contact>
     var latitude:Double? = null
     var longitude:Double? = null
    lateinit var fusedLocationClient: FusedLocationProviderClient



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {

        }
    }

    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        var view = inflater.inflate(R.layout.fragment_alarm, container, false)
        val img: ImageView = view.findViewById(
            R.id.imageView
        )

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        database = DatabaseObject.getInstance(this@AlarmFragment.requireContext())

        database.contactDao().getContact().observe(viewLifecycleOwner, Observer {
            contactList = it
        })
        soundPool = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
        soundPool!!.load(context, R.raw.alarm_sound, 1)
        img.setOnClickListener {
        if(!contactList .isEmpty())
        {

            // send sms
            CoroutineScope(Dispatchers.IO).launch {

                getLocation()
                val currentLocationLink = "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"
                Log.d("smssent"," coroutime, $latitude,$longitude")

                val smsUtils = SmsUtils()

                smsUtils.sendTextMessage(this@AlarmFragment.requireContext(), contactList, currentLocationLink)
            }

            // make call
            CoroutineScope(Dispatchers.IO).launch {

                val makeCall = MakeCall()
                makeCall.makeCall(this@AlarmFragment.requireContext(), "7417322551")
            }
            // play sound
           /* CoroutineScope(Dispatchers.IO).launch {
                //playAlarmSound()
            }*/

        }
        else
        {
            Toast.makeText(requireContext(), "First add contact", Toast.LENGTH_SHORT).show()
        }

        }
        return view
    }

    private fun playAlarmSound() {
        soundPool?.play(soundId, 1F, 1F, 0, 0, 1F)
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
            else
            {
                Log.d("currentLink", "it is empty")
            }
            Log.d("smssent", "$latitude, $longitude")
        }
    }

}