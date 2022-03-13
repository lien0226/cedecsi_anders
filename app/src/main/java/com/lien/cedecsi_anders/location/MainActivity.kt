package com.lien.cedecsi_anders.location

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.lien.cedecsi_anders.R

class MainActivity : AppCompatActivity() {

    private var tvLatitud: TextView? = null
    private var tvLongitud: TextView? = null
    private var btnCoordenadas: Button? = null
    private lateinit var gpsProvider: GPSProvider


    companion object{
        const val LOG_TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gpsProvider = GPSProvider(this)
        setupViews()
        setupListeners()

    }

    private fun setupViews(){
        tvLatitud = findViewById(R.id.tvLatitud)
        tvLongitud = findViewById(R.id.tvLongitud)
        btnCoordenadas = findViewById(R.id.btnGetCoords)
    }

    private fun setupListeners(){
        btnCoordenadas?.setOnClickListener {
            gpsProvider.checkPermission()
        }

        gpsProvider.onLocation={
            tvLatitud?.text = "${it.latitude}"
            tvLongitud?.text = "${it.longitude}"
        }
    }

    override fun onDestroy() {
        gpsProvider.onDestroy()
        super.onDestroy()
    }
}