package com.example.panicalarm.database

import android.content.Context
import android.util.Log
import androidx.room.Room

object DatabaseObject {

    private var INSTANCE: ContactDatabase? = null
    fun getInstance(context: Context): ContactDatabase {

        if (INSTANCE == null) {
            synchronized(ContactDatabase::class) {
                INSTANCE = buildRoomDB(context)
            }
        }
        return INSTANCE!!
    }
    private fun buildRoomDB(context: Context) =
        Room.databaseBuilder(
            context.applicationContext,
            ContactDatabase::class.java,
            "contactDB"
        ).build()
}