package com.example.teacare.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ApiHealthResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("model_loaded")
    val modelLoaded: Boolean,
    @SerializedName("num_classes")
    val numClasses: Int,
    @SerializedName("device")
    val device: String? = null,
    @SerializedName("model_path")
    val modelPath: String? = null
)

data class ApiClassesResponse(
    @SerializedName("classes")
    val classes: List<ApiClassItem>
)

data class ApiClassItem(
    @SerializedName("class_id")
    val classId: Int,
    @SerializedName("source_class")
    val sourceClass: String? = null,
    @SerializedName("class_name")
    val className: String,
    @SerializedName("disease_vi")
    val diseaseVi: String? = null,
    @SerializedName("is_healthy")
    val isHealthy: Boolean? = null
)

data class ApiPredictResponse(
    @SerializedName("class_id")
    val classId: Int,
    @SerializedName("class_name")
    val className: String,
    @SerializedName("disease_vi")
    val diseaseVi: String? = null,
    @SerializedName("confidence")
    val confidence: Float,
    @SerializedName("is_healthy")
    val isHealthy: Boolean,
    @SerializedName("top3_predictions")
    val top3Predictions: List<ApiPredictionItem> = emptyList(),
    @SerializedName("all_predictions")
    val allPredictions: List<ApiPredictionItem> = emptyList()
)

data class ApiPredictionItem(
    @SerializedName("class_id")
    val classId: Int,
    @SerializedName("class_name")
    val className: String,
    @SerializedName("disease_vi")
    val diseaseVi: String? = null,
    @SerializedName("confidence")
    val confidence: Float
)
