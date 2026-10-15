package com.example.teacare

import android.app.Application
import com.example.teacare.data.local.TeaCareDatabase
import com.example.teacare.data.local.preferences.UserPreferences
import com.example.teacare.data.repository.CareTaskRepository
import com.example.teacare.data.repository.DiseaseRepository
import com.example.teacare.data.repository.ScanHistoryRepository
import com.example.teacare.data.repository.FastApiTeaDetectionEngine
import com.example.teacare.data.repository.TeaDetectionEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TeaCareApplication : Application() {

    lateinit var database: TeaCareDatabase private set
    lateinit var diseaseRepository: DiseaseRepository private set
    lateinit var careTaskRepository: CareTaskRepository private set
    lateinit var scanHistoryRepository: ScanHistoryRepository private set
    lateinit var detectionEngine: TeaDetectionEngine private set
    lateinit var userPreferences: UserPreferences private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = TeaCareDatabase.getDatabase(this)
        diseaseRepository = DiseaseRepository()
        careTaskRepository = CareTaskRepository(database.careTaskDao())
        scanHistoryRepository = ScanHistoryRepository(database.scanHistoryDao())
        userPreferences = UserPreferences(this)
        detectionEngine = com.example.teacare.data.repository.FastApiTeaDetectionEngine(this, diseaseRepository, userPreferences)

        // Seed initial data if DB is empty
        val applicationScope = CoroutineScope(Dispatchers.IO)
        applicationScope.launch {
            val taskCount = careTaskRepository.totalTaskCount.first()
            if (taskCount == 0) {
                careTaskRepository.seedInitialTasksIfEmpty()
            }
            val scanCount = scanHistoryRepository.totalScansCount.first()
            if (scanCount == 0) {
                scanHistoryRepository.seedInitialScansIfEmpty()
            }
        }
    }

    companion object {
        lateinit var instance: TeaCareApplication
            private set
    }
}
