package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TaskEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.FlowViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    viewModel: FlowViewModel,
    modifier: Modifier = Modifier
) {
    val allTasks by viewModel.allTasks.collectAsState()
    val activeTasks by viewModel.activeTasks.collectAsState()
    val allSessions by viewModel.allSessions.collectAsState()
    val prefs by viewModel.userPreferences.collectAsState()

    // Timer States
    val timerRunning by viewModel.timerRunning.collectAsState()
    val timerSecondsRemaining by viewModel.timerSecondsRemaining.collectAsState()
    val totalSeconds by viewModel.totalSessionSeconds.collectAsState()

    // Dialog state
    var showAddTaskDialog by remember { mutableStateOf(false) }

    val completedTasksCount = allTasks.count { it.isCompleted }
    val totalTasksCount = allTasks.size

    val productivityPercent = if (totalTasksCount > 0) {
        ((completedTasksCount.toFloat() / totalTasksCount) * 100).toInt()
    } else {
        29
    }

    // Calculating total focus hours (mock + sessions)
    val sessionMinutes = allSessions.sumOf { it.durationSeconds } / 60
    val focusHours = 42.8f + (sessionMinutes.toFloat() / 60f)

    val todayStr = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. HEADER SECTION
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Good evening, ${prefs?.userName ?: "Jordan"}",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-1).sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$todayStr · You're on a ${prefs?.streakDays ?: 7}-day streak",
                        color = TextSecondaryDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.setShowInfoDialog(true) },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(DarkSurface)
                            .border(1.dp, DarkBorder, CircleShape)
                            .testTag("info_button_header")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "About App & Developer",
                            tint = LavenderAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Button(
                        onClick = { showAddTaskDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = LavenderAccent),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .height(40.dp)
                            .testTag("add_task_header_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Task",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "New Task",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 2. METRIC CARDS GRID (RESPONSIVE)
        item {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val isCompact = maxWidth < 640.dp
                val isVeryCompact = maxWidth < 400.dp
                
                if (isVeryCompact) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        MetricCard(
                            icon = Icons.Default.Adjust,
                            title = "Productivity",
                            value = "$productivityPercent%",
                            subValue = "+12% vs last week",
                            subValueColor = Color(0xFF4AF2A1),
                            modifier = Modifier.fillMaxWidth()
                        )
                        MetricCard(
                            icon = Icons.Default.LocalFireDepartment,
                            title = "Study Streak",
                            value = "${prefs?.streakDays ?: 7} days",
                            subValue = "Personal best",
                            subValueColor = Color(0xFFFFD08F),
                            modifier = Modifier.fillMaxWidth()
                        )
                        MetricCard(
                            icon = Icons.Default.CheckCircle,
                            title = "Tasks Done",
                            value = "$completedTasksCount / $totalTasksCount",
                            subValue = "Today",
                            subValueColor = TextSecondaryDark,
                            modifier = Modifier.fillMaxWidth()
                        )
                        MetricCard(
                            icon = Icons.Default.EmojiEvents,
                            title = "Focus Hours",
                            value = String.format("%.1f h", focusHours),
                            subValue = "This week",
                            subValueColor = LavenderAccent,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else if (isCompact) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            MetricCard(
                                icon = Icons.Default.Adjust,
                                title = "Productivity",
                                value = "$productivityPercent%",
                                subValue = "+12% vs last week",
                                subValueColor = Color(0xFF4AF2A1),
                                modifier = Modifier.weight(1f).fillMaxHeight()
                            )
                            MetricCard(
                                icon = Icons.Default.LocalFireDepartment,
                                title = "Study Streak",
                                value = "${prefs?.streakDays ?: 7} days",
                                subValue = "Personal best",
                                subValueColor = Color(0xFFFFD08F),
                                modifier = Modifier.weight(1f).fillMaxHeight()
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            MetricCard(
                                icon = Icons.Default.CheckCircle,
                                title = "Tasks Done",
                                value = "$completedTasksCount / $totalTasksCount",
                                subValue = "Today",
                                subValueColor = TextSecondaryDark,
                                modifier = Modifier.weight(1f).fillMaxHeight()
                            )
                            MetricCard(
                                icon = Icons.Default.EmojiEvents,
                                title = "Focus Hours",
                                value = String.format("%.1f h", focusHours),
                                subValue = "This week",
                                subValueColor = LavenderAccent,
                                modifier = Modifier.weight(1f).fillMaxHeight()
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        MetricCard(
                            icon = Icons.Default.Adjust,
                            title = "Productivity",
                            value = "$productivityPercent%",
                            subValue = "+12% vs last week",
                            subValueColor = Color(0xFF4AF2A1),
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                        MetricCard(
                            icon = Icons.Default.LocalFireDepartment,
                            title = "Study Streak",
                            value = "${prefs?.streakDays ?: 7} days",
                            subValue = "Personal best",
                            subValueColor = Color(0xFFFFD08F),
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                        MetricCard(
                            icon = Icons.Default.CheckCircle,
                            title = "Tasks Done",
                            value = "$completedTasksCount / $totalTasksCount",
                            subValue = "Today",
                            subValueColor = TextSecondaryDark,
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                        MetricCard(
                            icon = Icons.Default.EmojiEvents,
                            title = "Focus Hours",
                            value = String.format("%.1f h", focusHours),
                            subValue = "This week",
                            subValueColor = LavenderAccent,
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                    }
                }
            }
        }

        // 3. MID-SECTION CORES (RESPONSIVE)
        item {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val isCompact = maxWidth < 640.dp
                
                if (isCompact) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        TodayTasksPanel(
                            allTasks = allTasks,
                            completedTasksCount = completedTasksCount,
                            totalTasksCount = totalTasksCount,
                            viewModel = viewModel,
                            modifier = Modifier.fillMaxWidth()
                        )
                        FocusTimerPanel(
                            timerRunning = timerRunning,
                            timerSecondsRemaining = timerSecondsRemaining,
                            totalSeconds = totalSeconds,
                            viewModel = viewModel,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        TodayTasksPanel(
                            allTasks = allTasks,
                            completedTasksCount = completedTasksCount,
                            totalTasksCount = totalTasksCount,
                            viewModel = viewModel,
                            modifier = Modifier.weight(1.2f)
                        )
                        FocusTimerPanel(
                            timerRunning = timerRunning,
                            timerSecondsRemaining = timerSecondsRemaining,
                            totalSeconds = totalSeconds,
                            viewModel = viewModel,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 4. BOTTOM BANNER AI ADVICE
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                border = RowDefaults.cardBorder(DarkBorder)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(LavenderMuted),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Assistant",
                            tint = LavenderAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "AI INSIGHT FOR ${prefs?.userName?.uppercase() ?: "JORDAN"}",
                            color = LavenderAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Studying with the 50-10 focus ratio during your peak timing (9:00 PM) yields 22% higher recall accuracy.",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }
    }

    // Add Task Dialog
    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onAdd = { title, priority, category, minutes ->
                viewModel.addTask(title, priority, category, minutes)
                showAddTaskDialog = false
            }
        )
    }
}

@Composable
fun MetricCard(
    icon: ImageVector,
    title: String,
    value: String,
    subValue: String,
    subValueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        border = RowDefaults.cardBorder(DarkBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(LavenderMuted),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = LavenderAccent,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = title,
                color = TextSecondaryDark,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subValue,
                color = subValueColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
fun TaskItemRow(
    task: TaskEntity,
    onCheckedChange: () -> Unit
) {
    val (priorityBg, priorityText) = when (task.priority.uppercase()) {
        "HIGH" -> PriorityHighBg to PriorityHighText
        "MEDIUM" -> PriorityMediumBg to PriorityMediumText
        else -> PriorityLowBg to PriorityLowText
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(DarkBg)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onCheckedChange() },
                colors = CheckboxDefaults.colors(
                    checkedColor = LavenderAccent,
                    uncheckedColor = TextSecondaryDark,
                    checkmarkColor = Color.Black
                ),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = task.title,
                color = if (task.isCompleted) TextSecondaryDark else Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                maxLines = 1
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Category tag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(DarkBorder)
                    .padding(vertical = 2.dp, horizontal = 6.dp)
            ) {
                Text(
                    text = task.category,
                    color = TextSecondaryDark,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Priority tag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(priorityBg)
                    .padding(vertical = 2.dp, horizontal = 6.dp)
            ) {
                Text(
                    text = task.priority.lowercase(),
                    color = priorityText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf("HIGH") }
    var selectedCategory by remember { mutableStateOf("Study") }
    var minutes by remember { mutableStateOf("25") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "New Focus Task", color = Color.White) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LavenderAccent,
                        focusedLabelColor = LavenderAccent,
                        unfocusedLabelColor = TextSecondaryDark,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Priority Selection
                Column {
                    Text("Priority", color = TextSecondaryDark, fontSize = 12.sp)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("HIGH", "MEDIUM", "LOW").forEach { priority ->
                            val isSelected = selectedPriority == priority
                            val border = if (isSelected) LavenderAccent else DarkBorder
                            val bg = if (isSelected) LavenderMuted else Color.Transparent

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(bg)
                                    .border(1.dp, border, RoundedCornerShape(8.dp))
                                    .clickable { selectedPriority = priority }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = priority,
                                    color = if (isSelected) LavenderAccent else TextSecondaryDark,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Category Selection
                Column {
                    Text("Category", color = TextSecondaryDark, fontSize = 12.sp)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("Study", "Work", "Personal").forEach { category ->
                            val isSelected = selectedCategory == category
                            val border = if (isSelected) LavenderAccent else DarkBorder
                            val bg = if (isSelected) LavenderMuted else Color.Transparent

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(bg)
                                    .border(1.dp, border, RoundedCornerShape(8.dp))
                                    .clickable { selectedCategory = category }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = category,
                                    color = if (isSelected) LavenderAccent else TextSecondaryDark,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = minutes,
                    onValueChange = { minutes = it },
                    label = { Text("Est. Minutes") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LavenderAccent,
                        focusedLabelColor = LavenderAccent,
                        unfocusedLabelColor = TextSecondaryDark,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onAdd(
                            title,
                            selectedPriority,
                            selectedCategory,
                            minutes.toIntOrNull() ?: 25
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = LavenderAccent)
            ) {
                Text("Add Task")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondaryDark)
            }
        },
        containerColor = DarkSurface
    )
}

@Composable
fun TodayTasksPanel(
    allTasks: List<TaskEntity>,
    completedTasksCount: Int,
    totalTasksCount: Int,
    viewModel: FlowViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
            .padding(20.dp)
            .height(310.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today's Tasks",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "$completedTasksCount of $totalTasksCount",
                color = TextSecondaryDark,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (allTasks.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "All tasks completed! Take a break.",
                    color = TextSecondaryDark,
                    fontSize = 13.sp
                )
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxHeight()
            ) {
                allTasks.take(4).forEach { task ->
                    TaskItemRow(task = task, onCheckedChange = {
                        viewModel.toggleTaskCompletion(task)
                    })
                }
            }
        }
    }
}

@Composable
fun FocusTimerPanel(
    timerRunning: Boolean,
    timerSecondsRemaining: Int,
    totalSeconds: Int,
    viewModel: FlowViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
            .padding(20.dp)
            .height(310.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Focus Timer",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Timer Circular Progress
        Box(
            modifier = Modifier.size(130.dp),
            contentAlignment = Alignment.Center
        ) {
            val progress = timerSecondsRemaining.toFloat() / totalSeconds.toFloat()
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Background circle
                drawCircle(
                    color = DarkBorder,
                    style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                )
                // Progress sweep
                drawArc(
                    color = LavenderAccent,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Time digits
            val min = timerSecondsRemaining / 60
            val sec = timerSecondsRemaining % 60
            val digits = String.format("%02d:%02d", min, sec)

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = digits,
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "FOCUS",
                    color = TextSecondaryDark,
                    fontSize = 9.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Timer Controls Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { viewModel.toggleTimer() },
                colors = ButtonDefaults.buttonColors(containerColor = LavenderAccent),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .height(36.dp)
                    .width(90.dp)
            ) {
                Text(
                    text = if (timerRunning) "Pause" else "Start",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = { viewModel.resetTimer() },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(DarkBorder)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset Timer",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            IconButton(
                onClick = { viewModel.selectTab("Focus Mode") },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(DarkBorder)
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInFull,
                    contentDescription = "Fullscreen Immersive",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
