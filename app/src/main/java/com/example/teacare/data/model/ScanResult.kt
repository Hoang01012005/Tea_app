package com.example.teacare.data.model

data class ScanResult(
    val id: Long = 0,
    val imageUri: String,
    val diseaseId: String,
    val diseaseName: String,
    val confidence: Float,
    val severity: DiseaseSeverity,
    val summary: String,
    val symptoms: String,
    val treatment: String,
    val scannedAtMillis: Long = System.currentTimeMillis(),
    val isDemoMode: Boolean = true
)
