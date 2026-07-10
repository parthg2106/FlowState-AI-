package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.FlowViewModel

@Composable
fun RightFlowAIBar(
    viewModel: FlowViewModel,
    modifier: Modifier = Modifier
) {
    val insight by viewModel.flowAiInsight.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()
    val prefs by viewModel.userPreferences.collectAsState()

    val totalTasks = allTasks.size
    val completedTasks = allTasks.count { it.isCompleted }
    val remainingTasks = totalTasks - completedTasks
    val progressPercent = if (totalTasks > 0) (completedTasks.toFloat() / totalTasks) else 0.29f

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(DarkBg)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Flow AI Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4AF2A1))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Flow AI",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Active",
                    color = Color(0xFF4AF2A1),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Collapse",
                tint = TextSecondaryDark,
                modifier = Modifier.size(16.dp)
            )
        }

        // 1. INSIGHT CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = LavenderMuted.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp),
            border = RowDefaults.cardBorder(LavenderAccent.copy(alpha = 0.15f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Insight",
                        tint = LavenderAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "INSIGHT",
                        color = LavenderAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = insight,
                    color = Color.White,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        // 2. SUGGESTIONS LIST
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "SUGGESTIONS",
                color = TextSecondaryDark,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 0.5.sp
            )

            // Suggestion 1: Break suggestion
            SuggestionRow(
                icon = Icons.Default.Coffee,
                title = "5-min movement break",
                subtitle = "Seated for 80 min"
            )

            // Suggestion 2: Task suggestions
            SuggestionRow(
                icon = Icons.Default.Adjust,
                title = "$remainingTasks tasks to daily goal",
                subtitle = "Goal: ${prefs?.dailyGoalCount ?: 6} completed"
            )

            // Suggestion 3: Productivity Peak suggestion
            SuggestionRow(
                icon = Icons.Default.NightsStay,
                title = "Prime time in 2 hours",
                subtitle = "9 pm focus peak"
            )
        }

        // 3. QUICK ACTIONS
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "QUICK ACTIONS",
                color = TextSecondaryDark,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 0.5.sp
            )

            QuickActionItem(
                title = "Start focus session",
                icon = Icons.Default.PlayArrow,
                onClick = { viewModel.selectTab("Focus Mode") }
            )

            QuickActionItem(
                title = "Review my tasks",
                icon = Icons.Default.AssignmentTurnedIn,
                onClick = { viewModel.selectTab("Tasks") }
            )

            QuickActionItem(
                title = "Open calendar",
                icon = Icons.Default.CalendarToday,
                onClick = { viewModel.selectTab("Calendar") }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // 4. TODAY'S PROGRESS DONUT
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurface)
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "TODAY'S PROGRESS",
                color = TextSecondaryDark,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 0.5.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Donut Chart Canvas
                Box(
                    modifier = Modifier.size(54.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Track
                        drawCircle(
                            color = DarkBorder,
                            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                        )
                        // Progress Arc
                        drawArc(
                            color = LavenderAccent,
                            startAngle = -90f,
                            sweepAngle = 360f * progressPercent,
                            useCenter = false,
                            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Text(
                        text = "${(progressPercent * 100).toInt()}%",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "$completedTasks done",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "$remainingTasks remaining",
                        color = TextSecondaryDark,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SuggestionRow(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurface)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextSecondaryDark,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            )
            Text(
                text = subtitle,
                color = TextSecondaryDark,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun QuickActionItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = LavenderAccent,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 13.sp
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextSecondaryDark,
            modifier = Modifier.size(14.dp)
        )
    }
}

object RowDefaults {
    fun cardBorder(color: Color) = androidx.compose.foundation.BorderStroke(1.dp, color)
}
