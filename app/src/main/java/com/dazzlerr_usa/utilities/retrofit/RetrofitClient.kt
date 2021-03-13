package com.dazzlerr_usa.utilities.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.util.concurrent.TimeUnit


object RetrofitClient
{
    internal var retrofit: Retrofit? = null

    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor
    {
        val interceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { Timber.i(it) })
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    fun getClient(baseURL: String): Retrofit {
        if (retrofit == null)
        {
            retrofit = Retrofit.Builder()
                    .baseUrl(baseURL)
                    .client(provideOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }
        else
        {
            if (!retrofit!!.baseUrl().toString().equals(baseURL , ignoreCase = true))
            {
                retrofit = Retrofit.Builder()
                        .baseUrl(baseURL)
                        .client(provideOkHttpClient())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
            }
        }
        return retrofit!!
    }

    private fun provideOkHttpClient(): OkHttpClient
    {
        val okhttpClientBuilder = OkHttpClient.Builder()
        okhttpClientBuilder.connectTimeout(180, TimeUnit.SECONDS)
        okhttpClientBuilder.readTimeout(180, TimeUnit.SECONDS)
        okhttpClientBuilder.writeTimeout(180, TimeUnit.SECONDS)
        okhttpClientBuilder.addInterceptor(provideHttpLoggingInterceptor())
        return okhttpClientBuilder.build()
    }

}
