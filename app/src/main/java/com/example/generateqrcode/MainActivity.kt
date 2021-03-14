package com.example.generateqrcode

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() ,View.OnClickListener {
    lateinit var qrimage:ImageView
    lateinit var generate:Button
    lateinit var save:Button
    lateinit var value:EditText
    lateinit var bitmap: Bitmap

    var RequestCameraPermissionID:Int =1001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
  qrimage=findViewById(R.id.qrimage)
        generate=findViewById(R.id.generate)
        value=findViewById(R.id.value)
        save=findViewById(R.id.save)
        generate.setOnClickListener(this)
        save.setOnClickListener(this)
    }

     fun generateQRCode(text: String): Bitmap {
        val width = 500
        val height = 500
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val codeWriter = MultiFormatWriter()
        try {
            val bitMatrix = codeWriter.encode(text, BarcodeFormat.QR_CODE, width, height)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        } catch (e: WriterException) {
            Log.d("TAG", "generateQRCode: ${e.message}")
        }
        return bitmap
    }

    override fun onClick(p0: View?) {
        val item_id = p0?.id
        when (item_id) {
            R.id.generate -> generatecode()
            R.id.save -> saveQr()
        }
    }

    private fun saveQr() {
        if (ActivityCompat.checkSelfPermission(
                        getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) !== PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    RequestCameraPermissionID
            )
            return
        }
       var  pictureFile : File? = getOutputMediaFile()
        if (pictureFile == null)
        {
            Log.d("TAG",
                    "Error creating media file, check storage permissions: ")// e.getMessage());
            return
        }
        try
        {
            val fos = FileOutputStream(pictureFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos)
            fos.close()
        }
        catch (e: FileNotFoundException) {
            Log.d("TAG", "File not found: " + e.message)
        }
        catch (e: IOException) {
            Log.d("TAG", "Error accessing file: " + e.message)
        }
    }
    private fun getOutputMediaFile(): File? {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        val mediaStorageDir = File((Environment.getExternalStorageDirectory().toString()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files"))
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdirs())
            {
                return null
            }
        }
        // Create a media file name
        val timeStamp = SimpleDateFormat("ddMMyyyy_HHmm").format(Date())
        val mediaFile:File
        val mImageName = "MI_" + timeStamp + ".jpg"
        mediaFile = File(mediaStorageDir.getPath() + File.separator + mImageName)
        return mediaFile
    }

    private fun generatecode() {
        if(value.text.isEmpty() == false){
            bitmap  = generateQRCode(value.text.toString())
            qrimage.setImageBitmap(bitmap)

        }
        else {
        Toast.makeText(applicationContext  , "Enter Value First ",Toast.LENGTH_SHORT).show()
        }
    }
    override fun onRequestPermissionsResult(requestCode:Int, @NonNull permissions:Array<String>, @NonNull grantResults:IntArray) {
        when (requestCode) {
            RequestCameraPermissionID -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED)
                    {
                        return
                    }
                    try
                    {
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

}