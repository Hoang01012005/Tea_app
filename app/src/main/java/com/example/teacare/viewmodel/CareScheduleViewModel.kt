package com.example.teacare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacare.data.model.CareTask
import com.example.teacare.data.model.TaskCategory
import com.example.teacare.data.model.TaskPriority
import com.example.teacare.data.repository.CareTaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class TaskFilterTab(val label: String) {
    ALL("Tất cả"),
    PENDING("Cần làm"),
    COMPLETED("Đã xong")
}

data class CareScheduleUiState(
    val currentTab: TaskFilterTab = TaskFilterTab.ALL,
    val allTasks: List<CareTask> = emptyList(),
    val filteredTasks: List<CareTask> = emptyList(),
    val isAddEditDialogOpen: Boolean = false,
    val taskToEdit: CareTask? = null
)

class CareScheduleViewModel(
    private val careTaskRepository: CareTaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CareScheduleUiState())
    val uiState: StateFlow<CareScheduleUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            careTaskRepository.allTasks.collect { tasks ->
                _uiState.value = _uiState.value.copy(allTasks = tasks)
                applyTabFilter()
            }
        }
    }

    fun onTabSelected(tab: TaskFilterTab) {
        _uiState.value = _uiState.value.copy(currentTab = tab)
        applyTabFilter()
    }

    private fun applyTabFilter() {
        val current = _uiState.value
        val filtered = when (current.currentTab) {
            TaskFilterTab.ALL -> current.allTasks
            TaskFilterTab.PENDING -> current.allTasks.filter { !it.isCompleted }
            TaskFilterTab.COMPLETED -> current.allTasks.filter { it.isCompleted }
        }
        _uiState.value = current.copy(filteredTasks = filtered)
    }

    fun toggleTask(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            careTaskRepository.toggleTaskStatus(taskId, isCompleted)
        }
    }

    fun deleteTask(task: CareTask) {
        viewModelScope.launch {
            careTaskRepository.deleteTask(task)
        }
    }

    fun openAddTaskDialog() {
        _uiState.value = _uiState.value.copy(
            isAddEditDialogOpen = true,
            taskToEdit = null
        )
    }

    fun openEditTaskDialog(task: CareTask) {
        _uiState.value = _uiState.value.copy(
            isAddEditDialogOpen = true,
            taskToEdit = task
        )
    }

    fun dismissAddEditDialog() {
        _uiState.value = _uiState.value.copy(
            isAddEditDialogOpen = false,
            taskToEdit = null
        )
    }

    fun saveTask(
        title: String,
        note: String,
        dueDateMillis: Long,
        priority: TaskPriority,
        category: TaskCategory
    ) {
        viewModelScope.launch {
            val existing = _uiState.value.taskToEdit
            if (existing != null) {
                val updated = existing.copy(
                    title = title,
                    note = note,
                    dueDateMillis = dueDateMillis,
                    priority = priority,
                    category = category
                )
                careTaskRepository.updateTask(updated)
            } else {
                val newTask = CareTask(
                    title = title,
                    note = note,
                    dueDateMillis = dueDateMillis,
                    priority = priority,
                    category = category,
                    isCompleted = false
                )
                careTaskRepository.addTask(newTask)
            }
            dismissAddEditDialog()
        }
    }
}
