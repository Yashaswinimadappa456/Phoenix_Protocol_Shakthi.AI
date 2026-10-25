package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.AuthUser
import com.example.model.UserRole
import com.example.ui.theme.*

@Composable
fun TopHeader(
    currentTitle: String,
    authUser: AuthUser,
    onAvatarClick: () -> Unit
) {
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("top_header_surface"),
        color = HeraSurface.copy(alpha = 0.95f),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Identity & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Circular icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(HeraSurfaceContainerLowest)
                        .border(1.dp, HeraOutlineVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.heracare_icon),
                        contentDescription = "Shakthi.ai Logo",
                        modifier = Modifier.size(32.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "SHAKTHI.AI",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = HeraPrimary,
                            letterSpacing = 0.8.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(HeraSecondaryContainer.copy(alpha = 0.4f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "ABDM",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = HeraSecondary
                            )
                        }
                    }
                    Text(
                        text = currentTitle,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HeraOnSurface
                    )
                }
            }

            // 108 SOS & Profile Avatar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:108"))
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HeraError),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("sos_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Emergency,
                        contentDescription = "108 SOS",
                        modifier = Modifier.size(16.dp),
                        tint = HeraOnError
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "108 SOS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = HeraOnError
                    )
                }

                // Profile Avatar with Role Ring
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, HeraSurfaceContainerHigh, CircleShape)
                        .clickable { onAvatarClick() }
                        .testTag("avatar_button")
                ) {
                    AsyncImage(
                        model = "https://lh3.googleusercontent.com/aida-public/AB6AXuDFWItI3uxOIDrcLAQHSRi3i1C8FVv4EIPrTGlUtJNiykAdRGZ931OnsuAqBLuYdxmKfsWohybWTi5SV7URcEgWzQQl3h9B1Bvh-EjSOdI4CJZmpdDecGvqCo9xACmeKdH-17e-Hk-q6wIBk18huB7dmB6ehLXtgPNiMgLc1zKZlvoBPqqT4O-eVZb48SYORDza_QDq1iNrYZ-CehLgA1Kn4EnyupQnsvJJTOsNbVy1csALr_-7HlxWmQ",
                        contentDescription = "User Profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Status indicator dot
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(
                                when (authUser.role) {
                                    UserRole.DOCTOR -> HeraTertiary
                                    UserRole.ASHA -> HeraSecondary
                                    UserRole.PATIENT -> HeraPrimary
                                }
                            )
                            .border(1.5.dp, Color.White, CircleShape)
                    )
                }
            }
        }
    }
}
