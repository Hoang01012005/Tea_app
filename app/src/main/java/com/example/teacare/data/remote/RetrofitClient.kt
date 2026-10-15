package com.example.teacare.data.remote

import com.example.teacare.data.remote.api.TeaCareApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Địa chỉ IP mặc định:
    // 10.0.2.2:8000 là loopback máy tính Windows từ Android Emulator
    // Đối với điện thoại thật: cấu hình theo IP mạng LAN ví dụ http://192.168.1.x:8000/
    const val DEFAULT_EMULATOR_BASE_URL = "http://10.0.2.2:8000/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    @Volatile
    private var currentBaseUrl: String = DEFAULT_EMULATOR_BASE_URL

    @Volatile
    private var apiService: TeaCareApiService? = null

    fun getApiService(baseUrl: String = currentBaseUrl): TeaCareApiService {
        val normalizedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        if (apiService == null || currentBaseUrl != normalizedUrl) {
            synchronized(this) {
                if (apiService == null || currentBaseUrl != normalizedUrl) {
                    currentBaseUrl = normalizedUrl
                    val retrofit = Retrofit.Builder()
                        .baseUrl(normalizedUrl)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                    apiService = retrofit.create(TeaCareApiService::class.java)
                }
            }
        }
        return apiService!!
    }

    fun updateBaseUrl(newBaseUrl: String): TeaCareApiService {
        val normalizedUrl = if (newBaseUrl.endsWith("/")) newBaseUrl else "$newBaseUrl/"
        return getApiService(normalizedUrl)
    }
}
