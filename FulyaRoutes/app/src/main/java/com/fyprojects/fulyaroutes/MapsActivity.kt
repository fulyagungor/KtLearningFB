package com.fyprojects.fulyaroutes

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
//bu ise mesela anlık aracta kullanıcıdan veri alınmasi icin kullaniliyor
  //  private lateinit var fusedLocationClient: FusedLocationProviderClient oncreate- fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    private lateinit var  locationManager : LocationManager
    private lateinit var locationListener: LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intentToMainActivity = Intent(this,MainActivity::class.java)
        startActivity(intentToMainActivity)
        finish()

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
       mMap.setOnMapLongClickListener(myListener)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        println("--onMapReady--")
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                println("--onLocationChanged--")
                if (location != null) {

                    val sharedPreferences = this@MapsActivity.getSharedPreferences("com.fyprojects.fulyaroutes", Context.MODE_PRIVATE)
                    var firstLocationDef = sharedPreferences.getBoolean("firstLocationDef", false)

                    if (true) { //!firstLocationDef
                        mMap.clear()
                        val newUserLocation = LatLng(location.latitude, location.longitude)
                        println("--new user location to lat/lng--" + location.latitude + "/" + location.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newUserLocation, 15f))
                        sharedPreferences.edit().putBoolean("firstLocationDef", true).apply()
                    }
                }
            }
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {

            val intent = intent
            val info = intent.getStringExtra("info")

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2,2f,locationListener)
            if (info.equals("fromMenu")) {
                println("--fromMenu take lastknownlocation--")
                val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

                if (lastLocation != null) {

                    mMap.clear()
                    val lastLocationLatLng = LatLng(lastLocation.longitude, lastLocation.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocationLatLng, 15f))

                }
            } else {
                mMap.clear()
                println("--fromMenu take selectedPlace--")
                val selectedPlace = intent.getSerializableExtra("selectedPlace") as PlaceModel
                val selectedLocation = LatLng(selectedPlace.latitude!!, selectedPlace.longitude!!)
                mMap.addMarker(MarkerOptions().title(selectedPlace.address).position(selectedLocation))
                println("--address--"+selectedPlace.address)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 15f))

            }


        }
    }




    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if(requestCode==1 && grantResults.size>1
            && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2,2f,locationListener)

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    val myListener  = object : GoogleMap.OnMapLongClickListener{
        override fun onMapLongClick(p0: LatLng?) {
            println("----------OnMapLongClickListener--------")
            val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())

            val address = StringBuilder()
            if(p0 !=null) {
                try {
                    val addressList = geocoder.getFromLocation(p0.latitude, p0.longitude, 1)
                    println("--------addressList.size---------"+addressList.size)
                    if (addressList != null && addressList.size > 0) {
                        if (addressList[0].thoroughfare != null) {
                            address.append(addressList[0].thoroughfare)
                            if (addressList[0].subThoroughfare != null) {
                                address.append(addressList[0].subThoroughfare)
                            }
                        }
                    }else{
                        address.append("New Place")
                    }

                }catch(e:Exception){
                    e.printStackTrace()
                }

                mMap.addMarker(MarkerOptions().position(p0).title(address.toString()))
                println("-----MarkerOptions().position(p0)"+MarkerOptions().position(p0).title)
                val newPlaceModel = PlaceModel(address.toString(),p0.latitude,p0.longitude)

                val dialog = AlertDialog.Builder(this@MapsActivity)
                dialog.setCancelable(false) // cıkıs yapamasin
                dialog.setTitle( "Emin misiniz")

                dialog.setMessage(address.toString())
                dialog.setPositiveButton("Evet"){dialog, which ->
                    //Save sqlite address , enlem boylam kayit
                    try {
                        val database = openOrCreateDatabase("Places",Context.MODE_PRIVATE,null) //context private sadece ben kullancaksam
                        database.execSQL("CREATE TABLE IF NOT EXISTS places (address VARCHAR , latitude DOUBLE, longitude DOUBLE)")

                        val toCompileSql = "INSERT INTO places(address,latitude,longitude) VALUES (?,?,?)"
                        val sqLiteStatement = database.compileStatement(toCompileSql)
                        sqLiteStatement.bindString(1,newPlaceModel.address)
                        sqLiteStatement.bindDouble(2,newPlaceModel.latitude!!)
                        sqLiteStatement.bindDouble(3,newPlaceModel.longitude!!)
                        sqLiteStatement.execute()


                    }catch (e: java.lang.Exception){
                        e.printStackTrace()
                    }
                    Toast.makeText(this@MapsActivity,"Yeni yer kaydedildi Fulya.",Toast.LENGTH_LONG).show()

                }.setNegativeButton("Hayır"){dialog, which ->
                    Toast.makeText(this@MapsActivity,"Iptal Edildi",Toast.LENGTH_LONG).show()
                    mMap.clear()

                }
                dialog.show()
            }



        }


    }



}