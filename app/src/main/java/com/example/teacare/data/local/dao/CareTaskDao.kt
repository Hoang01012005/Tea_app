package com.example.teacare.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.teacare.data.local.entity.CareTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CareTaskDao {
    @Query("SELECT * FROM care_tasks ORDER BY isCompleted ASC, dueDateMillis ASC")
    fun getAllTasks(): Flow<List<CareTaskEntity>>

    @Query("SELECT * FROM care_tasks WHERE isCompleted = 0 ORDER BY dueDateMillis ASC")
    fun getPendingTasks(): Flow<List<CareTaskEntity>>

    @Query("SELECT * FROM care_tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): CareTaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: CareTaskEntity): Long

    @Update
    suspend fun updateTask(task: CareTaskEntity)

    @Delete
    suspend fun deleteTask(task: CareTaskEntity)

    @Query("UPDATE care_tasks SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateTaskStatus(id: Long, isCompleted: Boolean)

    @Query("SELECT COUNT(*) FROM care_tasks WHERE isCompleted = 0")
    fun getPendingTaskCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM care_tasks")
    fun getTotalTaskCount(): Flow<Int>
}
