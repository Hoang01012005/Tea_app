package com.example.teacare.ui.screens.library

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.LocalFlorist
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.teacare.data.model.Disease
import com.example.teacare.data.model.DiseaseCategory
import com.example.teacare.data.model.DiseaseSeverity
import com.example.teacare.ui.components.EmptyStateView
import com.example.teacare.ui.components.SeverityBadge
import com.example.teacare.ui.components.TeaCareCard
import com.example.teacare.ui.components.TeaCareTopBar
import com.example.teacare.ui.theme.TeaBorder
import com.example.teacare.ui.theme.TeaGreenLight
import com.example.teacare.ui.theme.TeaGreenPrimary
import com.example.teacare.ui.theme.TeaTextSecondary
import com.example.teacare.viewmodel.DiseaseLibraryViewModel

@Composable
fun DiseaseLibraryScreen(
    viewModel: DiseaseLibraryViewModel,
    onNavigateToDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TeaCareTopBar(title = "Thư viện bệnh cây chè")

        // 1. Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Tìm kiếm theo tên bệnh, nấm hại...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TeaTextSecondary
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Tìm kiếm",
                        tint = TeaGreenPrimary
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Xóa",
                                tint = TeaTextSecondary
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TeaGreenPrimary,
                    unfocusedBorderColor = TeaBorder,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }

        // 2. Filter Chips Row (Category)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = uiState.selectedCategory == null,
                    onClick = { viewModel.onCategorySelected(null) },
                    label = { Text("Tất cả nhóm") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TeaGreenLight,
                        selectedLabelColor = TeaGreenPrimary
                    )
                )
            }
            items(DiseaseCategory.values()) { category ->
                FilterChip(
                    selected = uiState.selectedCategory == category,
                    onClick = { viewModel.onCategorySelected(category) },
                    label = { Text(category.label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TeaGreenLight,
                        selectedLabelColor = TeaGreenPrimary
                    )
                )
            }
        }

        // 3. Filter Chips Row (Severity)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = uiState.selectedSeverity == null,
                    onClick = { viewModel.onSeveritySelected(null) },
                    label = { Text("Mọi mức độ") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TeaGreenLight,
                        selectedLabelColor = TeaGreenPrimary
                    )
                )
            }
            items(DiseaseSeverity.values()) { severity ->
                FilterChip(
                    selected = uiState.selectedSeverity == severity,
                    onClick = { viewModel.onSeveritySelected(severity) },
                    label = { Text(severity.label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TeaGreenLight,
                        selectedLabelColor = TeaGreenPrimary
                    )
                )
            }
        }

        // 4. Disease List
        if (uiState.filteredDiseases.isEmpty()) {
            EmptyStateView(
                title = "Không tìm thấy kết quả",
                description = "Hãy thử thay đổi từ khóa tìm kiếm hoặc bộ lọc danh mục.",
                icon = Icons.Outlined.Eco,
                actionButtonText = "Xóa bộ lọc",
                onActionClick = {
                    viewModel.onSearchQueryChanged("")
                    viewModel.onCategorySelected(null)
                    viewModel.onSeveritySelected(null)
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.filteredDiseases, key = { it.id }) { disease ->
                    DiseaseLibraryCard(
                        disease = disease,
                        onClick = { onNavigateToDetail(disease.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DiseaseLibraryCard(
    disease: Disease,
    onClick: () -> Unit
) {
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
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(TeaGreenLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocalFlorist,
                    contentDescription = null,
                    tint = TeaGreenPrimary,
                    modifier = Modifier.size(28.dp)
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
                        text = disease.vietnameseName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    SeverityBadge(severity = disease.severity)
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = disease.scientificName,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        fontSize = 12.sp
                    ),
                    color = TeaGreenPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = disease.shortDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = TeaTextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = TeaTextSecondary.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
