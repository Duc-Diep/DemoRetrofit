package com.ducdiep.retrofitdemo

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.ducdiep.retrofitdemo.api.RetrofitInstance
import com.ducdiep.retrofitdemo.api.UserService
import com.ducdiep.retrofitdemo.models.User
import com.ducdiep.retrofitdemo.ultil.RealPathUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse
import java.io.File

const val MY_REQUEST_CODE = 1

class MainActivity : AppCompatActivity() {
    lateinit var apiInstance: UserService
    var mUri:Uri? = null
    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent(),
        ActivityResultCallback {
            mUri = it
            img_from_gallery.setImageURI(it)
        })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        apiInstance = RetrofitInstance.getInstance().create(UserService::class.java)
        btn_get.setOnClickListener {
            getAllUser()
        }

        btn_post.setOnClickListener {
            createNewUser()
        }
        btn_delete.setOnClickListener {
            deleteUser()
        }
        btn_choose_image.setOnClickListener {
            requestPermission()
        }
        btn_upload_image.setOnClickListener {
            uploadImage()
        }
    }

    private fun uploadImage() {
        progress_bar.visibility = View.VISIBLE
        var realPath = RealPathUtil.getRealPath(this, mUri!!)
        Log.d("realpath", "uploadImage: $realPath")
        var file = File(realPath)
        var requestBodyAvt = RequestBody.create(MediaType.parse("multipart/form-data"),file)
        var multiPartBody = MultipartBody.Part.createFormData("avt",file.name,requestBodyAvt)
        GlobalScope.launch(Dispatchers.IO) {
            var response = apiInstance.updateAvatar(3,multiPartBody).awaitResponse()
            if (response.isSuccessful){
                var user = response.body()
                Log.d("realpath", "uploadImage: $user")
                if (user!=null){
                    withContext(Dispatchers.Main){
                        Glide.with(this@MainActivity).load(user.linkAvt).into(img_from_sever)
                        progress_bar.visibility = View.GONE
                    }
                }

            }
        }
    }


    private fun deleteUser() {
        GlobalScope.launch(Dispatchers.IO) {
            var response = apiInstance.deleteUser(3).awaitResponse()
            if (response.isSuccessful) {
                Toast.makeText(this@MainActivity, "Delete Success", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Delete failure", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createNewUser() {

        GlobalScope.launch(Dispatchers.IO) {
            var response = apiInstance.addNewUser("Hihi", "asdfg").awaitResponse()
            if (response.isSuccessful) {
                var data = response.body()
                Log.d("Hihi", "createNewUser: $data")
            }
        }
    }

    private fun getAllUser() {
        progress_bar.visibility = View.VISIBLE
        GlobalScope.launch(Dispatchers.IO) {
            var response = apiInstance.getAllUser().awaitResponse()
            if (response.isSuccessful) {
                val data = response.body()
                Log.d("Hihi", "getAllUser: $data")
                withContext(Dispatchers.Main) {
                    edt_id.setText(data!![0].id.toString())
                    edt_name.setText(data!![0].username)
                    edt_password.setText(data!![0].password)
                    progress_bar.visibility = View.GONE
                }
            }

        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                openGallery()
            }else{
                var permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permission, MY_REQUEST_CODE)
            }

        }else{
            openGallery()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun openGallery() {
        getImage.launch("image/*")
    }
}