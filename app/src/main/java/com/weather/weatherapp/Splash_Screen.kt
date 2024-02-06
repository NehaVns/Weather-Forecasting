package com.weather.weatherapp

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivitySplashScreenBinding

class Splash_Screen : AppCompatActivity() {
    private var binding: ActivitySplashScreenBinding? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = getColor(R.color.white)
        }

        setContentView(binding?.root)

        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    val intent = Intent(
                        this@Splash_Screen,
                        MainActivity::class.java
                    )
                    startActivity(intent)

                    finish()
                }
            }
        };thread.start()
    }
}