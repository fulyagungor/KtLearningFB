package com.fyprojects.flyplaces

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    //konum alma islemleri icin android locationManager ve Listener
    private lateinit var locationManager : LocationManager // lateinit var koyunca ? =null'a gerek yok
    private lateinit var locationListener : LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        /* bunun yerine konum servisini alcaz
        // Add a marker in Sydney and move the camera
        val bebek = LatLng(41.07621771895479, 29.043672546617035)
        mMap.addMarker(MarkerOptions().position(bebek).title("Marker in Bebek"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bebek,12f))  //zoom fonksiyonu burda, yoksa newLatlang yeterli
*/

        mMap.setOnMapLongClickListener(myListener)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager //getSystemService any dondugu icin cast ettik

        //LocationListener bir interface, locationmanager'i implement eden bir locationListener yazıyoruz- kotlin ile
        locationListener = object :LocationListener{
            override fun onLocationChanged(location: Location) {  //location direk gelior.

                if(location != null){
                    val userLocation = LatLng(location.latitude,location.longitude)
                    mMap.addMarker(MarkerOptions().position(userLocation).title("Your Location"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15f))

                    val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())

                    //Geocoding longlat den enlem boylamdan adres turetme
                    try {
                        println("---Geocoding---------")

                    val addresList = geocoder.getFromLocation(location.latitude,location.longitude,1)

                        if(addresList !=null && addresList.size>0){
                            println(addresList.get(0).toString())
                        }

                    }catch (e:Exception){

                    }
                }

            }

        }


        //izin istioruz yoksa
        if(ContextCompat.checkSelfPermission(this@MapsActivity,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this@MapsActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)
            //locationManager üzerinde son bilinen konumu alarak kullanıcının lokasyonuna zoomlama
            val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if(lastLocation!=null){
                val lastKnownLatLng = LatLng(lastLocation.latitude,lastLocation.longitude)
                mMap.addMarker(MarkerOptions().position(lastKnownLatLng).title("Son lokasyonum"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLatLng,15f))

            }

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==1 ){
            if(grantResults.size>1){

                //yetki varsa GPS üzerinden location verilerini al,periodik ya da uzakliklarla guncelle.verilen konumu listener'a aktar da degisik
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)
                   }
            }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    //uzun tıklama islemi icin bir hazır interface var. obje olusturuorz. bunu da mapin listenerı olarak yukarda setliyorz
    val myListener = object : GoogleMap.OnMapLongClickListener{
        override fun onMapLongClick(p0: LatLng?) {
            mMap.clear()
            val geocoder = Geocoder(this@MapsActivity,Locale.getDefault())

            if(p0!=null) {
                val address = StringBuilder()
                try {

                    println("-------p0.latitude--"+p0.latitude)


                    //burasi calismio emulator bugları acilmis version sıkıntıları
                    val addressList = geocoder.getFromLocation(p0.latitude, p0.longitude, 1)

                    println("-------adresList--"+addressList)
                    if(addressList!=null && addressList.size>0){
                        println("-------adresList size--"+addressList)
                        if(addressList[0].thoroughfare !=null){
                            println("-------thoroughfare--"+addressList[0].thoroughfare)
                            address.append(addressList[0].thoroughfare)
                            if(addressList[0].subThoroughfare !=null){
                                address.append(addressList[0].subThoroughfare)
                            }
                        }
                    }

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

                //addresi marker uzerinde title olarak goster
                mMap.addMarker(MarkerOptions().position(p0).title(address.toString()))

            }else{
                    Toast.makeText(this@MapsActivity,"Tekrar deneyiniz",Toast.LENGTH_LONG)
            }
        }

    }


}