package com.weather.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewbinding.BuildConfig
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityAboutUsBinding

class AboutUs : AppCompatActivity() {
    private lateinit var binding: ActivityAboutUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.icBack?.setOnClickListener {
            onBackPressed()
        }

        binding.tvVersionName?.setText(getString(R.string.version,com.example.weatherapp.BuildConfig.VERSION_NAME))


    }

}