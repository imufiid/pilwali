package com.mufiid.pilwali2020.ui.activities

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.locationbasedservice.MySimpleLocation
import com.mufiid.pilwali2020.R
import com.mufiid.pilwali2020.models.Tps
import com.mufiid.pilwali2020.presenters.TpsPresenter
import com.mufiid.pilwali2020.utils.Constants
import com.mufiid.pilwali2020.views.ITpsView
import kotlinx.android.synthetic.main.activity_tps.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.parse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

@Suppress("DEPRECATION")
class TpsActivity : AppCompatActivity(), MySimpleLocation.MySimpleLocationCallback, ITpsView {
    private val REQUEST_IMAGE_CAPTURE = 1
    private var alamat_tps = String
    private var loading: ProgressDialog? = null
    private var tpsPresenter: TpsPresenter? = null
    private var fotoTPS: Bitmap? = null

    // pertama
    lateinit var mySimpleLocation: MySimpleLocation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tps)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "TPS"

        // init progressbar
        loading = ProgressDialog(this)

         tpsPresenter = TpsPresenter(this)

        open_camera.setOnClickListener {
            captureTPS()
        }

        btn_simpan.setOnClickListener {
            doSimpan()
        }

        getPermission()
    }

    private fun doSimpan() {
        val pictFromBitmap = createFile(fotoTPS)
        val reqFile: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), pictFromBitmap!!)
        val part = MultipartBody.Part.createFormData("foto", pictFromBitmap.name, reqFile)

        val lat = RequestBody.create("text/plain".toMediaTypeOrNull(), latitude.text.toString())
        val long = RequestBody.create("text/plain".toMediaTypeOrNull(), longitude.text.toString())
        val username = RequestBody.create("text/plain".toMediaTypeOrNull(),  Constants.getUsername(this))
        val id_tps = RequestBody.create("text/plain".toMediaTypeOrNull(), Constants.getIDTps(this))
        val form_page = RequestBody.create("text/plain".toMediaTypeOrNull(), "2")


        tpsPresenter?.postData(id_tps, form_page, part, lat, long, username)
    }

    /**
    * function to create file
     * @author imam mufiid
     * @param bitmap => foto TPS
     *
    * */
    private fun createFile(bitmap: Bitmap?): File? {
        val file = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            , System.currentTimeMillis().toString() + "_image.jpg"
        )

        val bos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val bitmapData: ByteArray = bos.toByteArray()

        //write the bytes in file
        try {
            Log.d("PATH FILE", file.absolutePath)
            val fos = FileOutputStream(file)
            fos.write(bitmapData)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_refresh_tps, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp()
        onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refresh -> {
                getPermission()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            fotoTPS = imageBitmap
            image_tps.setImageBitmap(imageBitmap)
        }
    }

    private fun captureTPS() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun getPermission() {
        // check permission
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    1
                )
            } else {
                mySimpleLocation = MySimpleLocation(this, this)
                mySimpleLocation.checkLocationSetting(this)

                // show shimmer
                mShimmerViewContainer.startShimmer()
                mShimmerViewContainer.visibility = View.VISIBLE
                layout_latitude.visibility = View.GONE
                layout_longitude.visibility = View.GONE
            }
        } else {
            mySimpleLocation = MySimpleLocation(this, this)
            mySimpleLocation.checkLocationSetting(this)

            // show shimmer
            mShimmerViewContainer.startShimmer()
            mShimmerViewContainer.visibility = View.VISIBLE
            layout_latitude.visibility = View.GONE
            layout_longitude.visibility = View.GONE
        }
    }

    override fun getLocation(location: Location) {
        val lat = location.latitude
        val long = location.longitude
        val lati = lat.toString()
        val longi = long.toString()
        Log.d("MAINACTIVITY", "$lat $long")

        GlobalScope.launch(Dispatchers.IO) {
            val geoCoder = Geocoder(this@TpsActivity, Locale.getDefault())
            val addresses = geoCoder.getFromLocation(lat, long, 1)

            val address = if (addresses != null && addresses.size != 0) {
                val fullAddress = addresses[0].getAddressLine(0)
                fullAddress.plus("")
            } else {
                "Addresses Not Found!"
            }

            launch(Dispatchers.Main) {
                try {
                    Log.d("ADDRESS", address)

                    // stop shimmer
                    mShimmerViewContainer.stopShimmer()
                    mShimmerViewContainer.visibility = View.GONE
                    layout_latitude.visibility = View.VISIBLE
                    layout_longitude.visibility = View.VISIBLE

                    et_alamat_tps.setText(address)
                    latitude.setText(lati)
                    longitude.setText(longi)

                    //If you want to stop get your location on first result
                    mySimpleLocation.stopGetLocation()
                } catch (e: Exception) {
                    Log.e("MAINACTIVITY", e.message.toString())
                }
            }


        }
    }

    override fun isLoadingTps(state: Int?) {
        loading?.setMessage("Mohon tunggu sebentar...")
        loading?.show()
    }

    override fun hideLoadingTps(state: Int?) {
        loading?.dismiss()
    }

    override fun getDataTps(message: String?, data: Tps) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun failedGetDataTps(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun messageSuccess(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun messageFailed(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}