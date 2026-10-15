package com.example.teacare.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teacare.data.model.DiseaseSeverity
import com.example.teacare.data.model.TaskPriority
import com.example.teacare.ui.theme.TeaError
import com.example.teacare.ui.theme.TeaErrorBg
import com.example.teacare.ui.theme.TeaInfo
import com.example.teacare.ui.theme.TeaInfoBg
import com.example.teacare.ui.theme.TeaSuccess
import com.example.teacare.ui.theme.TeaSuccessBg
import com.example.teacare.ui.theme.TeaWarning
import com.example.teacare.ui.theme.TeaWarningBg

@Composable
fun SeverityBadge(
    severity: DiseaseSeverity,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (severity) {
        DiseaseSeverity.HEALTHY -> Pair(TeaSuccessBg, TeaSuccess)
        DiseaseSeverity.LOW -> Pair(TeaInfoBg, TeaInfo)
        DiseaseSeverity.MEDIUM -> Pair(TeaWarningBg, TeaWarning)
        DiseaseSeverity.HIGH -> Pair(TeaErrorBg, TeaError)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = severity.label,
            color = textColor,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
        )
    }
}

@Composable
fun PriorityBadge(
    priority: TaskPriority,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (priority) {
        TaskPriority.LOW -> Pair(TeaSuccessBg, TeaSuccess)
        TaskPriority.MEDIUM -> Pair(TeaWarningBg, TeaWarning)
        TaskPriority.HIGH -> Pair(TeaErrorBg, TeaError)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = priority.label,
            color = textColor,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
        )
    }
}

@Composable
fun DemoModeBadge(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFEDE9FE))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = "Thử nghiệm AI",
            color = Color(0xFF6D28D9),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
        )
    }
}
