package com.example.teacare.ui.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teacare.TeaCareApplication
import com.example.teacare.data.model.CareTask
import com.example.teacare.data.model.Disease
import com.example.teacare.data.model.TaskCategory
import com.example.teacare.data.model.TaskPriority
import com.example.teacare.ui.components.SeverityBadge
import com.example.teacare.ui.components.TeaCareCard
import com.example.teacare.ui.components.TeaCareTopBar
import com.example.teacare.ui.theme.TeaBorder
import com.example.teacare.ui.theme.TeaGreenDark
import com.example.teacare.ui.theme.TeaGreenLight
import com.example.teacare.ui.theme.TeaGreenPrimary
import com.example.teacare.ui.theme.TeaTextSecondary
import kotlinx.coroutines.launch

@Composable
fun DiseaseDetailScreen(
    diseaseId: String,
    onBackClick: () -> Unit
) {
    val disease = TeaCareApplication.instance.diseaseRepository.getDiseaseById(diseaseId)
    val coroutineScope = rememberCoroutineScope()
    var isTaskCreated by remember { mutableStateOf(false) }

    if (disease == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Không tìm thấy dữ liệu bệnh.")
            Button(onClick = onBackClick) { Text("Quay lại") }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TeaCareTopBar(
            title = disease.vietnameseName,
            onBackClick = onBackClick
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Hero Card
            item {
                TeaCareCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 24
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SeverityBadge(severity = disease.severity)
                            if (disease.isVerifiedExpert) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFDCFCE7))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Verified,
                                        contentDescription = null,
                                        tint = TeaGreenPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Kiểm chứng chuyên gia",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                        color = TeaGreenPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = disease.vietnameseName,
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "${disease.commonName} • ${disease.scientificName}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                fontWeight = FontWeight.Medium
                            ),
                            color = TeaGreenPrimary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = disease.shortDescription,
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                            color = TeaTextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(TeaGreenLight.copy(alpha = 0.5f))
                                .padding(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Thermostat,
                                contentDescription = null,
                                tint = TeaGreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Điều kiện phát sinh: ${disease.optimalClimate}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TeaGreenDark
                            )
                        }
                    }
                }
            }

            // 2. Triệu chứng nhận biết
            item {
                DetailSectionCard(
                    title = "Triệu chứng & Biểu hiện điển hình",
                    icon = Icons.Filled.LocalFlorist,
                    items = disease.symptoms
                )
            }

            // 3. Nguyên nhân gây bệnh
            item {
                DetailSectionCard(
                    title = "Nguyên nhân & Tác nhân gây hại",
                    icon = Icons.Filled.BugReport,
                    items = disease.causes
                )
            }

            // 4. Biện pháp sinh học
            item {
                DetailSectionCard(
                    title = "Biện pháp sinh học (An toàn VietGAP)",
                    icon = Icons.Filled.CheckCircle,
                    items = disease.biologicalControl
                )
            }

            // 5. Biện pháp hóa học
            item {
                DetailSectionCard(
                    title = "Biện pháp hóa học & Thuốc BVTV",
                    icon = Icons.Filled.MedicalServices,
                    items = disease.chemicalControl
                )
            }

            // 6. Phòng ngừa lâu dài
            item {
                DetailSectionCard(
                    title = "Kỹ thuật canh tác phòng ngừa",
                    icon = Icons.Filled.Shield,
                    items = disease.prevention
                )
            }

            // 7. Action Button: Create Care Task
            item {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val newTask = CareTask(
                                title = "Phòng trị ${disease.vietnameseName}",
                                note = disease.chemicalControl.firstOrNull()
                                    ?: disease.biologicalControl.firstOrNull()
                                    ?: "Theo dõi sát sao nương chè.",
                                dueDateMillis = System.currentTimeMillis() + 86400000L * 1,
                                priority = if (disease.severity == com.example.teacare.data.model.DiseaseSeverity.HIGH) TaskPriority.HIGH else TaskPriority.MEDIUM,
                                category = TaskCategory.TREATMENT,
                                isCompleted = false
                            )
                            TeaCareApplication.instance.careTaskRepository.addTask(newTask)
                            isTaskCreated = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isTaskCreated) TeaGreenLight else TeaGreenPrimary,
                        contentColor = if (isTaskCreated) TeaGreenDark else Color.White
                    )
                ) {
                    Icon(
                        imageVector = if (isTaskCreated) Icons.Outlined.CheckCircle else Icons.Filled.AddAlert,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isTaskCreated) "Đã thêm vào Lịch chăm sóc" else "Tạo lịch chăm sóc phòng trị cho bệnh này",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailSectionCard(
    title: String,
    icon: ImageVector,
    items: List<String>
) {
    TeaCareCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(TeaGreenLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = TeaGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            items.forEach { textItem ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "•",
                        color = TeaGreenPrimary,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = textItem,
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}
