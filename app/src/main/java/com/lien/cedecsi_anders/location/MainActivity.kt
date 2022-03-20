package com.lien.cedecsi_anders.location

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.lien.cedecsi_anders.R
import com.lien.cedecsi_anders.rest.RestExecute

class MainActivity : AppCompatActivity() {

    private var tvLatitud: TextView? = null
    private var tvLongitud: TextView? = null
    private var btnCoordenadas: Button? = null
    private var btnUpload: Button? = null
    private lateinit var gpsProvider: GPSProvider
    private var location: Location? = null
    private lateinit var restExecute: RestExecute

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
        btnUpload = findViewById(R.id.btnUpload)
    }

    private fun setupListeners(){
        btnCoordenadas?.setOnClickListener {
            gpsProvider.checkPermission()
        }

        gpsProvider.onLocation={
            tvLatitud?.text = "${it.latitude}"
            tvLongitud?.text = "${it.longitude}"
        }
        btnUpload?.setOnClickListener {
            if (location!=null) restExecute.uploadCoordinates(location!!){
                if (it){
                    Toast.makeText(this, "Envío Exitoso!", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "Envío Falló", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        gpsProvider.onDestroy()
        super.onDestroy()
    }
}