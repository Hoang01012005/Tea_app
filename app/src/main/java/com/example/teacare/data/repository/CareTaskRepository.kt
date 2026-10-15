package com.example.teacare.data.repository

import com.example.teacare.data.local.dao.CareTaskDao
import com.example.teacare.data.local.entity.CareTaskEntity
import com.example.teacare.data.model.CareTask
import com.example.teacare.data.model.TaskCategory
import com.example.teacare.data.model.TaskPriority
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CareTaskRepository(private val careTaskDao: CareTaskDao) {

    val allTasks: Flow<List<CareTask>> = careTaskDao.getAllTasks().map { list ->
        list.map { it.toDomainModel() }
    }

    val pendingTasks: Flow<List<CareTask>> = careTaskDao.getPendingTasks().map { list ->
        list.map { it.toDomainModel() }
    }

    val pendingTaskCount: Flow<Int> = careTaskDao.getPendingTaskCount()
    val totalTaskCount: Flow<Int> = careTaskDao.getTotalTaskCount()

    suspend fun getTaskById(id: Long): CareTask? {
        return careTaskDao.getTaskById(id)?.toDomainModel()
    }

    suspend fun addTask(task: CareTask): Long {
        return careTaskDao.insertTask(CareTaskEntity.fromDomainModel(task))
    }

    suspend fun updateTask(task: CareTask) {
        careTaskDao.updateTask(CareTaskEntity.fromDomainModel(task))
    }

    suspend fun deleteTask(task: CareTask) {
        careTaskDao.deleteTask(CareTaskEntity.fromDomainModel(task))
    }

    suspend fun toggleTaskStatus(id: Long, isCompleted: Boolean) {
        careTaskDao.updateTaskStatus(id, isCompleted)
    }

    suspend fun seedInitialTasksIfEmpty() {
        val count = 0
        // We will seed default care tasks if the DB is empty
        val sampleTasks = listOf(
            CareTask(
                title = "Phun chế phẩm sinh học phòng ngừa Phồng Lá",
                note = "Phun nấm đối kháng Trichoderma viride vào lúc chiều mát sau đợt sương muối kéo dài.",
                dueDateMillis = System.currentTimeMillis() + 86400000L * 1, // ngày mai
                priority = TaskPriority.HIGH,
                category = TaskCategory.TREATMENT,
                isCompleted = false
            ),
            CareTask(
                title = "Kiểm tra mật độ rầy xanh & bọ xít muỗi",
                note = "Quan sát 5 điểm trên luống chè, kiểm tra mặt dưới lá bánh tẻ và búp tôm non.",
                dueDateMillis = System.currentTimeMillis() + 86400000L * 2,
                priority = TaskPriority.MEDIUM,
                category = TaskCategory.INSPECTION,
                isCompleted = false
            ),
            CareTask(
                title = "Bón bổ sung Kali & phân hữu cơ vi sinh luống số 3",
                note = "Đào rãnh mép tán sâu 15cm, bón 2kg phân hữu cơ hoai mục mỗi gốc kết hợp tưới ẩm.",
                dueDateMillis = System.currentTimeMillis() + 86400000L * 3,
                priority = TaskPriority.MEDIUM,
                category = TaskCategory.FERTILIZING,
                isCompleted = false
            ),
            CareTask(
                title = "Đốn tỉa cành tăm lòng tán & dọn lá già rụng",
                note = "Cắt tỉa cành khuất ánh sáng để tăng lưu thông không khí trong lòng tán cây.",
                dueDateMillis = System.currentTimeMillis() - 86400000L * 1,
                priority = TaskPriority.LOW,
                category = TaskCategory.PRUNING,
                isCompleted = true
            )
        )
        for (task in sampleTasks) {
            careTaskDao.insertTask(CareTaskEntity.fromDomainModel(task))
        }
    }
}
