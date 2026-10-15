package com.example.teacare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacare.data.local.preferences.UserPreferences
import com.example.teacare.data.model.CareTask
import com.example.teacare.data.model.Disease
import com.example.teacare.data.model.TeaTip
import com.example.teacare.data.repository.CareTaskRepository
import com.example.teacare.data.repository.DiseaseRepository
import com.example.teacare.data.repository.ScanHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val userName: String = "Người làm vườn",
    val gardenName: String = "Đồi chè Tân Cương",
    val healthIndexPercent: Int = 94,
    val pendingTasksCount: Int = 0,
    val totalTasksCount: Int = 0,
    val totalScansCount: Int = 0,
    val upcomingTasks: List<CareTask> = emptyList(),
    val commonDiseases: List<Disease> = emptyList(),
    val dailyTips: List<TeaTip> = emptyList()
)

class HomeViewModel(
    private val careTaskRepository: CareTaskRepository,
    private val scanHistoryRepository: ScanHistoryRepository,
    private val diseaseRepository: DiseaseRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            userPreferences.userName.collect { name ->
                _uiState.value = _uiState.value.copy(userName = name)
            }
        }

        viewModelScope.launch {
            userPreferences.gardenName.collect { garden ->
                _uiState.value = _uiState.value.copy(gardenName = garden)
            }
        }

        viewModelScope.launch {
            careTaskRepository.pendingTasks.collect { tasks ->
                _uiState.value = _uiState.value.copy(
                    upcomingTasks = tasks.take(3),
                    pendingTasksCount = tasks.size
                )
            }
        }

        viewModelScope.launch {
            careTaskRepository.totalTaskCount.collect { count ->
                _uiState.value = _uiState.value.copy(totalTasksCount = count)
            }
        }

        viewModelScope.launch {
            scanHistoryRepository.totalScansCount.collect { count ->
                _uiState.value = _uiState.value.copy(totalScansCount = count)
            }
        }

        viewModelScope.launch {
            diseaseRepository.getAllDiseases().collect { list ->
                _uiState.value = _uiState.value.copy(
                    commonDiseases = list.take(4),
                    dailyTips = diseaseRepository.getDailyTips()
                )
            }
        }
    }

    fun toggleTask(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            careTaskRepository.toggleTaskStatus(taskId, isCompleted)
        }
    }
}
