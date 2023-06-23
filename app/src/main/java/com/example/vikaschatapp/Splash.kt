package com.example.vikaschatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.vikaschatapp.databinding.ActivitySplashBinding

class Splash : AppCompatActivity() {
    private lateinit var binding : ActivitySplashBinding
    private val SPLASH_DISPLAY_LENGTH = 2000
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler().postDelayed({
            // Start your main activity here
            val mainIntent = Intent(this, Login::class.java)
            startActivity(mainIntent)

            // Close the splash activity
            finish()
        }, SPLASH_DISPLAY_LENGTH.toLong())
    }
}