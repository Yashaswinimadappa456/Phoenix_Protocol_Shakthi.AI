package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.TranslationData
import com.example.ui.MainViewModel
import com.example.ui.components.BottomNav
import com.example.ui.components.NavScreen
import com.example.ui.components.TopHeader
import com.example.ui.screens.*
import com.example.ui.theme.HeraInverseOnSurface
import com.example.ui.theme.HeraInverseSurface
import com.example.ui.theme.HeraSecondary
import com.example.ui.theme.MyApplicationTheme
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            // Firebase initialized automatically via ContentProvider
        }

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                // Handle system back navigation
                if (uiState.currentScreen != NavScreen.HOME) {
                    BackHandler {
                        viewModel.setScreen(NavScreen.HOME)
                    }
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("main_scaffold"),
                    topBar = {
                        TopHeader(
                            currentTitle = when (uiState.currentScreen) {
                                NavScreen.HOME -> TranslationData.get(uiState.language, "home")
                                NavScreen.DASHBOARD -> TranslationData.get(uiState.language, "dashboard")
                                NavScreen.TRIAGE -> TranslationData.get(uiState.language, "triage")
                                NavScreen.CHAT -> TranslationData.get(uiState.language, "ask_shakthi")
                                NavScreen.PORTAL -> TranslationData.get(uiState.language, "portal")
                            },
                            authUser = uiState.authUser,
                            onAvatarClick = {
                                viewModel.setScreen(NavScreen.HOME)
                            }
                        )
                    },
                    bottomBar = {
                        BottomNav(
                            currentScreen = uiState.currentScreen,
                            language = uiState.language,
                            onScreenSelected = { screen ->
                                viewModel.setScreen(screen)
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (uiState.currentScreen) {
                            NavScreen.HOME -> HomeScreen(uiState = uiState, viewModel = viewModel)
                            NavScreen.DASHBOARD -> DashboardScreen(uiState = uiState, viewModel = viewModel)
                            NavScreen.TRIAGE -> TriageScreen(uiState = uiState, viewModel = viewModel)
                            NavScreen.CHAT -> ChatScreen(uiState = uiState, viewModel = viewModel)
                            NavScreen.PORTAL -> PortalScreen(uiState = uiState, viewModel = viewModel)
                        }

                        // Floating Feedback Toast
                        AnimatedVisibility(
                            visible = uiState.toastMessage != null,
                            enter = fadeIn(),
                            exit = fadeOut(),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp, start = 20.dp, end = 20.dp)
                        ) {
                            uiState.toastMessage?.let { msg ->
                                Surface(
                                    color = HeraInverseSurface,
                                    shape = RoundedCornerShape(12.dp),
                                    shadowElevation = 6.dp,
                                    modifier = Modifier.testTag("feedback_toast")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = HeraSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = msg,
                                            color = HeraInverseOnSurface,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                LaunchedEffect(msg) {
                                    delay(3000)
                                    viewModel.clearToast()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
