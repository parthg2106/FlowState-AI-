package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.FlowViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkBg)
                ) { innerPadding ->
                    FlowEcosystemApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun FlowEcosystemApp(
    modifier: Modifier = Modifier,
    viewModel: FlowViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val showInfoDialog by viewModel.showInfoDialog.collectAsState()

    if (showInfoDialog) {
        InfoDialog(onDismiss = { viewModel.setShowInfoDialog(false) })
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        val isWideScreen = maxWidth >= 840.dp

        if (isWideScreen) {
            // Tablet & Landscape Widescreen layout: SIDEBAR + MAIN CONTENT + RIGHT SIDEBAR
            Row(modifier = Modifier.fillMaxSize()) {
                // Left Navigation Sidebar
                NavigationSidebar(
                    viewModel = viewModel,
                    modifier = Modifier.weight(1f)
                )

                Spacer(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(DarkBorder)
                )

                // Main Content center screen
                Box(
                    modifier = Modifier
                        .weight(3.5f)
                        .fillMaxHeight()
                ) {
                    ActiveScreenLoader(currentTab = currentTab, viewModel = viewModel)
                }

                Spacer(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(DarkBorder)
                )

                // Right Flow AI Sidebar
                RightFlowAIBar(
                    viewModel = viewModel,
                    modifier = Modifier.weight(1.3f)
                )
            }
        } else {
            // Portrait Mobile layout: Content area + Bottom Navigation Bar
            Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = DarkSurface,
                        contentColor = TextSecondaryDark,
                        modifier = Modifier
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .testTag("mobile_bottom_nav_bar")
                    ) {
                        val items = listOf(
                            Triple("Dashboard", Icons.Default.GridView, "dashboard_m"),
                            Triple("Tasks", Icons.Default.CheckCircle, "tasks_m"),
                            Triple("Focus Mode", Icons.Default.Timer, "focus_m"),
                            Triple("Analytics", Icons.Default.BarChart, "analytics_m"),
                            Triple("AI Assistant", Icons.Default.AutoAwesome, "ai_m")
                        )

                        items.forEach { (tab, icon, tag) ->
                            val isSelected = currentTab == tab
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.selectTab(tab) },
                                icon = {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = tab,
                                        tint = if (isSelected) LavenderAccent else TextSecondaryDark
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab,
                                        fontSize = 10.sp,
                                        color = if (isSelected) Color.White else TextSecondaryDark
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = LavenderMuted
                                ),
                                modifier = Modifier.testTag(tag)
                            )
                        }
                    }
                },
                containerColor = DarkBg
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    ActiveScreenLoader(currentTab = currentTab, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun ActiveScreenLoader(
    currentTab: String,
    viewModel: FlowViewModel
) {
    when (currentTab) {
        "Dashboard" -> DashboardScreen(viewModel = viewModel)
        "Tasks" -> TasksScreen(viewModel = viewModel)
        "Calendar" -> CalendarScreen(viewModel = viewModel)
        "Focus Mode" -> FocusScreen(viewModel = viewModel)
        "Analytics" -> AnalyticsScreen(viewModel = viewModel)
        "AI Assistant" -> AIScreen(viewModel = viewModel)
        "Settings" -> SettingsScreen(viewModel = viewModel)
        else -> DashboardScreen(viewModel = viewModel)
    }
}

@Composable
fun InfoDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = LavenderAccent,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "About FlowState",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                // Section 1: The App
                Column {
                    Text(
                        text = "THE APPLICATION",
                        color = LavenderAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "FlowState is an immersive focus ecosystem designed to optimize cognitive performance, manage critical task hierarchies, visualize long-term streaks, and provide bespoke analytical feedback powered by state-of-the-art Gemini AI.",
                        color = Color.White,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }

                // Divider
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(DarkBorder)
                )

                // Section 2: Developer
                Column {
                    Text(
                        text = "THE DEVELOPER",
                        color = LavenderAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Parth Ghodke",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Computer Science & Engineering Student",
                        color = TextSecondaryDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Hello! I am Parth Ghodke, a Computer Science & Engineering student passionate about AI, software development, and building innovative projects. I enjoy learning new technologies, participating in hackathons, and turning ideas into real-world solutions. Always curious, always learning, and always building.",
                        color = Color.White,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = LavenderAccent),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    text = "Refocus",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        containerColor = DarkSurface,
        shape = RoundedCornerShape(16.dp)
    )
}
