package com.fyprojects.fulyaroutes

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    val placesArray = ArrayList<PlaceModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        try {

            val database = openOrCreateDatabase("Places",Context.MODE_PRIVATE,null)

            val cursor = database.rawQuery("SELECT * FROM places",null)
            val addressIx = cursor.getColumnIndex("address")
            val latitudeIx = cursor.getColumnIndex("latitude")
            val longitudeIx = cursor.getColumnIndex("longitude")
            while(cursor.moveToNext()){
                val addressFromDatabase = cursor.getString(addressIx)
                val latitudeFromDatabase = cursor.getDouble(latitudeIx)
                val longitudeFromDatabase = cursor.getDouble(longitudeIx)

                val myPlaceModel = PlaceModel(addressFromDatabase,latitudeFromDatabase,longitudeFromDatabase)

                //layoutla string list olarak gosteremioruz. Model olduundan custom layout yazıyoruz
                placesArray.add(myPlaceModel)


            }

            cursor.close()

        }catch (e:Exception){
            e.printStackTrace()
        }



        //Custom adapterı atadık
        val customAdapter = CustomAdapter(placesArray,this)
        placeListView.adapter =customAdapter

        //listeden tıklama ile mape git
        placeListView.setOnItemClickListener { parent, view, position, id ->
            val intent  = Intent(this@MainActivity,MapsActivity::class.java)
            intent.putExtra("info", "fromPlaceList")
            intent.putExtra("selectedPlace",placesArray.get(position))
            startActivity(intent)

        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.add_place,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.add_place_option){
            val intent = Intent(applicationContext,MapsActivity::class.java)
            intent.putExtra("info","fromMenu")
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }
}