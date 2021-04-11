package com.project.mapapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_map.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener,
    GoogleMap.OnMyLocationButtonClickListener {

    private lateinit var mMap: GoogleMap
    var client: FusedLocationProviderClient? = null
    private lateinit var mapFragment: SupportMapFragment
    lateinit var latitude: String
    lateinit var longitude: String

    private var sensorAccelerometer: Sensor? = null
    private var sensorMagneticField: Sensor? = null
    private lateinit var sensorManager: SensorManager
    private var floatGravity = FloatArray(3)
    private var floatGeo = FloatArray(3)

    private val floatOrientation = FloatArray(3)
    private val floatRotation = FloatArray(9)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //location
        client = LocationServices.getFusedLocationProviderClient(this);

        val bundle = intent.extras
        if (bundle != null) {
            latitude = bundle.getString("latitude").toString()
            longitude = bundle.getString("longitude").toString()
        }

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        val sensorEventListenerAccelerometer: SensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                floatGravity = event.values
                SensorManager.getRotationMatrix(
                    floatRotation,
                    null,
                    floatGravity,
                    floatGeo
                )
                SensorManager.getOrientation(floatRotation, floatOrientation)
                bearingText.text = (-floatOrientation[0] * 180 / 3.14159).toFloat().toInt().toString() + "degres"
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }

        var sensorEventListenerMagneticField: SensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                floatGeo = event.values
                SensorManager.getRotationMatrix(
                    floatRotation,
                    null,
                    floatGravity,
                    floatGeo
                )
                SensorManager.getOrientation(floatRotation, floatOrientation)
                bearingText.text = (-floatOrientation[0] * 180 / 3.14159).toFloat().toInt().toString() + "degres"
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }
        sensorManager.registerListener(
            sensorEventListenerAccelerometer,
            sensorAccelerometer,
            SensorManager.SENSOR_DELAY_UI
        );
        sensorManager.registerListener(
            sensorEventListenerMagneticField,
            sensorMagneticField,
            SensorManager.SENSOR_DELAY_UI
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        val latitudeDouble: Double = latitude.toDouble()
        val longitudeDouble: Double = longitude.toDouble()
        val sydney = LatLng(latitudeDouble, longitudeDouble)

        val location = CameraUpdateFactory.newLatLngZoom(sydney, 15f)
        googleMap.addMarker(MarkerOptions().position(sydney).title("We are here :)"))
        googleMap.animateCamera(location)

        mMap.isMyLocationEnabled = true
        mMap.setOnMyLocationButtonClickListener(this)

        getLocation(googleMap)

        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
    }

    private fun getLocation(map: GoogleMap) {
        val client: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
            applicationContext
        )
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        } else {
            client.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {

                }
            }
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    override fun onLocationChanged(location: Location) {
        TODO("Not yet implemented")
    }
}
