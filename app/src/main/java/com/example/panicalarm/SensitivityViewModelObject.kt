package com.example.panicalarm

import com.example.panicalarm.classess.Sensitivity

object SensitivityViewModelObject {
    private var instance: Sensitivity? = null

    fun getInstance(): Sensitivity {
        if (instance == null) {
            instance = Sensitivity()
        }
        return instance!!
    }
}