package com.example.teacare.data.repository

import com.example.teacare.data.local.dao.ScanHistoryDao
import com.example.teacare.data.local.entity.ScanHistoryEntity
import com.example.teacare.data.model.DiseaseSeverity
import com.example.teacare.data.model.ScanResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScanHistoryRepository(private val scanHistoryDao: ScanHistoryDao) {

    val allScans: Flow<List<ScanResult>> = scanHistoryDao.getAllScans().map { list ->
        list.map { it.toDomainModel() }
    }

    val totalScansCount: Flow<Int> = scanHistoryDao.getTotalScansCount()

    suspend fun getScanById(id: Long): ScanResult? {
        return scanHistoryDao.getScanById(id)?.toDomainModel()
    }

    suspend fun saveScan(scan: ScanResult): Long {
        return scanHistoryDao.insertScan(ScanHistoryEntity.fromDomainModel(scan))
    }

    suspend fun deleteScan(scan: ScanResult) {
        scanHistoryDao.deleteScan(ScanHistoryEntity.fromDomainModel(scan))
    }

    suspend fun deleteScanById(id: Long) {
        scanHistoryDao.deleteScanById(id)
    }

    suspend fun seedInitialScansIfEmpty() {
        val sampleScans = listOf(
            ScanResult(
                imageUri = "",
                diseaseId = "blister_blight",
                diseaseName = "Bệnh Phồng Lá Chè",
                confidence = 0.942f,
                severity = DiseaseSeverity.HIGH,
                summary = "Phát hiện vết phồng mụn nước trắng tròn ở mặt dưới lá non kèm đốm vàng lõm mặt trên.",
                symptoms = "Lá non cong queo, có ổ nấm màu trắng sữa phồng lên ở mặt dưới phiến lá.",
                treatment = "Cách ly khu vực nhiễm bệnh, ngưng bón đạm, phun gốc đồng Champion 77WP hoặc Anvil 5SC.",
                scannedAtMillis = System.currentTimeMillis() - 86400000L * 2,
                isDemoMode = true
            ),
            ScanResult(
                imageUri = "",
                diseaseId = "healthy_leaf",
                diseaseName = "Cây Chè Khỏe Mạnh",
                confidence = 0.985f,
                severity = DiseaseSeverity.HEALTHY,
                summary = "Búp tôm và lá non xanh nõn, biểu bì bóng mịn, không phát hiện dấu hiệu nấm hoặc côn trùng gây hại.",
                symptoms = "Cây phát triển bình thường, tán lá cân đối, không có triệu chứng bệnh.",
                treatment = "Duy trì chế độ tưới nước giữ ẩm và bón phân hữu cơ vi sinh định kỳ.",
                scannedAtMillis = System.currentTimeMillis() - 86400000L * 5,
                isDemoMode = true
            )
        )
        for (scan in sampleScans) {
            scanHistoryDao.insertScan(ScanHistoryEntity.fromDomainModel(scan))
        }
    }
}
