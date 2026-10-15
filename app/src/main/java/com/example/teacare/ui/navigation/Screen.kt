package com.example.teacare.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CenterFocusStrong
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Main : Screen("main")
    object CareSchedule : Screen("care_schedule")
    object ScanHistory : Screen("scan_history")
    object DiseaseDetail : Screen("disease_detail/{diseaseId}") {
        fun createRoute(diseaseId: String) = "disease_detail/$diseaseId"
    }
}

enum class BottomTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("Trang chủ", Icons.Filled.Home, Icons.Outlined.Home),
    DETECTION("Nhận diện", Icons.Filled.CenterFocusStrong, Icons.Outlined.CenterFocusStrong),
    LIBRARY("Thư viện", Icons.Filled.Book, Icons.Outlined.Book),
    PROFILE("Cá nhân", Icons.Filled.Person, Icons.Outlined.Person)
}
