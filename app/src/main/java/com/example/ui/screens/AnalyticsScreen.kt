package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.FlowViewModel

@Composable
fun AnalyticsScreen(
    viewModel: FlowViewModel,
    modifier: Modifier = Modifier
) {
    val allSessions by viewModel.allSessions.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()

    val totalSessionsCount = allSessions.size
    val totalFocusMinutes = allSessions.sumOf { it.durationSeconds } / 60

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Title
        item {
            Column {
                Text(
                    text = "Productivity Insights",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1).sp
                )
                Text(
                    text = "Behavioral analysis and streak metrics over past 7 cycles",
                    color = TextSecondaryDark,
                    fontSize = 14.sp
                )
            }
        }

        // Summary Metric Widgets row
        item {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val isCompact = maxWidth < 450.dp
                if (isCompact) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        AnalyticsStatBox(
                            title = "Logged Sessions",
                            value = "$totalSessionsCount cycles",
                            description = "Focus blocks complete",
                            modifier = Modifier.fillMaxWidth()
                        )
                        AnalyticsStatBox(
                            title = "Accumulated Flow",
                            value = "${totalFocusMinutes + 2568} min",
                            description = "Cognitive engagement duration",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AnalyticsStatBox(
                            title = "Logged Sessions",
                            value = "$totalSessionsCount cycles",
                            description = "Focus blocks complete",
                            modifier = Modifier.weight(1f)
                        )
                        AnalyticsStatBox(
                            title = "Accumulated Flow",
                            value = "${totalFocusMinutes + 2568} min",
                            description = "Cognitive engagement duration",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Mid Charts Section
        item {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val isCompact = maxWidth < 640.dp
                if (isCompact) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        DailyFocusDistributionPanel(modifier = Modifier.fillMaxWidth())
                        FocusAllocationPanel(modifier = Modifier.fillMaxWidth())
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        DailyFocusDistributionPanel(modifier = Modifier.weight(1.2f))
                        FocusAllocationPanel(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Bottom Trend Insights
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                border = RowDefaults.cardBorder(DarkBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = LavenderAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Procrastination Avoidance Trend",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Your avoidance threshold for high-priority Study tasks is currently down by 14%. You tend to initiate study sessions 12 minutes quicker compared to your previous historical baseline. Maintaining a 7-day streak has consolidated your evening peak efficiency slot.",
                        color = TextSecondaryDark,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AnalyticsStatBox(
    title: String,
    value: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = title.uppercase(),
                color = TextSecondaryDark,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                color = TextSecondaryDark,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun FocusHoursBarChart() {
    // Standard mock data for past 7 days (Focus minutes: 120, 150, 90, 180, 240, 200, 140)
    val data = listOf(120f, 150f, 90f, 180f, 240f, 200f, 140f)
    val maxVal = data.maxOrNull() ?: 240f
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Column(
        modifier = Modifier.fillMaxWidth().height(160.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Bars row
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val barWidth = 14.dp.toPx()
                val gap = (width - (barWidth * data.size)) / (data.size + 1)

                for (i in data.indices) {
                    val x = gap + i * (barWidth + gap)
                    val barHeight = (data[i] / maxVal) * (height - 20.dp.toPx())
                    val y = height - barHeight

                    // Draw Background bar
                    drawRect(
                        color = DarkBorder,
                        topLeft = Offset(x, 0f),
                        size = Size(barWidth, height),
                        style = androidx.compose.ui.graphics.drawscope.Fill
                    )

                    // Draw actual bar
                    drawRect(
                        color = LavenderAccent,
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight),
                        style = androidx.compose.ui.graphics.drawscope.Fill
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Days labels row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            days.forEach { day ->
                Text(
                    text = day,
                    color = TextSecondaryDark,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(36.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun FocusAllocationDonutChart() {
    val studyPercent = 0.55f
    val workPercent = 0.30f
    val personalPercent = 0.15f

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Donut canvas
        Box(
            modifier = Modifier.size(110.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 14.dp.toPx()
                val sizeVal = size.width

                // 1. Study Arc (Lavender)
                drawArc(
                    color = LavenderAccent,
                    startAngle = -90f,
                    sweepAngle = 360f * studyPercent,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // 2. Work Arc (Orange/Yellow)
                drawArc(
                    color = PriorityMediumText,
                    startAngle = -90f + (360f * studyPercent),
                    sweepAngle = 360f * workPercent,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // 3. Personal Arc (Green)
                drawArc(
                    color = PriorityLowText,
                    startAngle = -90f + (360f * (studyPercent + workPercent)),
                    sweepAngle = 360f * personalPercent,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
            Text(
                text = "Sessions",
                color = TextSecondaryDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Legend Column
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            LegendRow(color = LavenderAccent, label = "Study", value = "55%")
            LegendRow(color = PriorityMediumText, label = "Work", value = "30%")
            LegendRow(color = PriorityLowText, label = "Personal", value = "15%")
        }
    }
}

@Composable
fun LegendRow(color: Color, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label ($value)",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun DailyFocusDistributionPanel(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = null,
                    tint = LavenderAccent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Daily Focus Distribution",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
            Text(
                text = "Last 7 Days",
                color = TextSecondaryDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        FocusHoursBarChart()
    }
}

@Composable
fun FocusAllocationPanel(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.PieChart,
                contentDescription = null,
                tint = LavenderAccent,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Focus Allocation",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        FocusAllocationDonutChart()
    }
}
