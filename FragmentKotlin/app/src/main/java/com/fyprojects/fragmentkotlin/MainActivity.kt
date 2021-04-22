package com.fyprojects.fragmentkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    //Fragment kullanimi icin FragmentManager
    fun firstFragment(view: View){

        val fragmentManager = supportFragmentManager //JAva da getFragmentManager di
        val fragmentTransaction = fragmentManager.beginTransaction()

        //BlankFragement objesi olustur. Fragmanı frameLayout gosterecegi add/replace metodunda icin container atanir, firstFragementı gosterecek.
        val firstFragment = BlankFragment()
        fragmentTransaction.replace(R.id.frameLayoutId,firstFragment).commit()

    }

    fun secondFragment(view: View){

        val fragmentManager = supportFragmentManager //JAva da getFragmentManager di
        val fragmentTransaction = fragmentManager.beginTransaction()

        //BlankFragement objesi olustur. Fragmanı frameLayout gosterecegi add metodunda icin container atanir, firstFragementı gosterecek.
        val secondFragment = BlankFragment2()
        fragmentTransaction.replace(R.id.frameLayoutId,secondFragment).commit()
    }
}