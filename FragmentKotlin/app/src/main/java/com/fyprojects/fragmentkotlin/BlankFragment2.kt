package com.fyprojects.fragmentkotlin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class BlankFragment2 : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

/*Inflater'ı nezaman kullanıyoruz? Layout XML dosyasını kodda bağlarken kullanılır.
* Bu LayoutInflater , MenuInflater ya da farklı bir xml yapısında olabilir.  */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { //Bir view döndürüyor. RecyclerView'de buna benzerdi.
        // Inflate the layout for this fragment
        //layoutu fragent_blan2'e bağlıyor.
        return inflater.inflate(R.layout.fragment_blank2, container, false)
    }


}