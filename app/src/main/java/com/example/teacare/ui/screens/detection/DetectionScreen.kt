package com.example.teacare.ui.screens.detection

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.teacare.data.model.Disease
import com.example.teacare.ui.components.ScanningLaserEffect
import com.example.teacare.ui.components.SeverityBadge
import com.example.teacare.ui.components.TeaCareCard
import com.example.teacare.ui.components.TeaCarePrimaryButton
import com.example.teacare.ui.components.TeaCareTopBar
import com.example.teacare.ui.components.TeaLeafLogo
import com.example.teacare.ui.theme.TeaBorder
import com.example.teacare.ui.theme.TeaError
import com.example.teacare.ui.theme.TeaGreenDark
import com.example.teacare.ui.theme.TeaGreenLight
import com.example.teacare.ui.theme.TeaGreenPrimary
import com.example.teacare.ui.theme.TeaSuccess
import com.example.teacare.ui.theme.TeaSuccessBg
import com.example.teacare.ui.theme.TeaTextMuted
import com.example.teacare.ui.theme.TeaTextSecondary
import com.example.teacare.ui.theme.TeaWarning
import com.example.teacare.ui.theme.TeaWarningBg
import com.example.teacare.viewmodel.DetectionState
import com.example.teacare.viewmodel.DetectionViewModel
import java.io.File
import java.util.Locale

