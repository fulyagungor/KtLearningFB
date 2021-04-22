package com.fyprojects.kotlinartbook

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val artNameList = ArrayList<String>()
        val artIdList = ArrayList<Int>()

        //liste ile listviewi baglamak icin arrayadapter
        val arrayAdapter  = ArrayAdapter(this, android.R.layout.simple_list_item_1,artNameList)
        list_view.adapter = arrayAdapter

        try {
            val database = this.openOrCreateDatabase("ArtDatabase", Context.MODE_PRIVATE,null)
            val cursor = database.rawQuery("SELECT  * from arts",null)
            val artnameIndex = cursor.getColumnIndex("artname")
            val idIndex =cursor.getColumnIndex("id")

            while (cursor.moveToNext()){
                artNameList.add(cursor.getString(artnameIndex))
                artIdList.add(cursor.getInt(idIndex))
            }
            //veriler yenilenince listview'e koy
            arrayAdapter.notifyDataSetChanged()
            cursor.close()

        }catch (e: Exception){
            e.printStackTrace()
        }

        list_view.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val intent =  Intent(this,AddArtActivity::class.java)
            //menuden mi burdan mı geliyo ayristir
            intent.putExtra("intentInfo","old")
            intent.putExtra("artId",artIdList[position])
            startActivity(intent)


        }


    }


    //menumuzoption menu, 2fun kullanıyoruz
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //inflater kullanarak menu baglicaz

        val menuInflater = menuInflater  //app icin tekgecerli menuinflater var
        menuInflater.inflate(R.menu.add_art,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.add_art_item){
            val intent = Intent(this,AddArtActivity::class.java)
            intent.putExtra("intentInfo","new")
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }
}