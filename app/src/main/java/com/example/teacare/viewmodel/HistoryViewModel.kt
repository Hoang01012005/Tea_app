package com.example.teacare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacare.data.model.ScanResult
import com.example.teacare.data.repository.ScanHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HistoryUiState(
    val scans: List<ScanResult> = emptyList(),
    val selectedScan: ScanResult? = null
)

class HistoryViewModel(
    private val scanHistoryRepository: ScanHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            scanHistoryRepository.allScans.collect { list ->
                _uiState.value = _uiState.value.copy(scans = list)
            }
        }
    }

    fun selectScan(scan: ScanResult?) {
        _uiState.value = _uiState.value.copy(selectedScan = scan)
    }

    fun deleteScan(scan: ScanResult) {
        viewModelScope.launch {
            scanHistoryRepository.deleteScan(scan)
            if (_uiState.value.selectedScan?.id == scan.id) {
                _uiState.value = _uiState.value.copy(selectedScan = null)
            }
        }
    }
}
