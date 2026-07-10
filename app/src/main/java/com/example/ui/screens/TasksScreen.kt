package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TaskEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.FlowViewModel

@Composable
fun TasksScreen(
    viewModel: FlowViewModel,
    modifier: Modifier = Modifier
) {
    val allTasks by viewModel.allTasks.collectAsState()

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("ALL") } // "ALL", "ACTIVE", "COMPLETED"

    val filteredTasks = when (selectedFilter) {
        "ACTIVE" -> allTasks.filter { !it.isCompleted }
        "COMPLETED" -> allTasks.filter { it.isCompleted }
        else -> allTasks
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(24.dp)
    ) {
        // Top row: Header & Add
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Task Center",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1).sp
                )
                Text(
                    text = "Maintain study habits and avoid procrastination blockers",
                    color = TextSecondaryDark,
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = { showAddTaskDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = LavenderAccent),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .height(40.dp)
                    .testTag("add_task_center_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Task", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Filters row
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("ALL", "ACTIVE", "COMPLETED").forEach { filter ->
                val isSelected = selectedFilter == filter
                val bg = if (isSelected) LavenderMuted else DarkSurface
                val border = if (isSelected) LavenderAccent else DarkBorder
                val textColor = if (isSelected) Color.White else TextSecondaryDark

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(bg)
                        .border(1.dp, border, RoundedCornerShape(12.dp))
                        .clickable { selectedFilter = filter }
                        .padding(vertical = 10.dp, horizontal = 18.dp)
                ) {
                    Text(
                        text = filter,
                        color = textColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Tasks list
        if (filteredTasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Inbox,
                        contentDescription = "Empty",
                        tint = DarkBorder,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No tasks match your selection",
                        color = TextSecondaryDark,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredTasks, key = { it.id }) { task ->
                    DetailedTaskItem(
                        task = task,
                        onCheckedChange = { viewModel.toggleTaskCompletion(task) },
                        onDeleteClick = { viewModel.deleteTask(task.id) },
                        onFocusSelect = { viewModel.selectTaskForFocus(task); viewModel.selectTab("Focus Mode") }
                    )
                }
            }
        }
    }

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
fun DetailedTaskItem(
    task: TaskEntity,
    onCheckedChange: () -> Unit,
    onDeleteClick: () -> Unit,
    onFocusSelect: () -> Unit
) {
    val (priorityBg, priorityText) = when (task.priority.uppercase()) {
        "HIGH" -> PriorityHighBg to PriorityHighText
        "MEDIUM" -> PriorityMediumBg to PriorityMediumText
        else -> PriorityLowBg to PriorityLowText
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(14.dp),
        border = RowDefaults.cardBorder(DarkBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
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
                    )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = task.title,
                        color = if (task.isCompleted) TextSecondaryDark else Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Priority Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(priorityBg)
                                .padding(vertical = 2.dp, horizontal = 6.dp)
                        ) {
                            Text(
                                text = task.priority,
                                color = priorityText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Category Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(DarkBg)
                                .padding(vertical = 2.dp, horizontal = 6.dp)
                        ) {
                            Text(
                                text = task.category,
                                color = TextSecondaryDark,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Time allocation Badge
                        Icon(
                            imageVector = Icons.Default.HourglassEmpty,
                            contentDescription = "Time spend",
                            tint = TextSecondaryDark,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = "${task.actualMinutes}/${task.estimatedMinutes} min",
                            color = TextSecondaryDark,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (!task.isCompleted) {
                    IconButton(
                        onClick = onFocusSelect,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(LavenderMuted)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Select for Focus Timer",
                            tint = LavenderAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Task",
                        tint = PriorityHighText,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
