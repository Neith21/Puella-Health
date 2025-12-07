package com.puella_softworks.puellahealth.network

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {

    companion object {
        private const val BASE_URL = "x"

        private var apiService: ApiService? = null

        fun getApiService(context: Context): ApiService {
            if (apiService == null) {
                val client = OkHttpClient.Builder()
                    .addInterceptor(AuthInterceptor(context))
                    .build()

                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()

                apiService = retrofit.create(ApiService::class.java)
            }
            return apiService!!
        }
    }
}