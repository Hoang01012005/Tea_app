package com.example.teacare.ui.screens.profile

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teacare.ui.components.TeaCareCard
import com.example.teacare.ui.components.TeaCareTopBar
import com.example.teacare.ui.components.TeaLeafLogo
import com.example.teacare.ui.theme.TeaBorder
import com.example.teacare.ui.theme.TeaGreenLight
import com.example.teacare.ui.theme.TeaGreenPrimary
import com.example.teacare.ui.theme.TeaTextSecondary
import com.example.teacare.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToCareSchedule: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var isAboutDialogOpen by remember { mutableStateOf(false) }
    var isPrivacyDialogOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TeaCareTopBar(title = "Hồ sơ & Cài đặt")

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. User Profile Header Card
            item {
                TeaCareCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 24
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(TeaGreenLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = null,
                                tint = TeaGreenPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = uiState.userName,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = uiState.gardenName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TeaTextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Nông hộ chè VietGAP",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = TeaGreenPrimary
                            )
                        }

                        IconButton(onClick = { viewModel.openEditProfileDialog() }) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Chỉnh sửa",
                                tint = TeaGreenPrimary
                            )
                        }
                    }
                }
            }

            // 2. Shortcuts Section
            item {
                Text(
                    text = "QUẢN LÝ DỮ LIỆU",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                    color = TeaGreenPrimary,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            item {
                SettingsActionTile(
                    icon = Icons.Filled.CalendarMonth,
                    title = "Lịch chăm sóc đồi chè",
                    subtitle = "Quản lý công việc tưới, bón phân, tỉa cành",
                    onClick = onNavigateToCareSchedule
                )
            }

            item {
                SettingsActionTile(
                    icon = Icons.Filled.History,
                    title = "Lịch sử chẩn đoán bệnh",
                    subtitle = "Xem lại các mẫu lá chè đã phân tích",
                    onClick = onNavigateToHistory
                )
            }

            // 3. App Settings Section
            item {
                Text(
                    text = "TÙY CHỌN ỨNG DỤNG",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                    color = TeaGreenPrimary,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            item {
                SettingsSwitchTile(
                    icon = Icons.Filled.DarkMode,
                    title = "Chế độ nền tối (Dark Mode)",
                    subtitle = "Giao diện tối giúp dịu mắt và tiết kiệm pin",
                    checked = uiState.isDarkMode,
                    onCheckedChange = { viewModel.toggleDarkMode(it) }
                )
            }

            item {
                SettingsSwitchTile(
                    icon = Icons.Filled.Notifications,
                    title = "Thông báo nhắc việc",
                    subtitle = "Nhắc nhở lịch phun thuốc và chăm sóc chè",
                    checked = uiState.notificationsEnabled,
                    onCheckedChange = { viewModel.toggleNotifications(it) }
                )
            }

            item {
                SettingsActionTile(
                    icon = Icons.Filled.Settings,
                    title = "Cấu hình FastAPI Backend",
                    subtitle = uiState.apiBaseUrl,
                    onClick = { viewModel.openEditUrlDialog() }
                )
            }

            // 4. About & Privacy Section
            item {
                Text(
                    text = "THÔNG TIN & BẢO MẬT",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                    color = TeaGreenPrimary,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            item {
                SettingsActionTile(
                    icon = Icons.Filled.Info,
                    title = "Giới thiệu TeaCare",
                    subtitle = "Phiên bản 1.0.0 (Đồ án sinh viên)",
                    onClick = { isAboutDialogOpen = true }
                )
            }

            item {
                SettingsActionTile(
                    icon = Icons.Filled.Security,
                    title = "Chính sách quyền riêng tư",
                    subtitle = "Bảo mật hình ảnh và dữ liệu nông hộ",
                    onClick = { isPrivacyDialogOpen = true }
                )
            }
        }

        // Edit Profile Dialog
        if (uiState.isEditProfileDialogOpen) {
            EditProfileDialog(
                currentName = uiState.userName,
                currentGarden = uiState.gardenName,
                onDismiss = { viewModel.dismissEditProfileDialog() },
                onSave = { name, garden -> viewModel.updateProfile(name, garden) }
            )
        }

        // Edit FastAPI Base URL Dialog
        if (uiState.isEditUrlDialogOpen) {
            EditUrlDialog(
                currentUrl = uiState.apiBaseUrl,
                onDismiss = { viewModel.dismissEditUrlDialog() },
                onSave = { url -> viewModel.updateApiBaseUrl(url) }
            )
        }

        // About Dialog
        if (isAboutDialogOpen) {
            AlertDialog(
                onDismissRequest = { isAboutDialogOpen = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TeaLeafLogo(size = 32.dp, glow = false)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Về TeaCare", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "TeaCare – Smart Tea Plant Care là giải pháp ứng dụng di động hỗ trợ người trồng chè nhận diện bệnh học, tra cứu giải pháp phòng trừ và quản lý kế hoạch chăm sóc vườn chè thông minh.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Công nghệ: Jetpack Compose, Material 3, Room Database, DataStore, Coil.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TeaTextSecondary
                        )
                        Text(
                            text = "• Thiết kế: Premium Minimalism + Nature Tech.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TeaTextSecondary
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { isAboutDialogOpen = false },
                        colors = ButtonDefaults.buttonColors(containerColor = TeaGreenPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Đóng")
                    }
                },
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.surface
            )
        }

        // Privacy Policy Dialog
        if (isPrivacyDialogOpen) {
            AlertDialog(
                onDismissRequest = { isPrivacyDialogOpen = false },
                title = {
                    Text(text = "Chính sách bảo mật", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Cam kết quyền riêng tư người dùng:",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "1. Mọi hình ảnh chụp lá chè được xử lý cục bộ trên thiết bị, tuyệt đối không âm thầm gửi lên máy chủ ngoài mà không có sự đồng ý.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "2. Dữ liệu công việc chăm sóc và lịch sử chẩn đoán được lưu trữ an toàn trong Room Database trên máy của bạn.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "3. Ứng dụng chỉ yêu cầu quyền truy cập thư viện ảnh tối thiểu khi người dùng chủ động chọn ảnh bằng Photo Picker.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { isPrivacyDialogOpen = false },
                        colors = ButtonDefaults.buttonColors(containerColor = TeaGreenPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Đã hiểu")
                    }
                },
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}

@Composable
private fun SettingsActionTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    TeaCareCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 18,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(TeaGreenLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TeaGreenPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TeaTextSecondary
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = TeaTextSecondary.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun SettingsSwitchTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    TeaCareCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 18
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(TeaGreenLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TeaGreenPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TeaTextSecondary
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = TeaGreenPrimary
                )
            )
        }
    }
}

@Composable
private fun EditProfileDialog(
    currentName: String,
    currentGarden: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var garden by remember { mutableStateOf(currentGarden) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Chỉnh sửa thông tin", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên người dùng") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TeaGreenPrimary,
                        unfocusedBorderColor = TeaBorder
                    )
                )

                OutlinedTextField(
                    value = garden,
                    onValueChange = { garden = it },
                    label = { Text("Tên vườn chè / Nông hộ") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TeaGreenPrimary,
                        unfocusedBorderColor = TeaBorder
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, garden) },
                colors = ButtonDefaults.buttonColors(containerColor = TeaGreenPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Lưu")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TeaBorder)
            ) {
                Text("Hủy", color = TeaTextSecondary)
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun EditUrlDialog(
    currentUrl: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var url by remember { mutableStateOf(currentUrl) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Cấu hình địa chỉ FastAPI Backend", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Nhập URL máy chủ chạy mô hình DenseNet-121:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TeaTextSecondary
                )
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
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
                onClick = { onSave(url) },
                colors = ButtonDefaults.buttonColors(containerColor = TeaGreenPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Lưu cấu hình")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Hủy", color = TeaTextSecondary)
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}
