package com.example.teacare.data.model

enum class DiseaseSeverity(val label: String) {
    LOW("Nhẹ"),
    MEDIUM("Trung bình"),
    HIGH("Nguy hiểm"),
    HEALTHY("Khỏe mạnh")
}

enum class DiseaseCategory(val label: String) {
    FUNGAL("Bệnh do nấm"),
    PEST("Sâu - rầy hại"),
    BACTERIAL("Bệnh vi khuẩn"),
    PHYSIOLOGICAL("Rối loạn sinh lý"),
    HEALTHY("Cây đạt chuẩn")
}

data class Disease(
    val id: String,
    val vietnameseName: String,
    val commonName: String,
    val scientificName: String,
    val severity: DiseaseSeverity,
    val category: DiseaseCategory,
    val shortDescription: String,
    val symptoms: List<String>,
    val causes: List<String>,
    val biologicalControl: List<String>,
    val chemicalControl: List<String>,
    val prevention: List<String>,
    val optimalClimate: String,
    val isVerifiedExpert: Boolean = true
)
