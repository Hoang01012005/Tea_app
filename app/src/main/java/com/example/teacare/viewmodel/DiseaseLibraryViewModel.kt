package com.example.teacare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacare.data.model.Disease
import com.example.teacare.data.model.DiseaseCategory
import com.example.teacare.data.model.DiseaseSeverity
import com.example.teacare.data.repository.DiseaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DiseaseLibraryUiState(
    val searchQuery: String = "",
    val selectedCategory: DiseaseCategory? = null,
    val selectedSeverity: DiseaseSeverity? = null,
    val diseases: List<Disease> = emptyList(),
    val filteredDiseases: List<Disease> = emptyList()
)

class DiseaseLibraryViewModel(
    private val diseaseRepository: DiseaseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiseaseLibraryUiState())
    val uiState: StateFlow<DiseaseLibraryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            diseaseRepository.getAllDiseases().collect { all ->
                _uiState.value = _uiState.value.copy(
                    diseases = all,
                    filteredDiseases = all
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    fun onCategorySelected(category: DiseaseCategory?) {
        val newCategory = if (_uiState.value.selectedCategory == category) null else category
        _uiState.value = _uiState.value.copy(selectedCategory = newCategory)
        applyFilters()
    }

    fun onSeveritySelected(severity: DiseaseSeverity?) {
        val newSeverity = if (_uiState.value.selectedSeverity == severity) null else severity
        _uiState.value = _uiState.value.copy(selectedSeverity = newSeverity)
        applyFilters()
    }

    private fun applyFilters() {
        val current = _uiState.value
        val result = diseaseRepository.searchDiseases(
            query = current.searchQuery,
            category = current.selectedCategory,
            severity = current.selectedSeverity
        )
        _uiState.value = current.copy(filteredDiseases = result)
    }

    fun getDiseaseById(id: String): Disease? {
        return diseaseRepository.getDiseaseById(id)
    }
}
