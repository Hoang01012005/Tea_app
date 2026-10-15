package com.example.teacare.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teacare.ui.theme.TeaBorder
import com.example.teacare.ui.theme.TeaGreenLight
import com.example.teacare.ui.theme.TeaGreenPrimary
import com.example.teacare.ui.theme.TeaTextSecondary

@Composable
fun TeaCareBottomNavBar(
    currentTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .height(72.dp)
            .border(
                width = 1.dp,
                color = TeaBorder.copy(alpha = 0.6f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        BottomTab.values().forEach { tab ->
            val isSelected = currentTab == tab
            val iconTint by animateColorAsState(
                targetValue = if (isSelected) TeaGreenPrimary else TeaTextSecondary,
                label = "icon_color"
            )

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                        contentDescription = tab.title,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp
                        ),
                        color = iconTint
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = TeaGreenLight,
                    selectedIconColor = TeaGreenPrimary,
                    selectedTextColor = TeaGreenPrimary,
                    unselectedIconColor = TeaTextSecondary,
                    unselectedTextColor = TeaTextSecondary
                )
            )
        }
    }
}
