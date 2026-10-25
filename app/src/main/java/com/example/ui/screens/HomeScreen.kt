package com.example.ui.screens

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppLanguage
import com.example.model.UserRole
import com.example.ui.MainUiState
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    uiState: MainUiState,
    viewModel: MainViewModel
) {
    var mobileInput by remember { mutableStateOf("9845211094") }
    var abhaInput by remember { mutableStateOf("91-4428-8921-1049") }
    var otpDeliveryChannel by remember { mutableStateOf("whatsapp") }
    var ashaIdInput by remember { mutableStateOf("MH-PUN-ASHA-0491") }
    var wardInput by remember { mutableStateOf("Village PHC Ward 12") }
    var doctorRegInput by remember { mutableStateOf("doctor@heracare.org") }
    var doctorPinInput by remember { mutableStateOf("123456") }
    var isSimulatingOtp by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
            .testTag("home_screen_container")
    ) {
        // Multi-lingual Language Selector Strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                color = HeraSurfaceContainer,
                shape = RoundedCornerShape(50),
                modifier = Modifier.height(32.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
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
                        text = "भाषा:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = HeraOnSurface
                    )
                }
            }

            AppLanguage.values().forEach { lang ->
                val isSelected = uiState.language == lang
                Surface(
                    color = if (isSelected) HeraPrimary else HeraSurfaceContainer,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .height(32.dp)
                        .clickable { viewModel.setLanguage(lang) }
                        .testTag("lang_${lang.code}")
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = lang.nativeName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) HeraOnPrimary else HeraOnSurfaceVariant
                        )
                    }
                }
            }
        }

        // Hero Card with Maternal & Empathetic Framing
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = HeraSurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.maternal_hero),
                        contentDescription = "Empowering healthcare for Indian women",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Scrim gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        HeraInverseSurface.copy(alpha = 0.4f),
                                        HeraInverseSurface.copy(alpha = 0.9f)
                                    )
                                )
                            )
                    )

                    // Ayushman Bharat Ready pill
                    Surface(
                        color = HeraSurfaceContainerLowest.copy(alpha = 0.92f),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .padding(10.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(HeraSecondary)
                            )
                            Text(
                                text = "Ayushman Bharat Ready",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = HeraOnSurface
                            )
                        }
                    }

                    // Bottom maternal text
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = HeraPrimaryFixed,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "CARING FOR EVERY MILESTONE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = HeraPrimaryFixed,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Text(
                            text = "Culturally attuned, dignified care for Indian women across every milestone",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = HeraOnPrimaryContainer,
                            lineHeight = 20.sp
                        )
                    }
                }

                // Quick Trust Metric Ribbon
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(HeraSurfaceContainerLow)
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "2.4M+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = HeraPrimary)
                        Text(text = "Women Cared", fontSize = 11.sp, color = HeraOnSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "14,200", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = HeraSecondary)
                        Text(text = "ASHAs Active", fontSize = 11.sp, color = HeraOnSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "100%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = HeraTertiary)
                        Text(text = "Encrypted & Safe", fontSize = 11.sp, color = HeraOnSurfaceVariant)
                    }
                }
            }
        }

        // Role-Based Interactive Authentication Container
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = HeraSurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Segmented Role Switcher
                Surface(
                    color = HeraSurfaceContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        UserRole.values().forEach { role ->
                            val isSelected = uiState.activeRoleTab == role
                            Surface(
                                color = if (isSelected) HeraSurfaceContainerLowest else Color.Transparent,
                                shape = RoundedCornerShape(8.dp),
                                shadowElevation = if (isSelected) 1.dp else 0.dp,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setRoleTab(role) }
                                    .testTag("tab_${role.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = role.emoji, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = role.label,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) when (role) {
                                            UserRole.PATIENT -> HeraPrimary
                                            UserRole.ASHA -> HeraSecondary
                                            UserRole.DOCTOR -> HeraTertiary
                                        } else HeraOnSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // TAB 1: PATIENT LOGIN
                AnimatedVisibility(visible = uiState.activeRoleTab == UserRole.PATIENT) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Surface(
                            color = HeraSurfaceContainerLow,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = HeraPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = "Seamless ABHA-Linked Entry",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HeraOnSurface
                                    )
                                    Text(
                                        text = "Sign in securely using your 10-digit mobile number or 14-digit ABHA Health Card.",
                                        fontSize = 11.sp,
                                        color = HeraOnSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Mobile Number Input
                        Column {
                            Text(
                                text = "Mobile Number / मोबाइल नंबर",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = HeraOnSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = mobileInput,
                                onValueChange = { if (it.length <= 10) mobileInput = it },
                                placeholder = { Text("Enter 10-digit number", fontSize = 13.sp) },
                                leadingIcon = {
                                    Text(
                                        text = "🇮🇳 +91",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                },
                                trailingIcon = {
                                    if (mobileInput.length == 10) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Valid",
                                            tint = HeraSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("patient_mobile_input")
                            )
                        }

                        // ABHA ID Input
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ABHA ID / आभा संख्या (Optional)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = HeraOnSurfaceVariant
                                )
                                Text(
                                    text = "Find my ABHA",
                                    fontSize = 11.sp,
                                    color = HeraTertiary,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.clickable {
                                        viewModel.showToast("Opening National Health Authority ABHA portal")
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = abhaInput,
                                onValueChange = { abhaInput = it },
                                placeholder = { Text("XX-XXXX-XXXX-XXXX or name@abdm", fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Badge,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("patient_abha_input")
                            )
                        }

                        // OTP Channel Selectors
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Deliver OTP via:", fontSize = 12.sp, color = HeraOnSurfaceVariant)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    color = if (otpDeliveryChannel == "whatsapp") HeraSecondaryContainer.copy(alpha = 0.5f) else HeraSurfaceContainer,
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier.clickable { otpDeliveryChannel = "whatsapp" }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = otpDeliveryChannel == "whatsapp",
                                            onClick = { otpDeliveryChannel = "whatsapp" },
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HeraSecondary)
                                    }
                                }

                                Surface(
                                    color = if (otpDeliveryChannel == "sms") HeraPrimaryFixed.copy(alpha = 0.5f) else HeraSurfaceContainer,
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier.clickable { otpDeliveryChannel = "sms" }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = otpDeliveryChannel == "sms",
                                            onClick = { otpDeliveryChannel = "sms" },
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "SMS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Primary Action Button
                        Button(
                            onClick = {
                                isSimulatingOtp = true
                                viewModel.authenticateUser(
                                    role = UserRole.PATIENT,
                                    identifier = mobileInput,
                                    name = "Priya Sharma",
                                    abhaId = abhaInput
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = HeraPrimaryContainer),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("patient_login_btn")
                        ) {
                            Icon(imageVector = Icons.Default.SendToMobile, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Get OTP via WhatsApp / SMS",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Google Sign-In with Firebase Auth Button
                        val coroutineScope = rememberCoroutineScope()
                        val context = androidx.compose.ui.platform.LocalContext.current
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    val signedInUser = com.example.service.FirebaseFirestoreService.signInWithGoogle(context)
                                    if (signedInUser != null) {
                                        viewModel.authenticateUser(
                                            role = signedInUser.role,
                                            identifier = signedInUser.identifier,
                                            name = signedInUser.name,
                                            abhaId = signedInUser.abhaId
                                        )
                                        viewModel.showToast("Signed in with Google via Firebase Auth")
                                    } else {
                                        // Demo seamless login fallback
                                        viewModel.authenticateUser(
                                            role = UserRole.PATIENT,
                                            identifier = "priya.sharma@gmail.com",
                                            name = "Priya Sharma",
                                            abhaId = "91-4428-8921-1049"
                                        )
                                        viewModel.showToast("Signed in with Google Account: Priya Sharma")
                                    }
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("google_signin_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Google",
                                tint = HeraPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sign in with Google (Firebase)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = HeraOnSurface
                            )
                        }

                        // Voice Assisted Login Prompt
                        Surface(
                            color = HeraTertiary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(HeraTertiary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Mic,
                                            contentDescription = "Voice",
                                            tint = HeraOnTertiary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "बोलकर सहायता लें (Voice Assist)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = HeraTertiary
                                        )
                                        Text(
                                            text = "Tap to speak your mobile number in your dialect",
                                            fontSize = 10.sp,
                                            color = HeraOnSurfaceVariant
                                        )
                                    }
                                }
                                Button(
                                    onClick = {
                                        mobileInput = "9845211094"
                                        viewModel.showToast("Voice recognized: 98452 11094")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = HeraTertiary),
                                    shape = RoundedCornerShape(50),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text(text = "Start", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // TAB 2: ASHA FIELD WORKER LOGIN
                AnimatedVisibility(visible = uiState.activeRoleTab == UserRole.ASHA) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Surface(
                            color = HeraSecondaryFixed.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Diversity1,
                                    contentDescription = null,
                                    tint = HeraSecondary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "Community Care Portal",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = HeraOnSurface
                                        )
                                        Surface(
                                            color = HeraSecondary,
                                            shape = RoundedCornerShape(50)
                                        ) {
                                            Text(
                                                text = "OFFLINE READY",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = HeraOnSecondary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Verified access for ASHAs, ANMs, and Gram Panchayat Sevikas.",
                                        fontSize = 11.sp,
                                        color = HeraOnSurfaceVariant
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = ashaIdInput,
                            onValueChange = { ashaIdInput = it },
                            label = { Text("Government ASHA ID / ANM Code", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = wardInput,
                            onValueChange = { wardInput = it },
                            label = { Text("Sub-Center / Village Ward", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.HomeWork, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                viewModel.authenticateUser(
                                    role = UserRole.ASHA,
                                    identifier = ashaIdInput,
                                    name = "Sunita Devi (ASHA)",
                                    abhaId = "ASHA-BLR-0491"
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = HeraSecondary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("asha_login_btn")
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Authenticate & Load Ward Sync", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Surface(
                            color = HeraSurfaceContainerLow,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.WifiOff, contentDescription = null, tint = HeraSecondary, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "No internet required for house-to-house maternal survey. Syncs automatically later.",
                                    fontSize = 10.sp,
                                    color = HeraOnSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // TAB 3: DOCTOR / CLINICIAN LOGIN
                AnimatedVisibility(visible = uiState.activeRoleTab == UserRole.DOCTOR) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Surface(
                            color = HeraTertiaryFixed.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MedicalServices,
                                    contentDescription = null,
                                    tint = HeraTertiary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Column {
                                    Text(
                                        text = "Registered Clinician Gateway",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HeraOnSurface
                                    )
                                    Text(
                                        text = "NMC-authorized tele-consultation and maternal electronic health logs.",
                                        fontSize = 11.sp,
                                        color = HeraOnSurfaceVariant
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = doctorRegInput,
                            onValueChange = { doctorRegInput = it },
                            label = { Text("Clinician Email / NMC Reg No.", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.EditNote, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("doctor_reg_input")
                        )

                        OutlinedTextField(
                            value = doctorPinInput,
                            onValueChange = { doctorPinInput = it },
                            label = { Text("Doctor PIN or Biometric Key", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.Fingerprint, contentDescription = null) },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("doctor_pin_input")
                        )

                        Button(
                            onClick = {
                                viewModel.authenticateUser(
                                    role = UserRole.DOCTOR,
                                    identifier = doctorRegInput,
                                    name = "Dr. Soundarya Ramesh",
                                    abhaId = "NMC-KMC-48291"
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = HeraTertiary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("doctor_login_btn")
                        ) {
                            Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Doctor Verified Sign In", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // ABDM & Security Compliance Footer Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = HeraSurfaceContainerLow)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(HeraSecondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            tint = HeraOnSecondaryContainer,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "ABDM M2 Certified Platform",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = HeraOnSurface
                            )
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = HeraSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "National Health Authority (NHA) & Ayushman Bharat Digital Mission verified.",
                            fontSize = 11.sp,
                            color = HeraOnSurfaceVariant
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = HeraSurfaceContainerLowest,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = HeraPrimary, modifier = Modifier.size(16.dp))
                            Text(text = "ISO 27001 Data Privacy", fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Surface(
                        color = HeraSurfaceContainerLowest,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = HeraSecondary, modifier = Modifier.size(16.dp))
                            Text(text = "100% Consent-Protected", fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Text(
                    text = "Your records are never shared without explicit OTP authorization. Guided by NHA Health Data Management Policy.",
                    fontSize = 10.sp,
                    color = HeraOutline,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
