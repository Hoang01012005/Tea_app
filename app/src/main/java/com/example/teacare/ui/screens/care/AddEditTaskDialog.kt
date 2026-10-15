package com.example.teacare.ui.screens.care

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teacare.data.model.CareTask
import com.example.teacare.data.model.TaskCategory
import com.example.teacare.data.model.TaskPriority
import com.example.teacare.ui.theme.TeaBorder
import com.example.teacare.ui.theme.TeaGreenLight
import com.example.teacare.ui.theme.TeaGreenPrimary
import com.example.teacare.ui.theme.TeaTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddEditTaskDialog(
    taskToEdit: CareTask?,
    onDismiss: () -> Unit,
    onSave: (title: String, note: String, dueDateMillis: Long, priority: TaskPriority, category: TaskCategory) -> Unit
) {
    var title by remember { mutableStateOf(taskToEdit?.title ?: "") }
    var note by remember { mutableStateOf(taskToEdit?.note ?: "") }
    var priority by remember { mutableStateOf(taskToEdit?.priority ?: TaskPriority.MEDIUM) }
    var category by remember { mutableStateOf(taskToEdit?.category ?: TaskCategory.TREATMENT) }
    var dueDateDaysAhead by remember { mutableLongStateOf(1L) } // default: ngày mai
    var dueDateMillis by remember {
        mutableLongStateOf(taskToEdit?.dueDateMillis ?: (System.currentTimeMillis() + 86400000L * 1))
    }

    var titleError by remember { mutableStateOf(false) }

    val dateOptions = listOf(
        Pair("Hôm nay", 0L),
        Pair("Ngày mai", 1L),
        Pair("Sau 3 ngày", 3L),
        Pair("Sau 1 tuần", 7L)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (taskToEdit != null) "Chỉnh sửa công việc" else "Thêm công việc chăm sóc",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Tiêu đề
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = false
                    },
                    label = { Text("Tên công việc *") },
                    isError = titleError,
                    supportingText = {
                        if (titleError) Text("Vui lòng nhập tên công việc", color = MaterialTheme.colorScheme.error)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TeaGreenPrimary,
                        unfocusedBorderColor = TeaBorder
                    )
                )

                // Ghi chú chi tiết
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Ghi chú / Liều lượng / Hướng dẫn") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TeaGreenPrimary,
                        unfocusedBorderColor = TeaBorder
                    )
                )

                // Chọn hạn thực hiện
                Column {
                    Text(
                        text = "Hạn thực hiện:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = TeaTextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(dateOptions) { option ->
                            val isSelected = dueDateDaysAhead == option.second
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) TeaGreenPrimary else MaterialTheme.colorScheme.surface)
                                    .border(1.dp, if (isSelected) TeaGreenPrimary else TeaBorder, RoundedCornerShape(10.dp))
                                    .clickable {
                                        dueDateDaysAhead = option.second
                                        dueDateMillis = System.currentTimeMillis() + 86400000L * option.second
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = option.first,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Chọn mức độ ưu tiên
                Column {
                    Text(
                        text = "Mức độ ưu tiên:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = TeaTextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TaskPriority.values().forEach { prio ->
                            val isSelected = priority == prio
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) TeaGreenPrimary else MaterialTheme.colorScheme.surface)
                                    .border(1.dp, if (isSelected) TeaGreenPrimary else TeaBorder, RoundedCornerShape(10.dp))
                                    .clickable { priority = prio }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = prio.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Chọn phân loại việc
                Column {
                    Text(
                        text = "Phân loại chăm sóc:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = TeaTextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(TaskCategory.values()) { cat ->
                            val isSelected = category == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) TeaGreenLight else MaterialTheme.colorScheme.surface)
                                    .border(1.dp, if (isSelected) TeaGreenPrimary else TeaBorder, RoundedCornerShape(10.dp))
                                    .clickable { category = cat }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cat.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) TeaGreenPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        titleError = true
                    } else {
                        onSave(title.trim(), note.trim(), dueDateMillis, priority, category)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TeaGreenPrimary)
            ) {
                Text("Lưu công việc")
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
