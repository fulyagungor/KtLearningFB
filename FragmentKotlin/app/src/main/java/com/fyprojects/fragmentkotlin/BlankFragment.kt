package com.fyprojects.fragmentkotlin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/*Fragmanlarda oncreateView önemli. LayoutInflater'a fragman baglanir.
* Activitylerde ise lifecycle farklı. onCreate'de layout.activity setContentView(R.layout.activity_main)*/
class BlankFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    /*Inflater'ı nezaman kullanıyoruz? Layout XML dosyasını kodda bağlarken kullanılır.
* Bu LayoutInflater , MenuInflater ya da farklı bir xml yapısında olabilir.  */


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment,
        //layoutu fragent_blank'e bağlıyor.
        return inflater.inflate(R.layout.fragment_blank, container, false)
    }


}