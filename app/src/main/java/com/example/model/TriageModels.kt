package com.example.model

enum class TriageSeverity(val label: String) {
    RED("Level 1 Red"),
    YELLOW("Level 2 Consult"),
    GREEN("Level 3 Home")
}

data class TriageAssessment(
    val severity: String = "GREEN",
    val simple_summary: String = "",
    val home_care_steps: List<String> = emptyList(),
    val hospital_action: String = "",
    val recommended_doctor: String = ""
)

data class TriagePatientCase(
    val id: String,
    val name: String,
    val age: Int,
    val location: String,
    val gestationalAgeOrStage: String,
    val severity: TriageSeverity,
    val title: String,
    val vitals: String,
    val symptoms: List<String>,
    val summary: String,
    val avatarUrl: String,
    val abhaId: String = "",
    val actionTextPrimary: String = "",
    val actionTextSecondary: String = "",
    val ussdCode: String = "*108*99#",
    val isCollapsibleOpen: Boolean = false,
    val collapsibleDetails: List<String> = emptyList()
)

data class HospitalInfo(
    val name: String,
    val tag: String,
    val distance: String,
    val capacity: String,
    val phone: String
)
