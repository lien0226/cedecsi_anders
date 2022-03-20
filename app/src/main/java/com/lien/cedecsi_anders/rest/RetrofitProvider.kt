package com.lien.cedecsi_anders.rest

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitProvider {
    companion object{
        private const val baseUrl = "http://sunass.conceptomercado.com/sunass_rest/"
        private fun provideGson(): Gson {
            val builder = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            builder.setLenient().create()
            return builder.create()
        }

        private fun provideHttpClient(): OkHttpClient{
            val builder = OkHttpClient.Builder()
            return builder.build()
        }

        fun provideRetrofit(): Retrofit{
            val builder = Retrofit.Builder().baseUrl(baseUrl)
            builder.addConverterFactory(GsonConverterFactory.create(provideGson()))
            builder.client(provideHttpClient())
            return builder.build()
        }
    }
}