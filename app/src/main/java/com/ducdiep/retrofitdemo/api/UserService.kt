package com.ducdiep.retrofitdemo.api

import android.content.Context
import com.ducdiep.retrofitdemo.models.User
import com.google.gson.GsonBuilder
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface UserService {

    //@Query("key") String key truyền param
    //@Multipart annotation dành cho API hỗ trợ data up lên dạng multipart/form data
    //@Part("key") Requestbody value up từng thuộc tính lên
    //@Part MultiPartBody.Part avt up file lên server
    //@Body truyền 1 object lên
    //@Path truyền dữ liệu lên url endpoint
    //RequestBody laf 1 param khi put multipart/form-data

    @GET("/api/accounts")
    fun getAllUser():Call<ArrayList<User>>

    @FormUrlEncoded
    @POST("/api/accounts")
    fun addNewUser(@Field("username" ) userName:String, @Field("password") password:String):Call<User>

    @Multipart
    @PATCH("/api/accounts/{id}")
    fun updateAvatar(@Path("id") id:Int,@Part avt:MultipartBody.Part):Call<User>

    @DELETE("/api/accounts/{id}")
    fun deleteUser(@Path("id") id:Int):Call<User>
}