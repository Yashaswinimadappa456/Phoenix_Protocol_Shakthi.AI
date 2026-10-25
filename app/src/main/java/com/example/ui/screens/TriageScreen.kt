package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.TriagePatientCase
import com.example.model.TriageSeverity
import com.example.ui.MainUiState
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun TriageScreen(
    uiState: MainUiState,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    var rxDetailsOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(bottom = 80.dp)
            .testTag("triage_screen_container"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top Operational Context & Offline Status Badge
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = HeraSurfaceContainerLow)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Emergency,
                            contentDescription = null,
                            tint = HeraPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Emergency & Triage Canvas",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = HeraOnSurface
                        )
                    }

                    Surface(
                        color = HeraSecondaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(HeraSecondary))
                            Text(
                                text = "Offline Mesh Ready",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = HeraOnSecondaryContainer
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Priority sorting: South Zone (TN • KA • TS)",
                        fontSize = 11.sp,
                        color = HeraOnSurfaceVariant
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = null, tint = HeraTertiary, modifier = Modifier.size(13.dp))
                        Text(text = "Live Sync Active", fontSize = 11.sp, color = HeraTertiary, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // Live AI Triage Assistant with Gemini 2.5 Flash
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = HeraSurfaceContainerLowest),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(HeraPrimaryFixed)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(HeraPrimaryFixed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = HeraPrimary, modifier = Modifier.size(16.dp))
                        }
                        Text(text = "Live AI Clinical Triage", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Surface(color = HeraPrimaryFixed.copy(alpha = 0.5f), shape = RoundedCornerShape(50)) {
                        Text(
                            text = "Gemini 2.5 Flash",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = HeraPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = uiState.symptomPromptInput,
                        onValueChange = { viewModel.setSymptomInput(it) },
                        placeholder = { Text("Describe symptoms or speech transcript...", fontSize = 11.sp, color = HeraOnSurfaceVariant) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = HeraOnSurface,
                            unfocusedTextColor = HeraOnSurface,
                            focusedContainerColor = HeraSurfaceContainerLowest,
                            unfocusedContainerColor = HeraSurfaceContainerLowest,
                            focusedBorderColor = HeraPrimary,
                            unfocusedBorderColor = HeraOutlineVariant
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("triage_symptom_input")
                    )

                    Button(
                        onClick = { viewModel.runGeminiTriage(uiState.symptomPromptInput) },
                        enabled = !uiState.isAiEvaluating,
                        colors = ButtonDefaults.buttonColors(containerColor = HeraPrimary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier.height(48.dp).testTag("evaluate_symptoms_btn")
                    ) {
                        if (uiState.isAiEvaluating) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = HeraOnPrimary, strokeWidth = 2.dp)
                        } else {
                            Text(text = "Triage", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Render Active Gemini Assessment Result
                AnimatedVisibility(visible = uiState.activeTriageAssessment != null) {
                    uiState.activeTriageAssessment?.let { assessment ->
                        val isRed = assessment.severity == "RED"
                        val isYellow = assessment.severity == "YELLOW"

                        Surface(
                            color = if (isRed) HeraErrorContainer.copy(alpha = 0.6f) else if (isYellow) HeraWarningContainer.copy(alpha = 0.6f) else HeraSuccessContainer.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = if (isRed) HeraError else if (isYellow) HeraWarning else HeraSuccess,
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text(
                                            text = if (isRed) "Level 1 RED (Emergency)" else if (isYellow) "Level 2 YELLOW (Consult)" else "Level 3 GREEN (Safe Home)",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = assessment.recommended_doctor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HeraOnSurface
                                    )
                                }

                                Text(
                                    text = assessment.simple_summary,
                                    fontSize = 12.sp,
                                    color = HeraOnSurface,
                                    lineHeight = 16.sp
                                )

                                Surface(
                                    color = HeraSurfaceContainerLowest.copy(alpha = 0.9f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(text = "Home Care Steps:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HeraOnSurface)
                                        assessment.home_care_steps.forEach { step ->
                                            Text(text = "• $step", fontSize = 10.sp, color = HeraOnSurfaceVariant)
                                        }
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.LocalHospital, contentDescription = null, tint = HeraPrimary, modifier = Modifier.size(14.dp))
                                    Text(
                                        text = "Action: ${assessment.hospital_action}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HeraPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Filter Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val filters = listOf(
                "all" to "All Triage (3)",
                "red" to "Urgent Level 1",
                "yellow" to "Level 2 Consult",
                "green" to "Level 3 Routine"
            )

            filters.forEach { (key, label) ->
                val isSelected = uiState.triageFilter == key
                Surface(
                    color = if (isSelected) HeraPrimary else HeraSurfaceContainer,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.clickable { viewModel.setTriageFilter(key) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (key == "red") Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(HeraError))
                        if (key == "yellow") Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(HeraWarning))
                        if (key == "green") Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(HeraSecondary))

                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) HeraOnPrimary else HeraOnSurface
                        )
                    }
                }
            }
        }

        // Render Cases
        val filteredCases = uiState.triageCases.filter {
            uiState.triageFilter == "all" ||
            (uiState.triageFilter == "red" && it.severity == TriageSeverity.RED) ||
            (uiState.triageFilter == "yellow" && it.severity == TriageSeverity.YELLOW) ||
            (uiState.triageFilter == "green" && it.severity == TriageSeverity.GREEN)
        }

        filteredCases.forEach { triageCase ->
            when (triageCase.severity) {
                TriageSeverity.RED -> RedTriageCard(triageCase, onAmbulance = {
                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:108")))
                }, onHospital = {
                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:0802221111")))
                }, onUssd = {
                    val encoded = Uri.encode(triageCase.ussdCode)
                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$encoded")))
                    viewModel.showToast("Dialing low-bandwidth offline USSD ${triageCase.ussdCode}")
                })

                TriageSeverity.YELLOW -> YellowTriageCard(
                    triageCase = triageCase,
                    rxOpen = rxDetailsOpen,
                    onToggleRx = { rxDetailsOpen = !rxDetailsOpen },
                    onBookConsult = {
                        viewModel.showToast("Tele-Consultation booked with Gynecologist (Today 4:30 PM)")
                    }
                )

                TriageSeverity.GREEN -> GreenTriageCard(
                    triageCase = triageCase,
                    onLatchGuide = { viewModel.showToast("Loading bilingual Tamil/Kannada Latch video guide...") },
                    onFollowUp = { viewModel.showToast("ASHA post-natal home visit slotted for 7 days") }
                )
            }
        }

        // National Women Helpline & JSSK Scheme Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = HeraSurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                                .background(HeraPrimaryFixed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.SupportAgent, contentDescription = null, tint = HeraPrimary, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text(text = "National Women Helpline", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Free 24x7 Emergency Assistance", fontSize = 10.sp, color = HeraOnSurfaceVariant)
                        }
                    }
                    Button(
                        onClick = { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:181"))) },
                        colors = ButtonDefaults.buttonColors(containerColor = HeraPrimary),
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Dial 181", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Surface(
                    color = HeraSurfaceContainerLow,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = HeraSecondary, modifier = Modifier.size(16.dp))
                            Column {
                                Text(text = "Janani Shishu Suraksha Karyakram (JSSK)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Zero-expense delivery & hospital transit", fontSize = 10.sp, color = HeraOnSurfaceVariant)
                            }
                        }
                        Surface(color = HeraSecondaryContainer, shape = RoundedCornerShape(4.dp)) {
                            Text(text = "100% Free", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = HeraSecondary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RedTriageCard(
    triageCase: TriagePatientCase,
    onAmbulance: () -> Unit,
    onHospital: () -> Unit,
    onUssd: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HeraSurfaceContainerLowest),
        border = androidx.compose.foundation.BorderStroke(2.dp, HeraError),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // High Alert Banner
            Surface(
                color = HeraError,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Emergency, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Text(
                        text = "HIGH ALERT: IMMEDIATE HOSPITAL TRANSFER (Pre-Eclampsia / Hemorrhage)",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.size(44.dp)) {
                        AsyncImage(
                            model = triageCase.avatarUrl,
                            contentDescription = triageCase.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(HeraError),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "!", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = triageCase.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = HeraOnSurface)
                            Text(text = "${triageCase.age}y", fontSize = 11.sp, color = HeraOnSurfaceVariant)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = HeraPrimary, modifier = Modifier.size(13.dp))
                            Text(text = "${triageCase.location} • ${triageCase.gestationalAgeOrStage}", fontSize = 10.sp, color = HeraOnSurfaceVariant)
                        }
                    }
                }

                Surface(color = HeraError, shape = RoundedCornerShape(50)) {
                    Text(
                        text = "Level 1 Red",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Surface(
                color = HeraErrorContainer.copy(alpha = 0.25f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = HeraError, modifier = Modifier.size(16.dp))
                            Text(text = triageCase.title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HeraError)
                        }
                        Surface(color = HeraError, shape = RoundedCornerShape(4.dp)) {
                            Text(text = triageCase.vitals, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        triageCase.symptoms.forEach { symptom ->
                            Surface(
                                color = HeraSurfaceContainerLowest,
                                shape = RoundedCornerShape(6.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, HeraError.copy(alpha = 0.3f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.VisibilityOff, contentDescription = null, tint = HeraError, modifier = Modifier.size(14.dp))
                                    Text(text = symptom, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = HeraOnSurface)
                                }
                            }
                        }
                    }

                    Text(text = triageCase.summary, fontSize = 11.sp, color = HeraOnSurface, lineHeight = 15.sp)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAmbulance,
                    colors = ButtonDefaults.buttonColors(containerColor = HeraError),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("red_ambulance_btn")
                ) {
                    Icon(Icons.Default.Emergency, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "108 Ambulance", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onHospital,
                    colors = ButtonDefaults.buttonColors(containerColor = HeraSurfaceContainerLowest, contentColor = HeraPrimary),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, HeraPrimary),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Nearest OB-ICU", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Offline USSD drawer
            Surface(
                color = HeraSurfaceContainerHighest,
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.SignalCellularConnectedNoInternet0Bar, contentDescription = null, tint = HeraPrimary, modifier = Modifier.size(18.dp))
                        Column {
                            Text(text = "No Internet? Dial Offline USSD", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = HeraOnSurface)
                            Text(text = triageCase.ussdCode, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HeraPrimary)
                        }
                    }
                    Button(
                        onClick = onUssd,
                        colors = ButtonDefaults.buttonColors(containerColor = HeraPrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(text = "Trigger", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun YellowTriageCard(
    triageCase: TriagePatientCase,
    rxOpen: Boolean,
    onToggleRx: () -> Unit,
    onBookConsult: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HeraSurfaceContainerLowest),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, HeraWarning),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Moderate Urgency Banner
            Surface(
                color = HeraWarningContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = HeraWarning, modifier = Modifier.size(16.dp))
                    Text(
                        text = "LEVEL 2: CONSULTATION REQUIRED WITHIN 24 HOURS (PCOS / Monitor)",
                        color = HeraWarning,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AsyncImage(
                        model = triageCase.avatarUrl,
                        contentDescription = triageCase.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                    )
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = triageCase.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = HeraOnSurface)
                            Text(text = "${triageCase.age}y", fontSize = 11.sp, color = HeraOnSurfaceVariant)
                        }
                        Text(text = "${triageCase.location} • ${triageCase.gestationalAgeOrStage}", fontSize = 10.sp, color = HeraOnSurfaceVariant)
                    }
                }
                Surface(color = HeraWarningContainer, shape = RoundedCornerShape(50)) {
                    Text(text = "Level 2 Consult", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = HeraWarning, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                }
            }

            Surface(color = HeraSurfaceContainerLow, shape = RoundedCornerShape(10.dp)) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = triageCase.title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HeraOnSurface)
                    Text(text = triageCase.summary, fontSize = 10.sp, color = HeraOnSurface, lineHeight = 15.sp)
                }
            }

            Button(
                onClick = onBookConsult,
                colors = ButtonDefaults.buttonColors(containerColor = HeraTertiary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Book Tele-Consult within 24h", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Surface(
                color = HeraSurfaceContainer,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleRx() }
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "View Antispasmodic & Warm Compress Plan", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HeraOnSurface)
                    Icon(
                        imageVector = if (rxOpen) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = HeraOnSurface
                    )
                }
            }

            AnimatedVisibility(visible = rxOpen) {
                Surface(color = HeraSurfaceContainerLowest, shape = RoundedCornerShape(8.dp)) {
                    Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        triageCase.collapsibleDetails.forEach { detail ->
                            Text(text = detail, fontSize = 10.sp, color = HeraOnSurface, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GreenTriageCard(
    triageCase: TriagePatientCase,
    onLatchGuide: () -> Unit,
    onFollowUp: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HeraSurfaceContainerLowest),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, HeraSecondary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Safe Routine Banner
            Surface(
                color = HeraSecondaryContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = HeraSecondary, modifier = Modifier.size(16.dp))
                    Text(
                        text = "LEVEL 3: SAFE ROUTINE HOME WELLNESS & POSTPARTUM CARE",
                        color = HeraSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AsyncImage(
                        model = triageCase.avatarUrl,
                        contentDescription = triageCase.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                    )
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = triageCase.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = HeraOnSurface)
                            Text(text = "${triageCase.age}y", fontSize = 11.sp, color = HeraOnSurfaceVariant)
                        }
                        Text(text = "${triageCase.location} • ${triageCase.gestationalAgeOrStage}", fontSize = 10.sp, color = HeraOnSurfaceVariant)
                    }
                }
                Surface(color = HeraSecondaryContainer, shape = RoundedCornerShape(50)) {
                    Text(text = "Level 3 Home", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = HeraSecondary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                }
            }

            Surface(color = HeraSurfaceContainerLow, shape = RoundedCornerShape(10.dp)) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = triageCase.title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HeraSecondary)
                    Text(text = triageCase.summary, fontSize = 10.sp, color = HeraOnSurface, lineHeight = 15.sp)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onLatchGuide,
                    colors = ButtonDefaults.buttonColors(containerColor = HeraSecondary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                ) {
                    Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Latch Video Guide", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onFollowUp,
                    colors = ButtonDefaults.buttonColors(containerColor = HeraSurfaceContainer, contentColor = HeraOnSurface),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                ) {
                    Icon(Icons.Default.EventAvailable, contentDescription = null, tint = HeraSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Check in 7 Days", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
