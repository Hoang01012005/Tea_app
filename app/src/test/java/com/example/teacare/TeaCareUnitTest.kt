package com.example.teacare

import com.example.teacare.data.local.entity.CareTaskEntity
import com.example.teacare.data.model.CareTask
import com.example.teacare.data.model.DiseaseCategory
import com.example.teacare.data.model.DiseaseSeverity
import com.example.teacare.data.model.TaskCategory
import com.example.teacare.data.model.TaskPriority
import com.example.teacare.data.model.TeaLeafBDClassMapper
import com.example.teacare.data.repository.DiseaseRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TeaCareUnitTest {

    private val diseaseRepository = DiseaseRepository()

    @Test
    fun testTeaLeafBD7ClassesVietnameseMapping() {
        // Kiểm tra chính xác 7 lớp của TeaLeafBD theo Requirement 4
        assertEquals("Đốm lá do tảo", TeaLeafBDClassMapper.toVietnameseName("Tea algal leaf spot"))
        assertEquals("Bệnh cháy nâu", TeaLeafBDClassMapper.toVietnameseName("Brown Blight"))
        assertEquals("Bệnh cháy xám", TeaLeafBDClassMapper.toVietnameseName("Gray Blight"))
        assertEquals("Bọ xít muỗi hại chè", TeaLeafBDClassMapper.toVietnameseName("Helopeltis"))
        assertEquals("Nhện đỏ", TeaLeafBDClassMapper.toVietnameseName("Red spider"))
        assertEquals("Bọ xít xanh", TeaLeafBDClassMapper.toVietnameseName("Green mirid bug"))
        assertEquals("Lá chè khỏe mạnh", TeaLeafBDClassMapper.toVietnameseName("Healthy leaf"))
    }

    @Test
    fun testHealthyLeafClassification() {
        assertTrue(TeaLeafBDClassMapper.isHealthy("Healthy leaf"))
        assertTrue(TeaLeafBDClassMapper.isHealthy("healthy leaf"))
        assertFalse(TeaLeafBDClassMapper.isHealthy("Brown Blight"))
        assertFalse(TeaLeafBDClassMapper.isHealthy("Helopeltis"))
    }

    @Test
    fun testDiseaseSearchByVietnameseName() {
        val results = diseaseRepository.searchDiseases(query = "Cháy nâu")
        assertTrue(results.isNotEmpty())
        assertEquals("brown_blight", results[0].id)
    }

    @Test
    fun testDiseaseFilterByCategory() {
        val pestDiseases = diseaseRepository.searchDiseases(
            query = "",
            category = DiseaseCategory.PEST
        )
        assertTrue(pestDiseases.isNotEmpty())
        pestDiseases.forEach {
            assertEquals(DiseaseCategory.PEST, it.category)
        }
    }

    @Test
    fun testCareTaskEntityToDomainModelConversion() {
        val entity = CareTaskEntity(
            id = 42L,
            title = "Phun phòng ngừa cháy nâu",
            note = "Sử dụng Difenoconazole",
            dueDateMillis = 1700000000L,
            priority = TaskPriority.HIGH.name,
            category = TaskCategory.TREATMENT.name,
            isCompleted = false,
            createdAtMillis = 1699999000L
        )

        val model = entity.toDomainModel()
        assertEquals(42L, model.id)
        assertEquals("Phun phòng ngừa cháy nâu", model.title)
        assertEquals(TaskPriority.HIGH, model.priority)
        assertEquals(TaskCategory.TREATMENT, model.category)
        assertEquals(false, model.isCompleted)

        val backToEntity = CareTaskEntity.fromDomainModel(model)
        assertEquals(entity, backToEntity)
    }

    @Test
    fun testApiPredictResponseDto() {
        val json = """
            {
                "class_id": 6,
                "class_name": "Healthy leaf",
                "disease_vi": "Lá chè khỏe mạnh",
                "confidence": 0.954,
                "is_healthy": true,
                "top3_predictions": [
                    {
                        "class_id": 6,
                        "class_name": "Healthy leaf",
                        "disease_vi": "Lá chè khỏe mạnh",
                        "confidence": 0.954
                    }
                ],
                "all_predictions": []
            }
        """.trimIndent()
        val gson = com.google.gson.Gson()
        val response = gson.fromJson(json, com.example.teacare.data.remote.dto.ApiPredictResponse::class.java)
        assertEquals(6, response.classId)
        assertEquals("Healthy leaf", response.className)
        assertEquals("Lá chè khỏe mạnh", response.diseaseVi)
        assertEquals(0.954f, response.confidence, 0.001f)
        assertTrue(response.isHealthy)
        assertEquals(1, response.top3Predictions.size)
        assertEquals("Lá chè khỏe mạnh", response.top3Predictions[0].diseaseVi)
    }
}
