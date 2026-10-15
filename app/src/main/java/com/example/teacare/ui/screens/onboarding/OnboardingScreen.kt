package com.example.teacare.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CenterFocusStrong
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teacare.TeaCareApplication
import com.example.teacare.ui.components.TeaCarePrimaryButton
import com.example.teacare.ui.components.TeaLeafLogo
import com.example.teacare.ui.theme.TeaBorder
import com.example.teacare.ui.theme.TeaGreenLight
import com.example.teacare.ui.theme.TeaGreenPrimary
import com.example.teacare.ui.theme.TeaTextPrimary
import com.example.teacare.ui.theme.TeaTextSecondary
import kotlinx.coroutines.launch

data class OnboardingPageData(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val highlightBadge: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val pages = listOf(
        OnboardingPageData(
            title = "Chẩn đoán bệnh lá chè bằng thị giác máy tính",
            description = "Chụp ảnh lá chè bị tổn thương để nhận diện tức thì các bệnh phổ biến như Phồng lá, Đốm xám, Thán thư và côn trùng chích hút.",
            icon = Icons.Outlined.CenterFocusStrong,
            highlightBadge = "Trí tuệ nhân tạo"
        ),
        OnboardingPageData(
            title = "Cơ sở tri thức bệnh học chè chuyên sâu",
            description = "Tra cứu đầy đủ triệu chứng, tác nhân gây bệnh, biện pháp phòng trừ sinh học và danh mục thuốc BVTV an toàn đạt chuẩn VietGAP.",
            icon = Icons.Outlined.Eco,
            highlightBadge = "Cẩm nang nông nghiệp"
        ),
        OnboardingPageData(
            title = "Quản lý lịch chăm sóc & bảo vệ đồi chè",
            description = "Lên kế hoạch bón phân, tưới ẩm, đốn tỉa và phun phòng dịch hại theo mùa vụ để đạt năng suất và phẩm cấp búp chè cao nhất.",
            icon = Icons.Outlined.CalendarMonth,
            highlightBadge = "Nông hộ thông minh"
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })

    fun completeOnboarding() {
        coroutineScope.launch {
            TeaCareApplication.instance.userPreferences.setOnboardingCompleted(true)
            onFinishOnboarding()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        // Top row with Skip button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TeaLeafLogo(size = 32.dp, glow = false)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "TeaCare",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TeaGreenPrimary
                )
            }

            AnimatedVisibility(
                visible = pagerState.currentPage < pages.size - 1,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                TextButton(onClick = { completeOnboarding() }) {
                    Text(
                        text = "Bỏ qua",
                        color = TeaTextSecondary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        // Pager Content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { pageIndex ->
            val page = pages[pageIndex]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Circle illustration container
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape)
                        .background(TeaGreenLight.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = page.icon,
                        contentDescription = null,
                        tint = TeaGreenPrimary,
                        modifier = Modifier.size(80.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(TeaGreenPrimary.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = page.highlightBadge,
                        color = TeaGreenPrimary,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = page.title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        lineHeight = 30.sp
                    ),
                    color = TeaTextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = page.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 22.sp
                    ),
                    color = TeaTextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Bottom Controls: Page Indicator & Action Button
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dots indicator
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                repeat(pages.size) { iteration ->
                    val isSelected = pagerState.currentPage == iteration
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .height(8.dp)
                            .width(if (isSelected) 24.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) TeaGreenPrimary else TeaBorder)
                    )
                }
            }

            if (pagerState.currentPage == pages.size - 1) {
                TeaCarePrimaryButton(
                    text = "Bắt đầu ngay",
                    onClick = { completeOnboarding() }
                )
            } else {
                TeaCarePrimaryButton(
                    text = "Tiếp theo",
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
