package com.gregor.anaya.chazki

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.trip_layout.*
import kotlinx.android.synthetic.main.trip_layout.map
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.uiThread

import java.net.URL

class TripActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.trip_layout)
        val gMap =  map as SupportMapFragment
        gMap.getMapAsync(this)
        bt_cancel.setOnClickListener {
            Toast.makeText(this@TripActivity,"Viaje cancelado",Toast.LENGTH_LONG).show()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        bt_message.setOnClickListener {
            Toast.makeText(this@TripActivity,"Mensaje enviado",Toast.LENGTH_LONG).show()
        }
    }

    override fun onMapReady(maps: GoogleMap) {
        mMap = maps
        val LatLongB = LatLngBounds.Builder()
        val chazki = LatLng(-12.1107369, -77.0121892)
        val myHouse = LatLng(-12.145025,-77.0056433);
        mMap.addMarker(MarkerOptions().position(chazki).title("Chazki"))
        mMap.addMarker(MarkerOptions().position(myHouse).title("Mi casa"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(chazki))
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(chazki, 15.0f))

        val options = PolylineOptions()
        options.color(Color.RED)
        options.width(5f)

        val url = getURL(chazki, myHouse)

        async {
            // Connect to URL, download content and convert into string asynchronously
            val result = URL(url).readText()
            uiThread {
                // When API call is done, create parser and convert into JsonObjec
                val parser: Parser = Parser()
                val stringBuilder: StringBuilder = StringBuilder(result)
                val json: JsonObject = parser.parse(stringBuilder) as JsonObject
                // get to the correct element in JsonObject
                val routes = json.array<JsonObject>("routes")
                val points = routes!!["legs"]["steps"][0] as JsonArray<JsonObject>
                // For every element in the JsonArray, decode the polyline string and pass all points to a List
                val polypts = points.flatMap { decodePoly(it.obj("polyline")?.string("points")!!) }
                // Add  points to polyline and bounds
                options.add(chazki)
                LatLongB.include(chazki)
                for (point in polypts) {
                    options.add(point)
                    LatLongB.include(point)
                }
                options.add(myHouse)
                LatLongB.include(myHouse)
                // build bounds
                val bounds = LatLongB.build()
                // add polyline to the map
                mMap!!.addPolyline(options)
                // show map with route centered
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            }


        }
    }

    private fun getURL(from : LatLng, to : LatLng) : String {
        val origin = "origin=" + from.latitude + "," + from.longitude
        val dest = "destination=" + to.latitude + "," + to.longitude
        val sensor = "sensor=false"
        var apikey = "key=AIzaSyAfpHICOCgFT0-XtMFnbIQp59thHSLmjIM"
        val params = "$origin&$dest&$sensor&$apikey"
        return "https://maps.googleapis.com/maps/api/directions/json?$params"
    }

    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5,
                lng.toDouble() / 1E5)
            poly.add(p)
        }

        return poly
    }
}