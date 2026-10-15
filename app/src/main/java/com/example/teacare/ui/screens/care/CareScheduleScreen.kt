package com.example.teacare.ui.screens.care

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teacare.data.model.CareTask
import com.example.teacare.ui.components.EmptyStateView
import com.example.teacare.ui.components.PriorityBadge
import com.example.teacare.ui.components.TeaCareCard
import com.example.teacare.ui.components.TeaCareTopBar
import com.example.teacare.ui.theme.TeaError
import com.example.teacare.ui.theme.TeaGreenLight
import com.example.teacare.ui.theme.TeaGreenPrimary
import com.example.teacare.ui.theme.TeaTextMuted
import com.example.teacare.ui.theme.TeaTextSecondary
import com.example.teacare.viewmodel.CareScheduleViewModel
import com.example.teacare.viewmodel.TaskFilterTab
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CareScheduleScreen(
    viewModel: CareScheduleViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TeaCareTopBar(
                title = "Lịch chăm sóc đồi chè",
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddTaskDialog() },
                containerColor = TeaGreenPrimary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Thêm công việc"
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs: Tất cả, Cần làm, Đã xong
            TabRow(
                selectedTabIndex = uiState.currentTab.ordinal,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = TeaGreenPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[uiState.currentTab.ordinal]),
                        color = TeaGreenPrimary
                    )
                }
            ) {
                TaskFilterTab.values().forEach { tab ->
                    val isSelected = uiState.currentTab == tab
                    Tab(
                        selected = isSelected,
                        onClick = { viewModel.onTabSelected(tab) },
                        text = {
                            Text(
                                text = tab.label,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                ),
                                color = if (isSelected) TeaGreenPrimary else TeaTextSecondary
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tasks List
            if (uiState.filteredTasks.isEmpty()) {
                EmptyStateView(
                    title = "Chưa có công việc nào",
                    description = when (uiState.currentTab) {
                        TaskFilterTab.COMPLETED -> "Bạn chưa hoàn thành công việc nào trong danh mục này."
                        TaskFilterTab.PENDING -> "Tuyệt vời! Hiện không có công việc nào đang chờ thực hiện."
                        TaskFilterTab.ALL -> "Hãy bắt đầu lên lịch bón phân, tưới ẩm, tỉa cành hoặc phun phòng trừ cho vườn chè."
                    },
                    icon = Icons.Outlined.CalendarMonth,
                    actionButtonText = "Thêm việc mới",
                    onActionClick = { viewModel.openAddTaskDialog() }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.filteredTasks, key = { it.id }) { task ->
                        CareTaskCard(
                            task = task,
                            onToggle = { isChecked -> viewModel.toggleTask(task.id, isChecked) },
                            onEdit = { viewModel.openEditTaskDialog(task) },
                            onDelete = { viewModel.deleteTask(task) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(70.dp))
                    }
                }
            }
        }

        // Dialog thêm / sửa công việc
        if (uiState.isAddEditDialogOpen) {
            AddEditTaskDialog(
                taskToEdit = uiState.taskToEdit,
                onDismiss = { viewModel.dismissAddEditDialog() },
                onSave = { title, note, dueDate, priority, category ->
                    viewModel.saveTask(title, note, dueDate, priority, category)
                }
            )
        }
    }
}

@Composable
private fun CareTaskCard(
    task: CareTask,
    onToggle: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(task.dueDateMillis))

    TeaCareCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 18
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = onToggle,
                    colors = CheckboxDefaults.colors(
                        checkedColor = TeaGreenPrimary,
                        checkmarkColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.width(6.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        color = if (task.isCompleted) TeaTextMuted else MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Hạn: $dateStr • ${task.category.label}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TeaTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                PriorityBadge(priority = task.priority)
            }

            if (task.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = task.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = TeaTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Action row: Edit, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Chỉnh sửa",
                        tint = TeaGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.DeleteOutline,
                        contentDescription = "Xóa",
                        tint = TeaError,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
