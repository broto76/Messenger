package com.broto.messenger.retrofitServices

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Utility {
    companion object {
        fun getRetrofitService() : Retrofit {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://messenger-node-server.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit

        }
    }
}