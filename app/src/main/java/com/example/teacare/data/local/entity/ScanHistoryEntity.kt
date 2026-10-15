package com.example.teacare.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.teacare.data.model.DiseaseSeverity
import com.example.teacare.data.model.ScanResult

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val imageUri: String,
    val diseaseId: String,
    val diseaseName: String,
    val confidence: Float,
    val severity: String,
    val summary: String,
    val symptoms: String,
    val treatment: String,
    val scannedAtMillis: Long,
    val isDemoMode: Boolean
) {
    fun toDomainModel(): ScanResult {
        return ScanResult(
            id = id,
            imageUri = imageUri,
            diseaseId = diseaseId,
            diseaseName = diseaseName,
            confidence = confidence,
            severity = try { DiseaseSeverity.valueOf(severity) } catch (e: Exception) { DiseaseSeverity.MEDIUM },
            summary = summary,
            symptoms = symptoms,
            treatment = treatment,
            scannedAtMillis = scannedAtMillis,
            isDemoMode = isDemoMode
        )
    }

    companion object {
        fun fromDomainModel(model: ScanResult): ScanHistoryEntity {
            return ScanHistoryEntity(
                id = model.id,
                imageUri = model.imageUri,
                diseaseId = model.diseaseId,
                diseaseName = model.diseaseName,
                confidence = model.confidence,
                severity = model.severity.name,
                summary = model.summary,
                symptoms = model.symptoms,
                treatment = model.treatment,
                scannedAtMillis = model.scannedAtMillis,
                isDemoMode = model.isDemoMode
            )
        }
    }
}
