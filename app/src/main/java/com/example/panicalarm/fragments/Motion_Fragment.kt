package com.example.panicalarm.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.panicalarm.R
import com.example.panicalarm.SensitivityViewModelObject
import com.example.panicalarm.classess.Sensitivity
import com.example.panicalarm.databinding.FragmentContactDetailsBinding
import com.example.panicalarm.databinding.FragmentMotionBinding
import com.google.android.material.slider.Slider


class Motion_Fragment : Fragment() {

    lateinit var binding: FragmentMotionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMotionBinding.inflate(layoutInflater)
        val viewModel = SensitivityViewModelObject.getInstance()
        binding.slider.value = viewModel.getSensitivity().toFloat()

        binding.slider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                Log.d("onstart", "nothing to do")
            }
            override fun onStopTrackingTouch(slider: Slider) {

                val value = slider.value.toInt()
                viewModel.setSensitivity(value)
                binding.tvSliderSensitivity.text = "Shake sensitivity :" + value.toString()
            }


        })

        return binding.root
    }


}