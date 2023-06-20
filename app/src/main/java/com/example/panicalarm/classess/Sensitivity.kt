package com.example.panicalarm.classess

import androidx.lifecycle.ViewModel

class Sensitivity() :ViewModel(){

 private var sensitivity:Int = 20

    fun setSensitivity( sensitivity:Int)
    {
        this.sensitivity = sensitivity
    }

    fun getSensitivity():Int
    {
        return this.sensitivity
    }

}