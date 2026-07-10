package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TaskEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.FlowViewModel

@Composable
fun CalendarScreen(
    viewModel: FlowViewModel,
    modifier: Modifier = Modifier
) {
    val allTasks by viewModel.allTasks.collectAsState()
    var selectedDay by remember { mutableStateOf(9) } // Default: July 9, 2026

    // Calendar grid data for July 2026 (starts on Wednesday)
    val daysInMonth = 31
    val startOffset = 3 // Wednesday offset

    val activeDayTasks = allTasks.filter { task ->
        // For simulation, tasks are distributed across July 9 and 10
        if (selectedDay == 9) !task.isCompleted else task.isCompleted
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(24.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Productivity Schedule",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1).sp
                )
                Text(
                    text = "Visualizing deadlines and cognitive peak focus times",
                    color = TextSecondaryDark,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val isCompact = maxWidth < 640.dp
            
            if (isCompact) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CalendarGridBox(
                        startOffset = startOffset,
                        daysInMonth = daysInMonth,
                        selectedDay = selectedDay,
                        onDaySelected = { selectedDay = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    TimelineScheduleSlots(
                        selectedDay = selectedDay,
                        activeDayTasks = activeDayTasks,
                        modifier = Modifier.fillMaxWidth().height(420.dp)
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CalendarGridBox(
                        startOffset = startOffset,
                        daysInMonth = daysInMonth,
                        selectedDay = selectedDay,
                        onDaySelected = { selectedDay = it },
                        modifier = Modifier.weight(1.3f)
                    )
                    
                    TimelineScheduleSlots(
                        selectedDay = selectedDay,
                        activeDayTasks = activeDayTasks,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun TimelineSlot(
    time: String,
    title: String,
    type: String,
    priority: String? = null
) {
    val leftAccent = when (type) {
        "Focus" -> LavenderAccent
        "Task" -> if (priority == "HIGH") PriorityHighText else PriorityMediumText
        "Routine" -> Color(0xFF4AF2A1)
        else -> TextSecondaryDark
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(DarkBg)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.width(64.dp)) {
            Text(
                text = time,
                color = TextSecondaryDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        Box(
            modifier = Modifier
                .width(4.dp)
                .height(30.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(leftAccent)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Text(
                text = type.uppercase(),
                color = TextSecondaryDark,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun CalendarGridBox(
    startOffset: Int,
    daysInMonth: Int,
    selectedDay: Int,
    onDaySelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        // Month Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "July 2026",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Row {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Previous",
                    tint = TextSecondaryDark,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Next",
                    tint = TextSecondaryDark,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Days of week header
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su").forEach { day ->
                Text(
                    text = day,
                    color = TextSecondaryDark,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Calendar Numbers grid
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            var currentDay = 1
            for (row in 0..5) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0..6) {
                        val index = row * 7 + col
                        if (index < startOffset || currentDay > daysInMonth) {
                            // Empty slot
                            Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                        } else {
                            val isSelected = currentDay == selectedDay
                            val isToday = currentDay == 9
                            val dayNum = currentDay

                            val bg = when {
                                isSelected -> LavenderAccent
                                isToday -> LavenderMuted
                                else -> Color.Transparent
                            }
                            val textColor = when {
                                isSelected -> Color.Black
                                isToday -> LavenderAccent
                                else -> Color.White
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(bg)
                                    .clickable { onDaySelected(dayNum) },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$dayNum",
                                        color = textColor,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    // Dot for task deadlines
                                    if (dayNum == 9 || dayNum == 10 || dayNum == 12) {
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) Color.Black else LavenderAccent)
                                        )
                                    }
                                }
                            }
                            currentDay++
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineScheduleSlots(
    selectedDay: Int,
    activeDayTasks: List<TaskEntity>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Daily Schedule · Day $selectedDay",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            item {
                TimelineSlot(time = "09:00 AM", title = "Morning Standup & Planning", type = "Routine")
            }
            item {
                TimelineSlot(time = "10:30 AM", title = "Deep Work: Pomodoro study slot", type = "Focus")
            }

            if (activeDayTasks.isNotEmpty()) {
                items(activeDayTasks) { task ->
                    TimelineSlot(
                        time = "02:00 PM",
                        title = task.title,
                        type = "Task",
                        priority = task.priority
                    )
                }
            } else {
                item {
                    TimelineSlot(time = "02:30 PM", title = "Avoidance Buffer Block", type = "Buffer")
                }
            }

            item {
                TimelineSlot(time = "05:00 PM", title = "Sunset Breathing & Rest", type = "Routine")
            }
            item {
                TimelineSlot(time = "09:00 PM", title = "Prime Focus Peak block", type = "Focus")
            }
        }
    }
}
