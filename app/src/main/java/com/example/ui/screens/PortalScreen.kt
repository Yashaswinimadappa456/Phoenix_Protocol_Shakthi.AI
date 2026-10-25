package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.model.UserRole
import com.example.ui.MainUiState
import com.example.ui.MainViewModel
import com.example.ui.components.NavScreen
import com.example.ui.theme.*

@Composable
fun PortalScreen(
    uiState: MainUiState,
    viewModel: MainViewModel
) {
    val isDoctor = uiState.authUser.role == UserRole.DOCTOR

    if (!isDoctor) {
        // STRICT RBAC GUARD
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .testTag("rbac_restricted_container"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(HeraPrimaryFixed),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Lock",
                    tint = HeraPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Restricted Clinician Access",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = HeraOnSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Doctor Portal and clinical queue tools are accessible ONLY with registered doctor credentials (doctor@heracare.org). Patients and ASHA Workers are strictly restricted.",
                fontSize = 12.sp,
                color = HeraOnSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    viewModel.authenticateUser(
                        role = UserRole.DOCTOR,
                        identifier = "doctor@heracare.org",
                        name = "Dr. Soundarya Ramesh",
                        abhaId = "NMC-KMC-48291"
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = HeraTertiary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(44.dp)
                    .testTag("login_as_doctor_button")
            ) {
                Icon(Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Authenticate as Doctor", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
        return
    }

    // AUTHENTICATED DOCTOR CLINICAL PORTAL
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(bottom = 80.dp)
            .testTag("portal_screen_container"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Provider Identity & Clinical Status Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = HeraSurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.size(52.dp)) {
                        AsyncImage(
                            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuDFWItI3uxOIDrcLAQHSRi3i1C8FVv4EIPrTGlUtJNiykAdRGZ931OnsuAqBLuYdxmKfsWohybWTi5SV7URcEgWzQQl3h9B1Bvh-EjSOdI4CJZmpdDecGvqCo9xACmeKdH-17e-Hk-q6wIBk18huB7dmB6ehLXtgPNiMgLc1zKZlvoBPqqT4O-eVZb48SYORDza_QDq1iNrYZ-CehLgA1Kn4EnyupQnsvJJTOsNbVy1csALr_-7HlxWmQ",
                            contentDescription = "Dr. Soundarya Ramesh",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(HeraSecondary)
                                .border(1.5.dp, Color.White, CircleShape)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "Dr. Soundarya Ramesh", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Surface(color = HeraSecondaryContainer.copy(alpha = 0.5f), shape = RoundedCornerShape(50)) {
                                Text(text = "NMC #KMC-48291", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = HeraSecondary, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                            }
                        }
                        Text(text = "MD (OB/GYN) • Senior Maternal-Fetal Consultant", fontSize = 11.sp, color = HeraOnSurfaceVariant)
                        Text(text = "Apollo Cradle & Shakthi.ai Network", fontSize = 10.sp, color = HeraTertiary, fontWeight = FontWeight.Medium)
                    }
                }

                // Duty Toggle
                Surface(
                    color = HeraSurfaceContainerLow,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (uiState.isDoctorOnDuty) HeraSecondary else Color.Gray))
                            Text(text = "Accepting Urgent Triage & Consults", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Switch(
                            checked = uiState.isDoctorOnDuty,
                            onCheckedChange = { viewModel.toggleDoctorDuty() },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = HeraSecondary)
                        )
                    }
                }

                // Quick Clinical Telemetry Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(color = HeraSurfaceContainer, shape = RoundedCornerShape(8.dp), modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Queue Load", fontSize = 9.sp, color = HeraOnSurfaceVariant)
                            Text(text = "3 Active", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Surface(color = HeraSurfaceContainer, shape = RoundedCornerShape(8.dp), modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "SOS Escalations", fontSize = 9.sp, color = HeraOnSurfaceVariant)
                            Text(text = "1 En Route", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HeraError)
                        }
                    }
                    Surface(color = HeraSurfaceContainer, shape = RoundedCornerShape(8.dp), modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "ABDM Sync", fontSize = 9.sp, color = HeraOnSurfaceVariant)
                            Text(text = "Secured", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HeraSecondary)
                        }
                    }
                }
            }
        }

        // Encrypted Tele-Consult Suite
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
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Videocam, contentDescription = null, tint = HeraPrimary, modifier = Modifier.size(18.dp))
                        Text(text = "Encrypted Tele-Consult Suite", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Surface(color = HeraSecondaryContainer.copy(alpha = 0.4f), shape = RoundedCornerShape(50)) {
                        Text(text = "WebRTC 256-Bit", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = HeraSecondary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }

                // Video Preview Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(HeraInverseSurface)
                ) {
                    if (uiState.isTeleConsultActive) {
                        AsyncImage(
                            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuBFHy0ryAECLWnZ0NZHv05ND-hiEG5_wufHMgo3d0gEgbg_AgUKybwFVEJ7APrktPTzBGTV5HvLsmA1SIYJqHL47_apbiDHM2-0aNE0N6Dt_E3FcuMocyRs8SkPJifiBOrk0SaAgTUu2agRuE4YYJACQLvjsrDrdCUoAhCPJapHqmmS22T3bMZwFWoZ2BkxsUfvFjDbLfpok4bvOMTmXlXWdU3ryDkrzv0PYxzl1SSajcI9uaIPqohuUg",
                            contentDescription = "Patient Video Feed",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.VideoCameraFront, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Waiting for consultation session start...", fontSize = 11.sp, color = Color.LightGray)
                        }
                    }

                    // Vital Telemetry Overlay
                    Surface(
                        color = HeraInverseSurface.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = HeraPrimaryFixed, modifier = Modifier.size(13.dp))
                            Text(text = "78 bpm", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(text = "•", color = Color.White)
                            Icon(Icons.Default.Speed, contentDescription = null, tint = HeraSecondaryFixed, modifier = Modifier.size(13.dp))
                            Text(text = "118/76 mmHg", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    // Doctor Self-PiP
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(48.dp, 60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.5.dp, HeraPrimary, RoundedCornerShape(8.dp))
                    ) {
                        AsyncImage(
                            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuDFWItI3uxOIDrcLAQHSRi3i1C8FVv4EIPrTGlUtJNiykAdRGZ931OnsuAqBLuYdxmKfsWohybWTi5SV7URcEgWzQQl3h9B1Bvh-EjSOdI4CJZmpdDecGvqCo9xACmeKdH-17e-Hk-q6wIBk18huB7dmB6ehLXtgPNiMgLc1zKZlvoBPqqT4O-eVZb48SYORDza_QDq1iNrYZ-CehLgA1Kn4EnyupQnsvJJTOsNbVy1csALr_-7HlxWmQ",
                            contentDescription = "Doctor PiP",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Bottom Patient Tag
                    Surface(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Ready: Kavya Reddy (Room #942)",
                            fontSize = 10.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Button(
                    onClick = { viewModel.toggleTeleConsult() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.isTeleConsultActive) HeraError else HeraSecondary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("teleconsult_action_btn")
                ) {
                    Icon(
                        imageVector = if (uiState.isTeleConsultActive) Icons.Default.CallEnd else Icons.Default.PhoneInTalk,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (uiState.isTeleConsultActive) "End Encrypted Tele-Consult" else "Join Encrypted Tele-Consultation",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Patient Triage Queue Header
        Text(text = "Patient Triage Queue", fontSize = 14.sp, fontWeight = FontWeight.Bold)

        // Urgent Card
        Surface(
            color = HeraErrorContainer.copy(alpha = 0.4f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(HeraError))
                        Text(text = "Lakshmi Sundaram, 28y", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Surface(color = HeraError, shape = RoundedCornerShape(4.dp)) {
                            Text(text = "URGENT", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 4.dp))
                        }
                    }
                    Text(text = "2m ago", fontSize = 10.sp, color = HeraOnSurfaceVariant)
                }
                Text(text = "Pre-Eclampsia Alert • BP 168/104 • 34w 2d", fontSize = 11.sp, color = HeraError, fontWeight = FontWeight.SemiBold)
                Surface(color = HeraSurfaceContainerLowest, shape = RoundedCornerShape(6.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "108 Ambulance dispatched (ETA: 8 mins)", fontSize = 10.sp)
                        Text(text = "Track", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = HeraError)
                    }
                }
            }
        }

        // Active Consult Card
        Surface(
            color = HeraSurfaceContainerLowest,
            shape = RoundedCornerShape(12.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(HeraPrimary)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(HeraPrimary))
                        Text(text = "Kavya Reddy, 24y", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Surface(color = HeraPrimaryContainer, shape = RoundedCornerShape(4.dp)) {
                            Text(text = "ACTIVE CONSULT", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 4.dp))
                        }
                    }
                    Text(text = "In Waiting Room", fontSize = 10.sp, color = HeraSecondary, fontWeight = FontWeight.Bold)
                }
                Text(text = "PCOS & Severe Menorrhagia • ABHA: 91-8472-9102-1849", fontSize = 11.sp, color = HeraOnSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Cycle Day 38", fontSize = 10.sp, color = HeraOnSurfaceVariant)
                    Text(text = "• Hb 10.4 g/dL", fontSize = 10.sp, color = HeraOnSurfaceVariant)
                    Text(text = "• ABDM Consent Granted", fontSize = 10.sp, color = HeraSecondary, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Clinical Dossier for Kavya Reddy
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = HeraSurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.FolderShared, contentDescription = null, tint = HeraPrimary, modifier = Modifier.size(18.dp))
                        Text(text = "Clinical Dossier: Kavya Reddy", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(text = "ABHA #9102-1849", fontSize = 10.sp, color = HeraOnSurfaceVariant)
                }

                // Quad-split Vitals Matrix
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(color = HeraSurfaceContainerLow, shape = RoundedCornerShape(10.dp), modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(text = "Blood Pressure", fontSize = 9.sp, color = HeraOnSurfaceVariant)
                            Text(text = "118 / 76 mmHg", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Optimal Normotensive", fontSize = 9.sp, color = HeraSecondary)
                        }
                    }
                    Surface(color = HeraSurfaceContainerLow, shape = RoundedCornerShape(10.dp), modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(text = "Resting Heart Rate", fontSize = 9.sp, color = HeraOnSurfaceVariant)
                            Text(text = "78 bpm", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Regular Rhythm", fontSize = 9.sp, color = HeraSecondary)
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(color = HeraSurfaceContainerLow, shape = RoundedCornerShape(10.dp), modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(text = "Fasting Glucose", fontSize = 9.sp, color = HeraOnSurfaceVariant)
                            Text(text = "104 mg/dL", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Borderline Impaired", fontSize = 9.sp, color = HeraPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Surface(color = HeraSurfaceContainerLow, shape = RoundedCornerShape(10.dp), modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(text = "Current Pain VAS", fontSize = 9.sp, color = HeraOnSurfaceVariant)
                            Text(text = "6 / 10", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Moderate-Severe Cramping", fontSize = 9.sp, color = HeraPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Pelvic USG & Diagnostics
                Surface(color = HeraSurfaceContainerLow, shape = RoundedCornerShape(10.dp)) {
                    Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(HeraInverseSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MedicalInformation, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(24.dp))
                        }
                        Column {
                            Text(text = "Polycystic Ovarian Morphology", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Bilateral enlarged ovaries with >12 peripheral follicles ('necklace sign'). Endometrial thickness: 11.2 mm.", fontSize = 10.sp, color = HeraOnSurfaceVariant)
                        }
                    }
                }

                // Doctor Impression
                Surface(color = HeraSurfaceContainer, shape = RoundedCornerShape(10.dp)) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Consultation Impression & Orders", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Recommend cycle initiation with cyclic Progestin therapy (Medroxyprogesterone 10mg OD x 10 days). Initiate Myo-Inositol + D-Chiro-Inositol supplementation. Scheduled lipid profile & OGTT follow-up in 6 weeks.",
                            fontSize = 10.sp,
                            color = HeraOnSurface,
                            lineHeight = 15.sp
                        )
                    }
                }

                // Primary Provider Actions
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { viewModel.signPrescription() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (uiState.isPrescriptionSigned) HeraSecondary else HeraPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("sign_prescription_btn")
                    ) {
                        Icon(Icons.Default.Draw, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (uiState.isPrescriptionSigned) "E-Prescription Signed ✓" else "Approve & Sign E-Prescription",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(color = Color.Black.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                            Text(text = "SHA-256", fontSize = 9.sp, color = Color.White, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                        }
                    }

                    // OPD Pass Card
                    Surface(
                        color = HeraSurfaceContainer,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(HeraSurfaceContainerLowest),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.QrCode2, contentDescription = null, modifier = Modifier.size(26.dp))
                                }
                                Column {
                                    Text(text = "Verifiable ABDM OPD Pass", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "Token #SHAKTHI-BLR-2024-8491", fontSize = 10.sp, color = HeraOnSurfaceVariant)
                                }
                            }
                            Button(
                                onClick = { viewModel.setOpdPassModal(true) },
                                colors = ButtonDefaults.buttonColors(containerColor = HeraTertiaryContainer),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier
                                    .height(32.dp)
                                    .testTag("download_pass_btn")
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Download Pass", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Verifiable OPD Pass Dialog
    if (uiState.showOpdPassModal) {
        Dialog(onDismissRequest = { viewModel.setOpdPassModal(false) }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = HeraSurfaceContainerLowest),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "SHAKTHI.AI OPD PASS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HeraPrimary)
                        IconButton(onClick = { viewModel.setOpdPassModal(false) }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(18.dp))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(HeraSurfaceContainerLow)
                            .border(1.dp, HeraOutlineVariant, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.QrCode2, contentDescription = "QR Code", modifier = Modifier.size(90.dp), tint = HeraOnSurface)
                    }

                    Text(text = "Kavya Reddy (24y)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = "ABHA: 91-8472-9102-1849", fontSize = 11.sp, color = HeraOnSurfaceVariant)
                    Text(text = "Clinician: Dr. Soundarya Ramesh • NMC #KMC-48291", fontSize = 10.sp, color = HeraSecondary, fontWeight = FontWeight.SemiBold)
                    Text(text = "Consultation Token #SHAKTHI-BLR-2024-8491", fontSize = 10.sp, color = HeraOutline)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.showToast("Pass PDF printed/shared successfully")
                                viewModel.setOpdPassModal(false)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = HeraTertiary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(40.dp)
                        ) {
                            Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Print Slip", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                viewModel.showToast("Verifiable pass saved to device")
                                viewModel.setOpdPassModal(false)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = HeraSecondary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(40.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Save Pass", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
