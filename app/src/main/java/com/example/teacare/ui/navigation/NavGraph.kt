package com.example.teacare.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.teacare.TeaCareApplication
import com.example.teacare.ui.screens.care.CareScheduleScreen
import com.example.teacare.ui.screens.detection.DetectionScreen
import com.example.teacare.ui.screens.history.ScanHistoryScreen
import com.example.teacare.ui.screens.home.HomeScreen
import com.example.teacare.ui.screens.library.DiseaseDetailScreen
import com.example.teacare.ui.screens.library.DiseaseLibraryScreen
import com.example.teacare.ui.screens.onboarding.OnboardingScreen
import com.example.teacare.ui.screens.profile.ProfileScreen
import com.example.teacare.ui.screens.splash.SplashScreen
import com.example.teacare.viewmodel.CareScheduleViewModel
import com.example.teacare.viewmodel.DetectionViewModel
import com.example.teacare.viewmodel.DiseaseLibraryViewModel
import com.example.teacare.viewmodel.HistoryViewModel
import com.example.teacare.viewmodel.HomeViewModel
import com.example.teacare.viewmodel.ProfileViewModel

@Composable
fun TeaCareNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val app = TeaCareApplication.instance

    // ViewModels with dependency injection from Application class
    val homeViewModel = remember {
        HomeViewModel(
            careTaskRepository = app.careTaskRepository,
            scanHistoryRepository = app.scanHistoryRepository,
            diseaseRepository = app.diseaseRepository,
            userPreferences = app.userPreferences
        )
    }

    val detectionViewModel = remember {
        DetectionViewModel(
            detectionEngine = app.detectionEngine,
            scanHistoryRepository = app.scanHistoryRepository,
            careTaskRepository = app.careTaskRepository,
            userPreferences = app.userPreferences
        )
    }

    val libraryViewModel = remember {
        DiseaseLibraryViewModel(diseaseRepository = app.diseaseRepository)
    }

    val careScheduleViewModel = remember {
        CareScheduleViewModel(careTaskRepository = app.careTaskRepository)
    }

    val historyViewModel = remember {
        HistoryViewModel(scanHistoryRepository = app.scanHistoryRepository)
    }

    val profileViewModel = remember {
        ProfileViewModel(userPreferences = app.userPreferences)
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToNext = { hasCompletedOnboarding ->
                    val destination = if (hasCompletedOnboarding) Screen.Main.route else Screen.Onboarding.route
                    navController.navigate(destination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinishOnboarding = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainContainerScreen(
                homeViewModel = homeViewModel,
                detectionViewModel = detectionViewModel,
                libraryViewModel = libraryViewModel,
                profileViewModel = profileViewModel,
                onNavigateToCareSchedule = { navController.navigate(Screen.CareSchedule.route) },
                onNavigateToHistory = { navController.navigate(Screen.ScanHistory.route) },
                onNavigateToDiseaseDetail = { id -> navController.navigate(Screen.DiseaseDetail.createRoute(id)) }
            )
        }

        composable(Screen.CareSchedule.route) {
            CareScheduleScreen(
                viewModel = careScheduleViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.ScanHistory.route) {
            ScanHistoryScreen(
                viewModel = historyViewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToDetection = {
                    navController.navigate(Screen.Main.route)
                },
                onNavigateToDiseaseDetail = { id ->
                    navController.navigate(Screen.DiseaseDetail.createRoute(id))
                }
            )
        }

        composable(
            route = Screen.DiseaseDetail.route,
            arguments = listOf(navArgument("diseaseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val diseaseId = backStackEntry.arguments?.getString("diseaseId") ?: ""
            DiseaseDetailScreen(
                diseaseId = diseaseId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun MainContainerScreen(
    homeViewModel: HomeViewModel,
    detectionViewModel: DetectionViewModel,
    libraryViewModel: DiseaseLibraryViewModel,
    profileViewModel: ProfileViewModel,
    onNavigateToCareSchedule: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToDiseaseDetail: (String) -> Unit
) {
    var currentTab by remember { mutableStateOf(BottomTab.HOME) }

    Scaffold(
        bottomBar = {
            TeaCareBottomNavBar(
                currentTab = currentTab,
                onTabSelected = { currentTab = it }
            )
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "tab_crossfade",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { targetTab ->
            when (targetTab) {
                BottomTab.HOME -> {
                    HomeScreen(
                        viewModel = homeViewModel,
                        onNavigateToDetection = { currentTab = BottomTab.DETECTION },
                        onNavigateToLibrary = { currentTab = BottomTab.LIBRARY },
                        onNavigateToCareSchedule = onNavigateToCareSchedule,
                        onNavigateToHistory = onNavigateToHistory,
                        onNavigateToDiseaseDetail = onNavigateToDiseaseDetail
                    )
                }
                BottomTab.DETECTION -> {
                    DetectionScreen(
                        viewModel = detectionViewModel,
                        onNavigateToDiseaseDetail = onNavigateToDiseaseDetail
                    )
                }
                BottomTab.LIBRARY -> {
                    DiseaseLibraryScreen(
                        viewModel = libraryViewModel,
                        onNavigateToDetail = onNavigateToDiseaseDetail
                    )
                }
                BottomTab.PROFILE -> {
                    ProfileScreen(
                        viewModel = profileViewModel,
                        onNavigateToCareSchedule = onNavigateToCareSchedule,
                        onNavigateToHistory = onNavigateToHistory
                    )
                }
            }
        }
    }
}
