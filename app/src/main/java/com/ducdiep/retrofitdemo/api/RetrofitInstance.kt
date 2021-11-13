package com.ducdiep.retrofitdemo.api

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val baseUrl = "https://curd-demo.herokuapp.com"

class RetrofitInstance {
    companion object{
        fun getInstance():Retrofit{
            var gson = GsonBuilder()
                .setDateFormat("YYYY-MM-dd HH:mm:ss").create()

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
    }
}