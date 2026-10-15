package com.example.teacare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacare.data.local.preferences.UserPreferences
import com.example.teacare.data.model.CareTask
import com.example.teacare.data.model.Disease
import com.example.teacare.data.model.ScanResult
import com.example.teacare.data.model.TaskCategory
import com.example.teacare.data.model.TaskPriority
import com.example.teacare.data.remote.RetrofitClient
import com.example.teacare.data.repository.CareTaskRepository
import com.example.teacare.data.repository.DetectionEngineResult
import com.example.teacare.data.repository.PredictionProbability
import com.example.teacare.data.repository.ScanHistoryRepository
import com.example.teacare.data.repository.TeaDetectionEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed interface DetectionState {
    object Idle : DetectionState
    data class Analyzing(val stepMessage: String) : DetectionState
    data class Success(
        val scanResult: ScanResult,
        val matchedDisease: Disease,
        val inferenceTimeMs: Long,
        val topPredictions: List<PredictionProbability>,
        val englishClassName: String,
        val vietnameseClassName: String,
        val isHealthy: Boolean,
        val isSavedToHistory: Boolean = false,
        val isTaskCreated: Boolean = false
    ) : DetectionState
    data class Error(
        val errorMessage: String,
        val isNetworkError: Boolean = false
    ) : DetectionState
}

class DetectionViewModel(
    private val detectionEngine: TeaDetectionEngine,
    private val scanHistoryRepository: ScanHistoryRepository,
    private val careTaskRepository: CareTaskRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _detectionState = MutableStateFlow<DetectionState>(DetectionState.Idle)
    val detectionState: StateFlow<DetectionState> = _detectionState.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<String?>(null)
    val selectedImageUri: StateFlow<String?> = _selectedImageUri.asStateFlow()

    private val _apiBaseUrl = MutableStateFlow(RetrofitClient.DEFAULT_EMULATOR_BASE_URL)
    val apiBaseUrl: StateFlow<String> = _apiBaseUrl.asStateFlow()

    private val _isBackendOnline = MutableStateFlow<Boolean?>(null)
    val isBackendOnline: StateFlow<Boolean?> = _isBackendOnline.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferences.apiBaseUrl.collect { url ->
                _apiBaseUrl.value = url
                checkBackendHealth()
            }
        }
    }

    fun checkBackendHealth() {
        viewModelScope.launch {
            val isOnline = detectionEngine.checkBackendHealth()
            _isBackendOnline.value = isOnline
        }
    }

    fun updateBaseUrl(newUrl: String) {
        viewModelScope.launch {
            val clean = newUrl.trim()
            if (clean.isNotBlank()) {
                userPreferences.setApiBaseUrl(clean)
                _apiBaseUrl.value = clean
                checkBackendHealth()
            }
        }
    }

    fun onImageSelected(uri: String) {
        _selectedImageUri.value = uri
        _detectionState.value = DetectionState.Idle
    }

    fun startAnalysis() {
        val uri = _selectedImageUri.value ?: return

        viewModelScope.launch {
            _detectionState.value = DetectionState.Analyzing("Đang kết nối FastAPI backend...")

            when (val result = detectionEngine.analyzeLeafImage(uri)) {
                is DetectionEngineResult.Success -> {
                    // Tự động lưu vào lịch sử chẩn đoán
                    val savedId = scanHistoryRepository.saveScan(result.scanResult)
                    val updatedScanResult = result.scanResult.copy(id = savedId)

                    _detectionState.value = DetectionState.Success(
                        scanResult = updatedScanResult,
                        matchedDisease = result.matchedDisease,
                        inferenceTimeMs = result.inferenceTimeMs,
                        topPredictions = result.topPredictions,
                        englishClassName = result.englishClassName,
                        vietnameseClassName = result.vietnameseClassName,
                        isHealthy = result.isHealthy,
                        isSavedToHistory = true
                    )
                    _isBackendOnline.value = true
                }
                is DetectionEngineResult.Error -> {
                    _detectionState.value = DetectionState.Error(
                        errorMessage = result.message,
                        isNetworkError = result.isNetworkError
                    )
                    if (result.isNetworkError) {
                        _isBackendOnline.value = false
                    }
                }
            }
        }
    }

    fun retryAnalysis() {
        startAnalysis()
    }

    fun createCareTaskFromDisease(disease: Disease) {
        val currentState = _detectionState.value
        if (currentState is DetectionState.Success) {
            viewModelScope.launch {
                val newTask = CareTask(
                    title = "Điều trị ${disease.vietnameseName}",
                    note = disease.chemicalControl.firstOrNull()
                        ?: disease.biologicalControl.firstOrNull()
                        ?: "Theo dõi sát sao triệu chứng bệnh trong 3-5 ngày.",
                    dueDateMillis = System.currentTimeMillis() + 86400000L * 1,
                    priority = TaskPriority.HIGH,
                    category = TaskCategory.TREATMENT,
                    isCompleted = false
                )
                careTaskRepository.addTask(newTask)
                _detectionState.value = currentState.copy(isTaskCreated = true)
            }
        }
    }

    fun resetDetection() {
        _selectedImageUri.value = null
        _detectionState.value = DetectionState.Idle
    }
}
