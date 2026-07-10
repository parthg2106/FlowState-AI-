package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
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
import com.example.data.UserPreferenceEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.FlowViewModel

@Composable
fun SettingsScreen(
    viewModel: FlowViewModel,
    modifier: Modifier = Modifier
) {
    val prefs by viewModel.userPreferences.collectAsState()

    var userNameInput by remember(prefs) { mutableStateOf(prefs?.userName ?: "Jordan Smith") }
    var selectedPersonality by remember(prefs) { mutableStateOf(prefs?.aiPersonality ?: "Analytical Advisor") }
    var selectedSound by remember(prefs) { mutableStateOf(prefs?.focusSound ?: "None") }
    var dailyGoalInput by remember(prefs) { mutableStateOf((prefs?.dailyGoalCount ?: 6).toString()) }

    var saveConfirmationMessage by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(24.dp)
    ) {
        // Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LavenderMuted),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = LavenderAccent,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "System Settings",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1).sp
                )
                Text(
                    text = "Configure system indicators, sounds, and Flow AI character settings",
                    color = TextSecondaryDark,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.weight(1f)
        ) {
            // Block 1: Profile Username
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(14.dp),
                border = RowDefaults.cardBorder(DarkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PROFILE SETTINGS",
                        color = TextSecondaryDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = userNameInput,
                        onValueChange = { userNameInput = it },
                        label = { Text("User Display Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LavenderAccent,
                            focusedLabelColor = LavenderAccent,
                            unfocusedLabelColor = TextSecondaryDark,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("settings_username_input")
                    )
                }
            }

            // Block 2: Flow AI Character configuration
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(14.dp),
                border = RowDefaults.cardBorder(DarkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "FLOW AI ADVISOR CHARACTER",
                        color = TextSecondaryDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf("Analytical Advisor", "Strict Mentor", "Encouraging Coach").forEach { personality ->
                            val isSelected = selectedPersonality == personality
                            val bg = if (isSelected) LavenderMuted else Color.Black.copy(alpha = 0.2f)
                            val border = if (isSelected) LavenderAccent else DarkBorder
                            val textColor = if (isSelected) Color.White else TextSecondaryDark

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(bg)
                                    .border(1.dp, border, RoundedCornerShape(10.dp))
                                    .clickable { selectedPersonality = personality }
                                    .padding(vertical = 12.dp, horizontal = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = personality,
                                    color = textColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Block 3: Seeder & Database Utilities
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(14.dp),
                border = RowDefaults.cardBorder(DarkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "DATA MANAGEMENT UTILITIES",
                        color = TextSecondaryDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1.2f)) {
                            Text(
                                text = "Seed Database Records",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Seeds mock tasks, streaks, and focus blocks for historical analytical charts instantly.",
                                color = TextSecondaryDark,
                                fontSize = 11.sp
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.resetDatabase()
                                saveConfirmationMessage = "Database seeded successfully!"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LavenderMuted),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("seed_database_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = LavenderAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Seed DB",
                                color = LavenderAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Save confirmation banner
        if (saveConfirmationMessage.isNotBlank()) {
            Text(
                text = saveConfirmationMessage,
                color = Color(0xFF4AF2A1),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Bottom Save button
        Button(
            onClick = {
                viewModel.updatePreferences(
                    name = userNameInput,
                    aiPersonality = selectedPersonality,
                    focusSound = selectedSound,
                    dailyGoal = dailyGoalInput.toIntOrNull() ?: 6
                )
                saveConfirmationMessage = "Configuration saved successfully!"
            },
            colors = ButtonDefaults.buttonColors(containerColor = LavenderAccent),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("save_settings_button")
        ) {
            Text("Save Configuration", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}
