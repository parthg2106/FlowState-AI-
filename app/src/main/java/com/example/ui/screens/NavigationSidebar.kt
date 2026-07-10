package com.example.ui.screens

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.FlowViewModel

@Composable
fun NavigationSidebar(
    viewModel: FlowViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val timerRunning by viewModel.timerRunning.collectAsState()
    val timerSecondsRemaining by viewModel.timerSecondsRemaining.collectAsState()
    val prefs by viewModel.userPreferences.collectAsState()

    val navItems = listOf(
        NavigationItem("Dashboard", Icons.Default.GridView, Icons.Outlined.GridView, "dashboard_tab"),
        NavigationItem("Tasks", Icons.Default.CheckCircle, Icons.Outlined.CheckCircle, "tasks_tab"),
        NavigationItem("Calendar", Icons.Default.CalendarToday, Icons.Outlined.CalendarToday, "calendar_tab"),
        NavigationItem("Focus Mode", Icons.Default.Timer, Icons.Outlined.Timer, "focus_tab"),
        NavigationItem("Analytics", Icons.Default.BarChart, Icons.Outlined.BarChart, "analytics_tab"),
        NavigationItem("AI Assistant", Icons.Default.AutoAwesome, Icons.Outlined.AutoAwesome, "ai_tab"),
        NavigationItem("Settings", Icons.Default.Settings, Icons.Outlined.Settings, "settings_tab")
    )

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(240.dp)
            .background(DarkBg)
            .padding(vertical = 24.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Logo & Brand Header
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(LavenderMuted),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = "FlowState",
                            tint = LavenderAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "FlowState",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "AI PRODUCTIVITY",
                            color = TextSecondaryDark,
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                IconButton(
                    onClick = { viewModel.setShowInfoDialog(true) },
                    modifier = Modifier.size(32.dp).testTag("info_button_sidebar")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "About App & Developer",
                        tint = TextSecondaryDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Navigation Tabs
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                navItems.forEach { item ->
                    val isSelected = currentTab == item.title
                    val icon = if (isSelected) item.selectedIcon else item.unselectedIcon
                    val bg = if (isSelected) Color(0xFF1B1A24) else Color.Transparent
                    val textColor = if (isSelected) Color.White else TextSecondaryDark
                    val iconColor = if (isSelected) LavenderAccent else TextSecondaryDark

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(bg)
                            .clickable { viewModel.selectTab(item.title) }
                            .padding(horizontal = 12.dp)
                            .testTag(item.tag),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = item.title,
                            tint = iconColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = item.title,
                            color = textColor,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Bottom widgets (Focus status & User Profile)
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Running focus overlay indicator
            if (timerRunning) {
                val minutes = timerSecondsRemaining / 60
                val seconds = timerSecondsRemaining % 60
                val timeString = String.format("%02d:%02d", minutes, seconds)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(LavenderMuted)
                        .border(1.dp, color = LavenderAccent.copy(alpha = 0.3f), shape = RoundedCornerShape(12.dp))
                        .clickable { viewModel.selectTab("Focus Mode") }
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Focus session running",
                        color = LavenderAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = timeString,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // User Profile Widget
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurface)
                    .clickable { viewModel.selectTab("Settings") }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(LavenderAccent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (prefs?.userName ?: "J").take(2).uppercase(),
                        color = LavenderAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = prefs?.userName ?: "Jordan Smith",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                    Text(
                        text = "Pro Member",
                        color = TextSecondaryDark,
                        fontSize = 11.sp
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Profile Settings",
                    tint = TextSecondaryDark,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

data class NavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
)
