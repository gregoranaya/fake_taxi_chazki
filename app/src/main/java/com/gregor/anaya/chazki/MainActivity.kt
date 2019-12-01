package com.gregor.anaya.chazki

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.ActivityNavigator
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val gMap =  map as SupportMapFragment
        gMap.getMapAsync(this)
        bt_request.setOnClickListener {
            val intent = Intent(this,OrderActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onMapReady(maps: GoogleMap) {
        mMap = maps

        val chazki = LatLng(-12.1107369, -77.0121892)
        mMap.addMarker(MarkerOptions().position(chazki).title("Chazki"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(chazki))
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(chazki, 15.0f))

    }

}
