package com.example.model

enum class UserRole(val label: String, val emoji: String) {
    PATIENT("Patient", "🌸"),
    ASHA("ASHA Didi", "🥻"),
    DOCTOR("Doctor", "🩺")
}

data class AuthUser(
    val role: UserRole = UserRole.PATIENT,
    val identifier: String = "+91 98452 11094",
    val name: String = "Priya Sharma",
    val abhaId: String = "91-4428-8921-1049",
    val token: String = "TOKEN-AUTH-HC-2024",
    val isAuthenticated: Boolean = true
)
