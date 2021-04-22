package com.fyprojects.fulyaroutes

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.custom_list_row.view.*


//Context :hangi activity, resource :hangi resource baglanacak, list :hangi liste baglanacak
class CustomAdapter(val placeList: ArrayList<PlaceModel>,val context: Activity) : ArrayAdapter<PlaceModel>(context, R.layout.custom_list_row,placeList) {

    //custom adapter'a baglanan contextin layout inflaterı alinir ve textview icindeki deger burada setlenir
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater = context.layoutInflater
        val customView = layoutInflater.inflate(R.layout.custom_list_row,null)
        customView.customListTextRow.text=placeList.get(position).address

        return customView
    }
}