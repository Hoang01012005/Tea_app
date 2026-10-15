package com.example.teacare.data.remote.api

import com.example.teacare.data.remote.dto.ApiClassesResponse
import com.example.teacare.data.remote.dto.ApiHealthResponse
import com.example.teacare.data.remote.dto.ApiPredictResponse
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface TeaCareApiService {

    @GET("health")
    suspend fun checkHealth(): ApiHealthResponse

    @GET("classes")
    suspend fun getClasses(): ApiClassesResponse

    @Multipart
    @POST("predict")
    suspend fun predictDisease(
        @Part file: MultipartBody.Part
    ): ApiPredictResponse
}
