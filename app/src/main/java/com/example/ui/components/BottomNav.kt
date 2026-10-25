package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.TranslationData
import com.example.ui.theme.*

enum class NavScreen(val translationKey: String, val icon: ImageVector, val testTag: String) {
    HOME("home", Icons.Default.Female, "nav_home"),
    DASHBOARD("dashboard", Icons.Default.MonitorHeart, "nav_dashboard"),
    TRIAGE("triage", Icons.Default.Emergency, "nav_triage"),
    CHAT("ask_shakthi", Icons.Default.ChatBubble, "nav_chat"),
    PORTAL("portal", Icons.Default.MedicalServices, "nav_portal")
}

@Composable
fun BottomNav(
    currentScreen: NavScreen,
    language: AppLanguage,
    onScreenSelected: (NavScreen) -> Unit
) {
    Surface(
        color = HeraSurfaceContainerLowest,
        shadowElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("bottom_nav_surface")
    ) {
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(60.dp)
                .testTag("bottom_nav_bar"),
            containerColor = HeraSurfaceContainerLowest,
            tonalElevation = 0.dp
        ) {
            NavScreen.values().forEach { screen ->
                val isSelected = currentScreen == screen
                val label = TranslationData.get(language, screen.translationKey)

                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onScreenSelected(screen) },
                    icon = {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = label,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = HeraPrimary,
                        selectedTextColor = HeraPrimary,
                        unselectedIconColor = HeraOnSurfaceVariant,
                        unselectedTextColor = HeraOnSurfaceVariant,
                        indicatorColor = HeraPrimaryFixed.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.testTag(screen.testTag)
                )
            }
        }
    }
}
