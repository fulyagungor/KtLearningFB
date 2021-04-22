package com.fyprojects.kotlinartbook

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.media.Image
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.decodeBitmap
import kotlinx.android.synthetic.main.activity_add_art.*
import java.io.ByteArrayOutputStream
import java.util.jar.Manifest

class AddArtActivity : AppCompatActivity() {

    var permissionRequestNo = 1
    var galeryRequestNo = 2

    var selectedPictureUri : Uri? = null
    var selectedBitmapPitcure : Bitmap? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_art)

        val intent = intent
        val intentInfo = intent.getStringExtra("intentInfo")

        println("---------intentinfo:"+intentInfo)

        if(intentInfo.equals("new")){
            //ekran temizleme
            art_text.setText("")
            artist_text.setText("")
            year_text.setText("")
            save.visibility = View.VISIBLE

            val selectedImageBackground = BitmapFactory.decodeResource(applicationContext.resources,R.drawable.selectimage)
            imageButton.setImageBitmap(selectedImageBackground)

        }else{
            save.visibility = View.INVISIBLE
            val artId = intent.getIntExtra("artId",1) // alamiyosa default 1. satir gelsin

            val database = this.openOrCreateDatabase("ArtDatabase",Context.MODE_PRIVATE,null)
            val cursor = database.rawQuery("SELECT * FROM arts WHERE id=?", arrayOf(artId.toString()))

            val artIx = cursor.getColumnIndex("artname")
            val artistIx = cursor.getColumnIndex("artistname")
            val yearIx = cursor.getColumnIndex("year")
            val imageIx = cursor.getColumnIndex("image")


            while (cursor.moveToNext()) {
                art_text.setText(cursor.getString(artId))
                artist_text.setText(cursor.getString(artistIx))
                year_text.setText(cursor.getString(yearIx))

                val byteArray = cursor.getBlob(imageIx)
                val bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size) // bytearray kadar decode
                imageButton.setImageBitmap(bitmap)

            }
           cursor.close()




        }


    }

    fun save(view : View){
        val artistName = artist_text.text.toString()
        val artText = art_text.text.toString()
        val year =year_text.text.toString().toIntOrNull()

        if(selectedBitmapPitcure!=null) {

            //gorseli bitmapten byte arraye cevir. formatlari boyutu setle

            //sqllite da 1mb üstü olduğunda coker. blob kayit icin de uygun diil aslinda yinede kucuk boyutta kaydedilebilir
            //bitmap scale et
            val outputStream = ByteArrayOutputStream()
            val smallBitmap = makeSmallerBitmap(selectedBitmapPitcure!!, 300)
            selectedBitmapPitcure?.compress(Bitmap.CompressFormat.PNG, 50, outputStream)

            val byteArray = outputStream.toByteArray();

            try {


            val database  = this.openOrCreateDatabase("ArtDatabase",Context.MODE_PRIVATE,null)
            database.execSQL("CREATE TABLE IF NOT EXISTS arts( id INTEGER PRIMARY KEY ,artname VARCHAR,artistname VARCHAR, year  INTEGER, image BLOB)")

            val sqlString  = "INSERT INTO arts (artname,artistname,year,image) VALUES (?,?,?,?)"
            val statement = database.compileStatement(sqlString)
            statement.bindString(1,artText)
            statement.bindString(2,artistName)
            statement.bindString(3,year.toString())
            statement.bindBlob(4,byteArray)
            statement.execute()
            }catch (e : Exception){
                println(e.printStackTrace())
            }

            //burdaki aktiviteyi sonlandır.
            //finish() ana ekran guncellenmedii icin sildik

            val intent = Intent(this, MainActivity::class.java)
            //yeni activity oncesindeki tum aktivityleri kapat
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

        }
    }

    fun makeSmallerBitmap(image: Bitmap, maximumSize :Int) : Bitmap{
        var width =image.width
        var height = image.height

        val bitmapRatio : Double = width.toDouble() / height.toDouble();
        //genislik yukseklige bolunce 1den buyukse gorsel yatay
        if(bitmapRatio>1){
            width=maximumSize
            val scaledHeight = width/bitmapRatio
            height = scaledHeight.toInt()
        }else{
            height = maximumSize
            val scaledWidth = height*bitmapRatio
            width = maximumSize.toInt()
        }

        return Bitmap.createScaledBitmap(image,width,height,true)
    }




    fun selectImage(view: View){
    //API23 (Android 10 üstü) 22 altında bu izinleri istemek zorunlu diil.
        // ContextCompat : Daha oncekilerle uyumlu olmasi icin 23 oncesi cihazsa kontrol etmez, sonrasiysa checkpermission yapacak
        //izin vermemişse izin iste
    if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
        //requestpermission dizi bekliyor. Kotlinin güzelliginden biri array donusumleri. direk manifestten arrayof ile alırız
        ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),permissionRequestNo)
    }else{

        goToGalery()
    }

    }
    // adam onay verince onRequestPermissionsResult cagrilir. burdaki requestcode checkpermissionda tanımladigimiz requestcode
    //farkli yerden gelen izinlerin yonetimi icin requestcode
    //grandResults izin verilip verilmedigi burda baglanmistir. Otomatik gelcek. kontrol edicez
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode==permissionRequestNo){
            //dizide bişey varmı izinli mi
            if (grantResults.size >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                goToGalery()
            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    private fun goToGalery() {
        //galeriye giderken intent ile
        val intentToGalery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI) //cihazin hafizada gorselleri nerde tuttugu esternal_content_uri
        startActivityForResult(intentToGalery, galeryRequestNo)
    }


//galeriye gittikten sonra, gorsel seçili ise data uri icerir. yoksa null gelir
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == galeryRequestNo && resultCode == Activity.RESULT_OK && data!= null) {
            selectedPictureUri = data.data

            //Media.getBitmap deprecated ama yeni metod API27 oncesinde calismiyor o yuzden her ikiside lazim.
            //API 27 sonrasinda ImageDecode ile cozumlenir.

            try {

                if (Build.VERSION.SDK_INT < 28) {

                    // uri var, image i bitmape cevirecegiz. getbitmap ContentResolver ister. ContentResolver donusumu saglar
                    selectedBitmapPitcure = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedPictureUri)
                    //ekrandaki imageview e ata
                    imageButton.setImageBitmap(selectedBitmapPitcure)
                } else {
                    val source = ImageDecoder.createSource(this.contentResolver, selectedPictureUri!!)  //!! isareti null olmadigini check eder. extra kontrol
                    selectedBitmapPitcure = ImageDecoder.decodeBitmap(source)
                    imageButton.setImageBitmap(selectedBitmapPitcure)
                }

            } catch(e : Exception){

                println(e.printStackTrace())
            }

        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}