@Composable
fun DetectionScreen(
    viewModel: DetectionViewModel,
    onNavigateToDiseaseDetail: (String) -> Unit
) {
    val context = LocalContext.current
    val detectionState by viewModel.detectionState.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val apiBaseUrl by viewModel.apiBaseUrl.collectAsState()
    val isBackendOnline by viewModel.isBackendOnline.collectAsState()

    var showConfigUrlDialog by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Hàm tạo tệp tạm thời cho ảnh chụp từ camera
    fun createTempPictureUri(): Uri {
        val tempFile = File.createTempFile("teacare_capture_", ".jpg", context.cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && tempCameraUri != null) {
            viewModel.onImageSelected(tempCameraUri.toString())
        }
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.onImageSelected(uri.toString())
        }
    }

    // 5 mẫu kiểm thử nhanh khi chạy trên môi trường máy ảo
    val demoSampleLeaves = listOf(
        Pair("Đốm lá tảo", "sample_tea_algal_leaf_spot"),
        Pair("Cháy nâu", "sample_brown_blight"),
        Pair("Cháy xám", "sample_gray_blight"),
        Pair("Bọ xít muỗi", "sample_helopeltis"),
        Pair("Nhện đỏ", "sample_red_spider"),
        Pair("Bọ xít xanh", "sample_green_mirid_bug"),
        Pair("Lá khỏe mạnh", "sample_healthy_leaf")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TeaCareTopBar(title = "Nhận diện bệnh chè (DenseNet-121)")

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Backend Server Status & Base URL Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isBackendOnline == true) TeaSuccessBg else if (isBackendOnline == false) Color(0xFFFEE2E2) else Color(0xFFFEF3C7))
                        .border(1.dp, if (isBackendOnline == true) TeaSuccess.copy(alpha = 0.3f) else if (isBackendOnline == false) TeaError.copy(alpha = 0.3f) else TeaBorder, RoundedCornerShape(12.dp))
                        .clickable { showConfigUrlDialog = true }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = if (isBackendOnline == true) Icons.Filled.CloudDone else Icons.Filled.CloudOff,
                            contentDescription = null,
                            tint = if (isBackendOnline == true) TeaSuccess else if (isBackendOnline == false) TeaError else TeaWarning,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isBackendOnline == true) "FastAPI Online • DenseNet121" else if (isBackendOnline == false) "FastAPI Mất kết nối" else "Đang kiểm tra kết nối Backend...",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isBackendOnline == true) TeaGreenDark else if (isBackendOnline == false) TeaError else Color(0xFF92400E)
                            )
                            Text(
                                text = apiBaseUrl,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = TeaTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Đổi IP",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = TeaGreenPrimary
                        )
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Chỉnh sửa URL",
                            tint = TeaGreenPrimary,
                            modifier = Modifier.size(16.dp).padding(start = 2.dp)
                        )
                    }
                }
            }

            // 2. Image Selection & Preview Card
            item {
                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(
                            1.dp,
                            if (selectedImageUri != null) TeaGreenPrimary else TeaBorder,
                            RoundedCornerShape(24.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        if (selectedImageUri!!.startsWith("content://") || selectedImageUri!!.startsWith("file://")) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(selectedImageUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Ảnh lá chè đã chọn",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(TeaGreenLight.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    TeaLeafLogo(size = 80.dp, tint = TeaGreenPrimary)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Mẫu thử nghiệm đã nạp",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TeaGreenDark
                                    )
                                    Text(
                                        text = selectedImageUri!!.replace("sample_", "Lớp: ").replace("_", " "),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TeaTextSecondary
                                    )
                                }
                            }
                        }

                        // Hiệu ứng tia quét laser
                        val isScanning = detectionState is DetectionState.Analyzing
                        ScanningLaserEffect(isScanning = isScanning)
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(TeaGreenLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CameraAlt,
                                    contentDescription = null,
                                    tint = TeaGreenPrimary,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Chụp hoặc chọn ảnh lá chè",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Chọn nút bên dưới để chụp từ camera hoặc chọn ảnh từ bộ sưu tập",
                                style = MaterialTheme.typography.bodySmall,
                                color = TeaTextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // 3. Action Buttons: Chụp Camera | Chọn Thư viện
            item {
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Nút Chụp Camera
                    OutlinedButton(
                        onClick = {
                            try {
                                val uri = createTempPictureUri()
                                tempCameraUri = uri
                                cameraLauncher.launch(uri)
                            } catch (e: Exception) {
                                // Fallback nếu thiết bị không có camera app
                            }
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TeaBorder)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = null,
                            tint = TeaGreenPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Chụp ảnh",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    // Nút Chọn Thư viện
                    OutlinedButton(
                        onClick = {
                            try {
                                galleryLauncher.launch("image/*")
                            } catch (e: Exception) {
                                // Fallback
                            }
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TeaBorder)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PhotoLibrary,
                            contentDescription = null,
                            tint = TeaGreenPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Thư viện",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            // 4. Quick Sample Selector for Emulators
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Mẫu thử nghiệm nhanh 7 lớp TeaLeafBD:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = TeaTextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        items(demoSampleLeaves) { sample ->
                            val isSelected = selectedImageUri == sample.second
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) TeaGreenPrimary else MaterialTheme.colorScheme.surface)
                                    .border(1.dp, if (isSelected) TeaGreenPrimary else TeaBorder, RoundedCornerShape(10.dp))
                                    .clickable { viewModel.onImageSelected(sample.second) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = sample.first,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // 5. Nút Bắt đầu nhận diện bệnh (Primary Action)
            item {
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.startAnalysis() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = selectedImageUri != null && detectionState !is DetectionState.Analyzing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TeaGreenPrimary,
                        contentColor = Color.White
                    )
                ) {
                    if (detectionState is DetectionState.Analyzing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Đang nhận diện qua FastAPI...",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Nhận diện bệnh ngay",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        )
                    }
                }
            }

            // 6. Analyzing Indicator Card
            item {
                if (detectionState is DetectionState.Analyzing) {
                    val analyzing = detectionState as DetectionState.Analyzing
                    Spacer(modifier = Modifier.height(16.dp))
                    TeaCareCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = TeaGreenLight.copy(alpha = 0.5f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                        ) {
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = TeaGreenPrimary,
                                trackColor = Color.White
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = analyzing.stepMessage,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = TeaGreenDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Mô hình DenseNet-121 đang suy luận trên server...",
                                style = MaterialTheme.typography.bodySmall,
                                color = TeaTextSecondary
                            )
                        }
                    }
                }
            }

            // 7. Error & Retry State
            item {
                if (detectionState is DetectionState.Error) {
                    val error = detectionState as DetectionState.Error
                    Spacer(modifier = Modifier.height(16.dp))
                    TeaCareCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color(0xFFFEE2E2),
                        borderColor = Color(0xFFFCA5A5)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.Outlined.Warning,
                                    contentDescription = null,
                                    tint = TeaError,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (error.isNetworkError) "Lỗi kết nối Backend" else "Lỗi nhận diện",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF991B1B)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = error.errorMessage,
                                        color = Color(0xFF991B1B),
                                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (error.isNetworkError) {
                                    OutlinedButton(
                                        onClick = { showConfigUrlDialog = true },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.height(38.dp)
                                    ) {
                                        Text("Đổi IP máy chủ", style = MaterialTheme.typography.labelSmall)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                }

                                Button(
                                    onClick = { viewModel.retryAnalysis() },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = TeaError),
                                    modifier = Modifier.height(38.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Thử lại", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                    }
                }
            }

            // 8. Result Section (Tên lớp tiếng Anh + tiếng Việt, % xác suất, trạng thái khỏe mạnh/bệnh, nút nhận diện ảnh khác)
            item {
                if (detectionState is DetectionState.Success) {
                    val success = detectionState as DetectionState.Success
                    val result = success.scanResult
                    val disease = success.matchedDisease
                    val confidencePercent = String.format(Locale.US, "%.1f%%", result.confidence * 100)

                    Spacer(modifier = Modifier.height(20.dp))

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "KẾT QUẢ MÔ HÌNH DENSENET-121",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 1.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TeaGreenPrimary
                            )
                            Text(
                                text = "${success.inferenceTimeMs}ms",
                                style = MaterialTheme.typography.labelSmall,
                                color = TeaTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Thẻ kết quả chính
                        TeaCareCard(
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = 22
                        ) {
                            Column {
                                // Trạng thái: Khỏe mạnh hoặc Có dấu hiệu bệnh
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (success.isHealthy) TeaSuccessBg else TeaWarningBg)
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (success.isHealthy) Icons.Filled.CheckCircle else Icons.Outlined.Warning,
                                            contentDescription = null,
                                            tint = if (success.isHealthy) TeaSuccess else TeaWarning,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (success.isHealthy) "TRẠNG THÁI: KHỎE MẠNH" else "TRẠNG THÁI: CÓ DẤU HIỆU BỆNH HẠI",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (success.isHealthy) TeaSuccess else Color(0xFFB45309)
                                        )
                                    }
                                    SeverityBadge(severity = result.severity)
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Tên bệnh tiếng Việt (Lớn)
                                Text(
                                    text = success.vietnameseClassName,
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, fontSize = 22.sp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                // Tên lớp tiếng Anh gốc theo TeaLeafBD
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Nhãn gốc mô hình: ",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TeaTextSecondary
                                    )
                                    Text(
                                        text = success.englishClassName,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = TeaGreenPrimary
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Đồng hồ hiển thị phần trăm độ tin cậy
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(TeaGreenLight.copy(alpha = 0.6f))
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "Xác suất mô hình trả về:",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TeaGreenDark
                                        )
                                        Text(
                                            text = confidencePercent,
                                            style = MaterialTheme.typography.displayMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 28.sp
                                            ),
                                            color = TeaGreenPrimary
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "Tập dữ liệu:",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TeaGreenDark
                                        )
                                        Text(
                                            text = "TeaLeafBD (7 lớp)",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = TeaGreenDark
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Mô tả tóm tắt
                                Text(
                                    text = "Đặc điểm nhận diện:",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = result.summary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TeaTextSecondary
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Hướng xử lý
                                Text(
                                    text = "Hướng xử lý khuyên dùng:",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = result.treatment,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TeaGreenDark
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Danh sách xác suất của toàn bộ 7 lớp
                                Text(
                                    text = "Phân phối xác suất 7 lớp TeaLeafBD:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TeaTextSecondary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                success.topPredictions.forEach { pred ->
                                    val pct = String.format(Locale.US, "%.1f%%", pred.probability * 100)
                                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "${pred.vietnameseName} (${pred.englishName})",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontWeight = if (pred.englishName == success.englishClassName) FontWeight.Bold else FontWeight.Normal
                                                ),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = pct,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontWeight = if (pred.englishName == success.englishClassName) FontWeight.Bold else FontWeight.Normal
                                                ),
                                                color = if (pred.englishName == success.englishClassName) TeaGreenPrimary else TeaTextSecondary
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        LinearProgressIndicator(
                                            progress = { pred.probability },
                                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                            color = if (pred.englishName == success.englishClassName) TeaGreenPrimary else TeaBorder,
                                            trackColor = TeaBorder.copy(alpha = 0.3f)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Nút Nhận diện ảnh khác (Requirement 5)
                        OutlinedButton(
                            onClick = { viewModel.resetDetection() },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TeaBorder)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = null,
                                tint = TeaGreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Nhận diện ảnh khác",
                                color = TeaGreenPrimary,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Nút Thêm vào lịch chăm sóc
                        Button(
                            onClick = { viewModel.createCareTaskFromDisease(disease) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (success.isTaskCreated) TeaGreenLight else TeaGreenPrimary,
                                contentColor = if (success.isTaskCreated) TeaGreenDark else Color.White
                            )
                        ) {
                            Icon(
                                imageVector = if (success.isTaskCreated) Icons.Outlined.CheckCircle else Icons.Filled.AddAlert,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (success.isTaskCreated) "Đã thêm vào lịch chăm sóc" else "Tạo lịch chăm sóc phòng trị",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Xem chi tiết trong thư viện
                        OutlinedButton(
                            onClick = { onNavigateToDiseaseDetail(disease.id) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TeaBorder)
                        ) {
                            Text(
                                text = "Xem biện pháp phòng ngừa chuyên sâu",
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                    }
                }
            }
        }

        // Hộp thoại cấu hình Base URL cho máy ảo hoặc điện thoại thật
        if (showConfigUrlDialog) {
            var inputUrl by remember { mutableStateOf(apiBaseUrl) }

            AlertDialog(
                onDismissRequest = { showConfigUrlDialog = false },
                title = {
                    Text(text = "Cấu hình địa chỉ FastAPI Backend", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Nhập địa chỉ máy chủ FastAPI chạy mô hình DenseNet-121:",
                            style = MaterialTheme.typography.bodySmall,
                            color = TeaTextSecondary
                        )

                        OutlinedTextField(
                            value = inputUrl,
                            onValueChange = { inputUrl = it },
                            label = { Text("Base URL") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TeaGreenPrimary,
                                unfocusedBorderColor = TeaBorder
                            )
                        )

                        Text(
                            text = "Gợi ý kết nối:\n• Máy ảo Android Emulator: http://10.0.2.2:8000/\n• Điện thoại thật (cùng Wi-Fi): http://192.168.1.x:8000/ (thay x bằng IP máy tính)",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 16.sp),
                            color = TeaTextSecondary
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.updateBaseUrl(inputUrl)
                            showConfigUrlDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TeaGreenPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Lưu & Kiểm tra")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showConfigUrlDialog = false },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Hủy", color = TeaTextSecondary)
                    }
                },
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}
