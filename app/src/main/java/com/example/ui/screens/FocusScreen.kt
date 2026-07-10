package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
fun FocusScreen(
    viewModel: FlowViewModel,
    modifier: Modifier = Modifier
) {
    val timerRunning by viewModel.timerRunning.collectAsState()
    val timerSecondsRemaining by viewModel.timerSecondsRemaining.collectAsState()
    val totalSeconds by viewModel.totalSessionSeconds.collectAsState()
    val selectedTask by viewModel.selectedTaskForTimer.collectAsState()
    val prefs by viewModel.userPreferences.collectAsState()

    var activeSound by remember { mutableStateOf(prefs?.focusSound ?: "None") }

    // Breathing Animation State (Pulsating scale 0.8f to 1.3f)
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Calculate breathing prompt based on scale progress
    val breathingPrompt = when {
        breatheScale > 1.15f -> "HOLD"
        breatheScale < 0.95f -> "BREATHE OUT"
        else -> "BREATHE IN"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. TOP HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Deep Focus Chamber",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1).sp
                )
                Text(
                    text = if (selectedTask != null) "Focusing on: ${selectedTask?.title}" else "Unclutter your mind and initiate deep flow state",
                    color = TextSecondaryDark,
                    fontSize = 14.sp
                )
            }
        }

        // 2. MIDDLE BREATHING CORE & TIMER
        Box(
            modifier = Modifier
                .size(320.dp),
            contentAlignment = Alignment.Center
        ) {
            // Pulsating glow background
            Box(
                modifier = Modifier
                    .size((180 * breatheScale).dp)
                    .clip(CircleShape)
                    .background(LavenderAccent.copy(alpha = 0.08f))
            )

            // Inner glowing circle
            Box(
                modifier = Modifier
                    .size((140 * breatheScale).dp)
                    .clip(CircleShape)
                    .background(LavenderAccent.copy(alpha = 0.15f))
                    .border(1.dp, LavenderAccent.copy(alpha = 0.3f), CircleShape)
            )

            // Dynamic Progress Canvas
            val progress = timerSecondsRemaining.toFloat() / totalSeconds.toFloat()
            Canvas(modifier = Modifier.size(240.dp)) {
                drawCircle(
                    color = DarkBorder,
                    style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = LavenderAccent,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Central digits
            val min = timerSecondsRemaining / 60
            val sec = timerSecondsRemaining % 60
            val digits = String.format("%02d:%02d", min, sec)

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = breathingPrompt,
                    color = LavenderAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = digits,
                    color = Color.White,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "FLOW REMAINING",
                    color = TextSecondaryDark,
                    fontSize = 9.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 3. BOTTOM CONTROLS & MODULES
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Block 1: Pomodoro Slot Toggles
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.widthIn(max = 480.dp)
            ) {
                listOf(
                    FocusDurationOption("Focus Block", 25),
                    FocusDurationOption("Short Break", 5),
                    FocusDurationOption("Long Break", 15)
                ).forEach { opt ->
                    val isCurrent = (totalSeconds / 60) == opt.minutes
                    val bg = if (isCurrent) LavenderMuted else DarkSurface
                    val border = if (isCurrent) LavenderAccent else DarkBorder
                    val textColor = if (isCurrent) Color.White else TextSecondaryDark

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(bg)
                            .border(1.dp, border, RoundedCornerShape(12.dp))
                            .clickable { viewModel.setTimerDuration(opt.minutes) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = opt.label,
                            color = textColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Block 2: Sound Toggles
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AMBIENT SOUND",
                    color = TextSecondaryDark,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                SoundscapePill(label = "None", icon = Icons.Default.MusicOff, active = activeSound == "None") {
                    activeSound = "None"
                }
                SoundscapePill(label = "Rain", icon = Icons.Default.CloudQueue, active = activeSound == "Rain") {
                    activeSound = "Rain"
                }
                SoundscapePill(label = "White Noise", icon = Icons.Default.Air, active = activeSound == "White Noise") {
                    activeSound = "White Noise"
                }
            }

            // Block 3: Action Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Button(
                    onClick = { viewModel.toggleTimer() },
                    colors = ButtonDefaults.buttonColors(containerColor = LavenderAccent),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .width(140.dp)
                ) {
                    Icon(
                        imageVector = if (timerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (timerRunning) "Pause Session" else "Start Session",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = { viewModel.resetTimer() },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(DarkSurface)
                        .border(1.dp, DarkBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Timer",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

data class FocusDurationOption(val label: String, val minutes: Int)

@Composable
fun SoundscapePill(
    label: String,
    icon: ImageVector,
    active: Boolean,
    onClick: () -> Unit
) {
    val bg = if (active) LavenderAccent.copy(alpha = 0.15f) else DarkSurface
    val border = if (active) LavenderAccent else DarkBorder
    val tint = if (active) LavenderAccent else TextSecondaryDark

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = if (active) Color.White else TextSecondaryDark,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
