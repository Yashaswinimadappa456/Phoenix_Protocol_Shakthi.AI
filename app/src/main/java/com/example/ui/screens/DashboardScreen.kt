package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppLanguage
import com.example.model.TranslationData
import com.example.ui.MainUiState
import com.example.ui.MainViewModel
import com.example.ui.components.NavScreen
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    uiState: MainUiState,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    var selectedStage by remember { mutableStateOf("pregnancy") }
    var showLangMenu by remember { mutableStateOf(false) }

    // Periods & Hormones Interactive State
    var currentCycleDay by remember { mutableStateOf(14) }
    var selectedCramp by remember { mutableStateOf("Moderate") }
    var selectedMood by remember { mutableStateOf("Calm") }
    var selectedBloating by remember { mutableStateOf("Mild") }
    var selectedFlow by remember { mutableStateOf("Medium") }
    var isListeningSpeech by remember { mutableStateOf(false) }

    // Speech Recognizer Launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isListeningSpeech = false
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                viewModel.setSymptomInput(spokenText)
                viewModel.setScreen(NavScreen.TRIAGE)
                viewModel.showToast("Captured: \"$spokenText\"")
            }
        }
    }

    val t: (String) -> String = { key -> TranslationData.get(uiState.language, key) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 76.dp)
            .testTag("dashboard_screen_container")
    ) {
        // Multilingual & Location Quick Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Multilingual selector pill
            Box {
                Surface(
                    color = HeraSurfaceContainerLow,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.clickable { showLangMenu = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Language",
                            tint = HeraPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${uiState.language.displayName} (${uiState.language.nativeName})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = HeraOnSurface
                        )
                        Icon(
                            imageVector = Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = HeraOnSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = showLangMenu,
                    onDismissRequest = { showLangMenu = false }
                ) {
                    AppLanguage.values().forEach { lang ->
                        DropdownMenuItem(
                            text = { Text("${lang.displayName} (${lang.nativeName})", fontSize = 12.sp, color = HeraOnSurface) },
                            onClick = {
                                viewModel.setLanguage(lang)
                                showLangMenu = false
                            }
                        )
                    }
                }
            }

            // ABDM ID Sync Token
            Surface(
                color = HeraSecondaryContainer.copy(alpha = 0.4f),
                shape = RoundedCornerShape(50)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = "Verified",
                        tint = HeraSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "ABHA: 91-4428-****",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = HeraOnSecondaryContainer
                    )
                }
            }
        }

        // Nearby Maternity & Emergency Centers Accordion
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = HeraSurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column {
                val chevronAngle by animateFloatAsState(
                    targetValue = if (uiState.isNearbyHospitalsExpanded) 180f else 0f,
                    label = "chevron"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.toggleHospitalsAccordion() }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(HeraPrimaryFixed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalHospital,
                                contentDescription = null,
                                tint = HeraPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Column {
                            Text(
                                text = t("nearby_hospitals_title"),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = HeraOnSurface
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(HeraSecondary)
                                )
                                Text(
                                    text = t("beds_available"),
                                    fontSize = 11.sp,
                                    color = HeraSecondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = "Expand",
                        tint = HeraOnSurface,
                        modifier = Modifier
                            .size(22.dp)
                            .rotate(chevronAngle)
                    )
                }

                AnimatedVisibility(visible = uiState.isNearbyHospitalsExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(HeraSurfaceContainerLowest)
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Hospital 1
                        Surface(
                            color = HeraSurfaceContainerLow,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(text = "Mallya Maternity Hospital", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HeraOnSurface)
                                        Surface(color = HeraSecondaryContainer, shape = RoundedCornerShape(4.dp)) {
                                            Text(text = "NABH", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = HeraSecondary, modifier = Modifier.padding(horizontal = 4.dp))
                                        }
                                    }
                                    Text(text = "1.8 km away • 4 Maternity suites free", fontSize = 11.sp, color = HeraOnSurfaceVariant)
                                }
                                Button(
                                    onClick = { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:08022277979"))) },
                                    colors = ButtonDefaults.buttonColors(containerColor = HeraPrimary),
                                    shape = RoundedCornerShape(50),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = t("call_btn"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Hospital 2
                        Surface(
                            color = HeraSurfaceContainerLow,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(text = "KC General Hospital", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HeraOnSurface)
                                        Surface(color = HeraErrorContainer, shape = RoundedCornerShape(4.dp)) {
                                            Text(text = "Govt / ABDM", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = HeraError, modifier = Modifier.padding(horizontal = 4.dp))
                                        }
                                    }
                                    Text(text = "3.2 km • Level-III NICU • 8 beds", fontSize = 11.sp, color = HeraOnSurfaceVariant)
                                }
                                Button(
                                    onClick = { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:08023341771"))) },
                                    colors = ButtonDefaults.buttonColors(containerColor = HeraSurfaceContainerHigh),
                                    shape = RoundedCornerShape(50),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = null, tint = HeraOnSurface, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = t("call_btn"), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HeraOnSurface)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Lifespan Care Spectrum Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.Spa, contentDescription = null, tint = HeraPrimary, modifier = Modifier.size(20.dp))
                Text(text = t("lifespan_spectrum"), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = HeraOnSurface)
            }
            Text(text = t("tap_explore"), fontSize = 11.sp, color = HeraOnSurfaceVariant, fontWeight = FontWeight.Medium)
        }

        // Horizontal Carousel Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val stages = listOf(
                "pregnancy" to t("tab_pregnancy"),
                "periods" to t("tab_periods"),
                "postpartum" to t("tab_postpartum"),
                "vitality" to t("tab_vitality")
            )

            stages.forEach { (key, label) ->
                val isSelected = selectedStage == key
                Surface(
                    color = if (isSelected) HeraPrimary else HeraSurfaceContainerLowest,
                    shape = RoundedCornerShape(50),
                    shadowElevation = 1.dp,
                    modifier = Modifier.clickable { selectedStage = key }
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (isSelected) HeraOnPrimary else HeraOnSurface,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // STAGE 1: PREGNANCY CARE
        if (selectedStage == "pregnancy") {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = HeraSurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.maternal_hero),
                            contentDescription = "Maternal stage",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, HeraInverseSurface.copy(alpha = 0.9f))
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(color = HeraSecondary, shape = RoundedCornerShape(50)) {
                                    Text(
                                        text = "Week 26 • Trimester 2",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HeraOnSecondary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Surface(color = HeraSurfaceContainerLowest.copy(alpha = 0.95f), shape = RoundedCornerShape(50)) {
                                    Text(
                                        text = "98 Days to EDD",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HeraPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Baby is the size of an Eggplant 🍆",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = HeraSurfaceContainerLow,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.TipsAndUpdates, contentDescription = null, tint = HeraPrimary, modifier = Modifier.size(20.dp))
                                Column {
                                    Text(text = "Trimester 2 Essential Insight", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HeraOnSurface)
                                    Text(
                                        text = "Auditory senses are now active. Listen to calming music, ensure adequate calcium intake, and practice lateral left-side resting.",
                                        fontSize = 11.sp,
                                        color = HeraOnSurface,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }

                        // Kick Counter & Hydration Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                color = HeraPrimaryFixed.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = t("baby_kicks"), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HeraPrimary)
                                    Row(
                                        verticalAlignment = Alignment.Bottom,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Text(text = "${uiState.kicksCount}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = HeraPrimary)
                                        Text(text = " / 10 target", fontSize = 11.sp, color = HeraOnSurface, modifier = Modifier.padding(bottom = 2.dp))
                                    }
                                    Button(
                                        onClick = { viewModel.incrementKicks() },
                                        colors = ButtonDefaults.buttonColors(containerColor = HeraPrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(vertical = 4.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(32.dp)
                                            .testTag("tap_kick_button")
                                    ) {
                                        Text(text = t("tap_kick"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Surface(
                                color = HeraTertiaryFixed.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = t("water_intake"), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HeraTertiary)
                                    Row(
                                        verticalAlignment = Alignment.Bottom,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Text(text = "${uiState.waterIntake}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = HeraTertiary)
                                        Text(text = " / 3.0 Liters", fontSize = 11.sp, color = HeraOnSurface, modifier = Modifier.padding(bottom = 2.dp))
                                    }
                                    Button(
                                        onClick = { viewModel.logWaterGlass() },
                                        colors = ButtonDefaults.buttonColors(containerColor = HeraTertiaryContainer),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(vertical = 4.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(32.dp)
                                            .testTag("log_water_button")
                                    ) {
                                        Text(text = t("add_water"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // IFA Dose
                        Surface(
                            color = HeraSurfaceContainerLow,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(HeraSecondaryContainer.copy(alpha = 0.7f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Medication, contentDescription = null, tint = HeraSecondary, modifier = Modifier.size(20.dp))
                                    }
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(text = t("ifa_title"), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HeraOnSurface)
                                            Surface(color = HeraSecondary.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                                                Text(text = "Daily PM", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = HeraSecondary, modifier = Modifier.padding(horizontal = 4.dp))
                                            }
                                        }
                                        Text(text = t("ifa_sub"), fontSize = 11.sp, color = HeraOnSurface)
                                    }
                                }

                                Button(
                                    onClick = { viewModel.toggleIfaDose() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (uiState.ifaDoseTaken) HeraSecondaryContainer else HeraSecondary,
                                        contentColor = if (uiState.ifaDoseTaken) HeraOnSecondaryContainer else HeraOnSecondary
                                    ),
                                    shape = RoundedCornerShape(50),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = if (uiState.ifaDoseTaken) t("taken_done") else t("mark_taken"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // STAGE 2: ENHANCED INTERACTIVE PERIODS & HORMONAL HEALTH SECTION
        if (selectedStage == "periods") {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = HeraSurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header with phase status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = t("cycle_tracker_title"), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = HeraOnSurface)
                            Text(
                                text = "Cycle Day $currentCycleDay: ${if (currentCycleDay in 1..5) "Menstrual Phase" else if (currentCycleDay in 6..13) "Follicular Phase" else if (currentCycleDay in 14..16) "Ovulation Window" else "Luteal Phase"}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = HeraPrimary
                            )
                        }
                        Surface(color = HeraSecondaryContainer, shape = RoundedCornerShape(50)) {
                            Text(text = "Regular 28-day", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = HeraSecondary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }

                    // Cycle Phase Slider & Visual Stage Indicator
                    Surface(color = HeraSurfaceContainerLow, shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Day 1", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = HeraOnSurface)
                                Text(text = "Day 14 (Ovulation)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = HeraPrimary)
                                Text(text = "Day 28", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = HeraOnSurface)
                            }

                            Slider(
                                value = currentCycleDay.toFloat(),
                                onValueChange = { currentCycleDay = it.toInt() },
                                valueRange = 1f..28f,
                                steps = 27,
                                colors = SliderDefaults.colors(
                                    thumbColor = HeraPrimary,
                                    activeTrackColor = HeraPrimary,
                                    inactiveTrackColor = HeraSurfaceContainerHigh
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // 4 Phases Ribbon
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val phases = listOf(
                                    "Menstrual (1-5)" to (currentCycleDay in 1..5),
                                    "Follicular (6-13)" to (currentCycleDay in 6..13),
                                    "Ovulation (14-16)" to (currentCycleDay in 14..16),
                                    "Luteal (17-28)" to (currentCycleDay in 17..28)
                                )
                                phases.forEach { (name, active) ->
                                    Surface(
                                        color = if (active) HeraPrimary else HeraSurfaceContainerLowest,
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = name,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (active) HeraOnPrimary else HeraOnSurface,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Quick-Tap Symptom Logger
                    Text(text = t("symptom_logger_title"), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HeraOnSurface)

                    // Cramps
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = t("cramps_label"), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HeraOnSurface)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("None", "Mild", "Moderate", "Severe").forEach { level ->
                                val sel = selectedCramp == level
                                Surface(
                                    color = if (sel) HeraPrimary else HeraSurfaceContainerLow,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            selectedCramp = level
                                            viewModel.showToast("Cramp intensity logged: $level")
                                        }
                                ) {
                                    Text(
                                        text = level,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (sel) HeraOnPrimary else HeraOnSurface,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Mood & Energy
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = t("mood_label"), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HeraOnSurface)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Calm", "Fatigued", "Anxious", "Sensitive").forEach { mood ->
                                val sel = selectedMood == mood
                                Surface(
                                    color = if (sel) HeraTertiary else HeraSurfaceContainerLow,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            selectedMood = mood
                                            viewModel.showToast("Mood logged: $mood")
                                        }
                                ) {
                                    Text(
                                        text = mood,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (sel) HeraOnTertiary else HeraOnSurface,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Flow Intensity
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = t("flow_label"), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HeraOnSurface)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Spotting", "Light", "Medium", "Heavy").forEach { flow ->
                                val sel = selectedFlow == flow
                                Surface(
                                    color = if (sel) HeraSecondary else HeraSurfaceContainerLow,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            selectedFlow = flow
                                            viewModel.showToast("Flow level logged: $flow")
                                        }
                                ) {
                                    Text(
                                        text = flow,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (sel) HeraOnSecondary else HeraOnSurface,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Natural Cramp Relief Recommendations
                    Surface(
                        color = HeraSurfaceContainerLow,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(Icons.Default.Spa, contentDescription = null, tint = HeraSecondary, modifier = Modifier.size(20.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(text = t("cramp_relief_title"), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HeraOnSurface)
                                Text(
                                    text = t("cramp_relief_desc"),
                                    fontSize = 11.sp,
                                    color = HeraOnSurface,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // ASHA Worker Connect Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = HeraSurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(HeraSecondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.SupportAgent, contentDescription = null, tint = HeraOnSecondaryContainer, modifier = Modifier.size(22.dp))
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = t("asha_contact"), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = HeraOnSurface)
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(HeraSecondary))
                        }
                        Text(text = t("asha_role"), fontSize = 11.sp, color = HeraOnSurfaceVariant)
                    }
                }
                Button(
                    onClick = { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:104"))) },
                    colors = ButtonDefaults.buttonColors(containerColor = HeraSecondary),
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = t("connect_btn"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Hardware Action Dock with Voice Microphone Integration
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            color = HeraSurfaceContainerLowest,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        try {
                            isListeningSpeech = true
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                putExtra(RecognizerIntent.EXTRA_PROMPT, t("listening"))
                            }
                            speechLauncher.launch(intent)
                        } catch (e: Exception) {
                            // Fallback simulation
                            isListeningSpeech = true
                            viewModel.showToast(t("listening"))
                            val demoText = "Severe blurred vision and sharp epigastric pain since morning"
                            viewModel.setSymptomInput(demoText)
                            viewModel.setScreen(NavScreen.TRIAGE)
                            isListeningSpeech = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isListeningSpeech) HeraError else HeraPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("speak_symptoms_btn")
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isListeningSpeech) t("listening") else t("speak_symptoms"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                FilledTonalIconButton(
                    onClick = { viewModel.triggerGpsLock() },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("gps_share_btn")
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "GPS", tint = HeraSecondary)
                }

                FilledTonalIconButton(
                    onClick = { viewModel.showToast("Camera OCR initialized: Align lab blood report or prescription") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("ocr_scan_btn")
                ) {
                    Icon(Icons.Default.DocumentScanner, contentDescription = "Scan", tint = HeraTertiary)
                }
            }
        }
    }
}
