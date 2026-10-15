package com.example.teacare.data.model

enum class TaskPriority(val label: String) {
    LOW("Thấp"),
    MEDIUM("Vừa"),
    HIGH("Khẩn cấp")
}

enum class TaskCategory(val label: String, val iconName: String) {
    WATERING("Tưới tiêu", "water"),
    FERTILIZING("Bón phân", "spa"),
    PRUNING("Đốn tỉa", "cut"),
    TREATMENT("Phun phòng trừ", "sanitizer"),
    HARVEST("Thu hái búp", "agriculture"),
    INSPECTION("Kiểm tra vườn", "search")
}

data class CareTask(
    val id: Long = 0,
    val title: String,
    val note: String,
    val dueDateMillis: Long,
    val priority: TaskPriority,
    val category: TaskCategory,
    val isCompleted: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
)
