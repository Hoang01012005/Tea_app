package com.example.teacare.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.teacare.data.model.CareTask
import com.example.teacare.data.model.TaskCategory
import com.example.teacare.data.model.TaskPriority

@Entity(tableName = "care_tasks")
data class CareTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val note: String,
    val dueDateMillis: Long,
    val priority: String,
    val category: String,
    val isCompleted: Boolean,
    val createdAtMillis: Long
) {
    fun toDomainModel(): CareTask {
        return CareTask(
            id = id,
            title = title,
            note = note,
            dueDateMillis = dueDateMillis,
            priority = try { TaskPriority.valueOf(priority) } catch (e: Exception) { TaskPriority.MEDIUM },
            category = try { TaskCategory.valueOf(category) } catch (e: Exception) { TaskCategory.INSPECTION },
            isCompleted = isCompleted,
            createdAtMillis = createdAtMillis
        )
    }

    companion object {
        fun fromDomainModel(task: CareTask): CareTaskEntity {
            return CareTaskEntity(
                id = task.id,
                title = task.title,
                note = task.note,
                dueDateMillis = task.dueDateMillis,
                priority = task.priority.name,
                category = task.category.name,
                isCompleted = task.isCompleted,
                createdAtMillis = task.createdAtMillis
            )
        }
    }
}
