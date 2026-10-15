package com.example.teacare.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teacare.data.model.ScanResult
import com.example.teacare.ui.components.DemoModeBadge
import com.example.teacare.ui.components.EmptyStateView
import com.example.teacare.ui.components.SeverityBadge
import com.example.teacare.ui.components.TeaCareCard
import com.example.teacare.ui.components.TeaCareTopBar
import com.example.teacare.ui.theme.TeaBorder
import com.example.teacare.ui.theme.TeaError
import com.example.teacare.ui.theme.TeaGreenLight
import com.example.teacare.ui.theme.TeaGreenPrimary
import com.example.teacare.ui.theme.TeaTextSecondary
import com.example.teacare.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ScanHistoryScreen(
    viewModel: HistoryViewModel,
    onBackClick: () -> Unit,
    onNavigateToDetection: () -> Unit,
    onNavigateToDiseaseDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TeaCareTopBar(
            title = "Lịch sử chẩn đoán",
            onBackClick = onBackClick
        )

        if (uiState.scans.isEmpty()) {
            EmptyStateView(
                title = "Chưa có lượt chẩn đoán nào",
                description = "Chụp hoặc chọn ảnh lá chè từ thư viện để bắt đầu nhận diện bệnh.",
                icon = Icons.Filled.History,
                actionButtonText = "Chẩn đoán ngay",
                onActionClick = onNavigateToDetection
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.scans, key = { it.id }) { scan ->
                    HistoryItemCard(
                        scan = scan,
                        onClick = { viewModel.selectScan(scan) },
                        onDelete = { viewModel.deleteScan(scan) }
                    )
                }
            }
        }

        // Dialog xem lại chi tiết chẩn đoán
        uiState.selectedScan?.let { selected ->
            ScanDetailDialog(
                scan = selected,
                onDismiss = { viewModel.selectScan(null) },
                onViewInLibrary = {
                    viewModel.selectScan(null)
                    onNavigateToDiseaseDetail(selected.diseaseId)
                }
            )
        }
    }
}

@Composable
private fun HistoryItemCard(
    scan: ScanResult,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault()).format(Date(scan.scannedAtMillis))

    TeaCareCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(TeaGreenLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Eco,
                    contentDescription = null,
                    tint = TeaGreenPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = scan.diseaseName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    SeverityBadge(severity = scan.severity)
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Độ tin cậy: ${(scan.confidence * 100).toInt()}% • $dateStr",
                    style = MaterialTheme.typography.bodySmall,
                    color = TeaTextSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = scan.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = TeaTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Filled.DeleteOutline,
                    contentDescription = "Xóa",
                    tint = TeaError.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ScanDetailDialog(
    scan: ScanResult,
    onDismiss: () -> Unit,
    onViewInLibrary: () -> Unit
) {
    val dateStr = SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault()).format(Date(scan.scannedAtMillis))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = scan.diseaseName,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                SeverityBadge(severity = scan.severity)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Thời gian thực hiện:", style = MaterialTheme.typography.labelSmall, color = TeaTextSecondary)
                    Text(text = dateStr, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Độ tin cậy:", style = MaterialTheme.typography.labelSmall, color = TeaTextSecondary)
                    Text(
                        text = "${(scan.confidence * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TeaGreenPrimary
                    )
                }

                DemoModeBadge()

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Mô tả nhận định:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(text = scan.summary, style = MaterialTheme.typography.bodySmall, color = TeaTextSecondary)

                Text(
                    text = "Hướng xử lý:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(text = scan.treatment, style = MaterialTheme.typography.bodySmall, color = TeaGreenPrimary)
            }
        },
        confirmButton = {
            Button(
                onClick = onViewInLibrary,
                colors = ButtonDefaults.buttonColors(containerColor = TeaGreenPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Xem trong Thư viện")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TeaBorder)
            ) {
                Text("Đóng", color = TeaTextSecondary)
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}
