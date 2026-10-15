package com.example.teacare.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.teacare.data.local.preferences.UserPreferences
import com.example.teacare.data.model.Disease
import com.example.teacare.data.model.ScanResult
import com.example.teacare.data.model.TeaLeafBDClassMapper
import com.example.teacare.data.remote.RetrofitClient
import com.example.teacare.data.remote.dto.ApiPredictResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

sealed interface DetectionEngineResult {
    data class Success(
        val scanResult: ScanResult,
        val matchedDisease: Disease,
        val inferenceTimeMs: Long,
        val topPredictions: List<PredictionProbability>,
        val englishClassName: String,
        val vietnameseClassName: String,
        val isHealthy: Boolean
    ) : DetectionEngineResult

    data class Error(
        val message: String,
        val isNetworkError: Boolean = false
    ) : DetectionEngineResult
}

data class PredictionProbability(
    val englishName: String,
    val vietnameseName: String,
    val probability: Float
)

interface TeaDetectionEngine {
    suspend fun analyzeLeafImage(imageUri: String): DetectionEngineResult
    suspend fun checkBackendHealth(): Boolean
}

class FastApiTeaDetectionEngine(
    private val context: Context,
    private val diseaseRepository: DiseaseRepository,
    private val userPreferences: UserPreferences
) : TeaDetectionEngine {

    override suspend fun checkBackendHealth(): Boolean = withContext(Dispatchers.IO) {
        try {
            val baseUrl = userPreferences.apiBaseUrl.first()
            val apiService = RetrofitClient.getApiService(baseUrl)
            val health = apiService.checkHealth()
            health.status.equals("ok", ignoreCase = true)
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun analyzeLeafImage(imageUri: String): DetectionEngineResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        val baseUrl = userPreferences.apiBaseUrl.first()

        // 1. Đọc dữ liệu ảnh từ Uri
        val imageBytes = try {
            readImageBytes(imageUri)
        } catch (e: Exception) {
            return@withContext DetectionEngineResult.Error("Không thể đọc tệp ảnh: ${e.localizedMessage}")
        }

        if (imageBytes.isEmpty()) {
            return@withContext DetectionEngineResult.Error("Dữ liệu ảnh trống hoặc không hợp lệ.")
        }

        // 2. Tạo MultipartBody
        val requestBody = imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
        val multipartPart = MultipartBody.Part.createFormData("file", "leaf_upload.jpg", requestBody)

        // 3. Gửi request tới FastAPI Backend
        val apiService = RetrofitClient.getApiService(baseUrl)
        val apiResponse: ApiPredictResponse = try {
            apiService.predictDisease(multipartPart)
        } catch (e: ConnectException) {
            return@withContext DetectionEngineResult.Error(
                message = "Không thể kết nối đến máy chủ FastAPI tại $baseUrl.\n\nHãy đảm bảo bạn đã mở server trên máy tính (chạy file run_server.bat trong thư mục fastapi_server).",
                isNetworkError = true
            )
        } catch (e: SocketTimeoutException) {
            return@withContext DetectionEngineResult.Error(
                message = "Kết nối đến máy chủ bị quá thời gian (Timeout). Vui lòng thử lại.",
                isNetworkError = true
            )
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string() ?: ""
            return@withContext DetectionEngineResult.Error(
                message = "Lỗi máy chủ (HTTP ${e.code()}): $errorBody",
                isNetworkError = true
            )
        } catch (e: IOException) {
            return@withContext DetectionEngineResult.Error(
                message = "Lỗi kết nối mạng: ${e.localizedMessage}\nKiểm tra lại cấu hình địa chỉ IP máy chủ.",
                isNetworkError = true
            )
        } catch (e: Exception) {
            return@withContext DetectionEngineResult.Error("Đã xảy ra lỗi không xác định: ${e.localizedMessage}")
        }

        val inferenceTime = System.currentTimeMillis() - startTime

        // 4. Ánh xạ kết quả: ưu tiên nhãn tiếng Việt trực tiếp từ API trả về
        val rawEnglishName = apiResponse.className
        val vietnameseName = apiResponse.diseaseVi?.takeIf { it.isNotBlank() }
            ?: TeaLeafBDClassMapper.toVietnameseName(rawEnglishName)
        val isHealthy = apiResponse.isHealthy || TeaLeafBDClassMapper.isHealthy(rawEnglishName)
        val severity = TeaLeafBDClassMapper.getSeverity(rawEnglishName)

        val matchedDisease = diseaseRepository.getDiseaseByClassName(rawEnglishName)

        val predictionsList = if (apiResponse.allPredictions.isNotEmpty()) {
            apiResponse.allPredictions
        } else {
            apiResponse.top3Predictions
        }

        val topPredictions = predictionsList.map { pred ->
            PredictionProbability(
                englishName = pred.className,
                vietnameseName = pred.diseaseVi?.takeIf { it.isNotBlank() }
                    ?: TeaLeafBDClassMapper.toVietnameseName(pred.className),
                probability = pred.confidence
            )
        }

        val scanResult = ScanResult(
            id = 0,
            imageUri = imageUri,
            diseaseId = matchedDisease.id,
            diseaseName = vietnameseName,
            confidence = apiResponse.confidence,
            severity = severity,
            summary = matchedDisease.shortDescription,
            symptoms = matchedDisease.symptoms.firstOrNull() ?: TeaLeafBDClassMapper.getQuickSummary(rawEnglishName),
            treatment = matchedDisease.chemicalControl.firstOrNull()
                ?: matchedDisease.biologicalControl.firstOrNull()
                ?: TeaLeafBDClassMapper.getQuickTreatment(rawEnglishName),
            scannedAtMillis = System.currentTimeMillis(),
            isDemoMode = false // Kết quả thực tế từ mô hình DenseNet-121 được phục vụ qua FastAPI
        )

        DetectionEngineResult.Success(
            scanResult = scanResult,
            matchedDisease = matchedDisease,
            inferenceTimeMs = inferenceTime,
            topPredictions = topPredictions,
            englishClassName = rawEnglishName,
            vietnameseClassName = vietnameseName,
            isHealthy = isHealthy
        )
    }

    private fun readImageBytes(uriString: String): ByteArray {
        val uri = Uri.parse(uriString)
        if (uri.scheme == "file") {
            val file = File(uri.path ?: "")
            if (file.exists()) {
                return file.readBytes()
            }
        }

        if (uri.scheme == "content") {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                return inputStream.readBytes()
            }
        }

        // Trường hợp ảnh chụp từ camera tạm hoặc file nội bộ
        val localFile = File(uriString)
        if (localFile.exists()) {
            return localFile.readBytes()
        }

        // Tạo bitmap mẫu nếu uri là demo tag
        val bmp = Bitmap.createBitmap(224, 224, Bitmap.Config.ARGB_8888)
        val stream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        return stream.toByteArray()
    }
}
