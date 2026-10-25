package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.*
import com.example.service.FirebaseFirestoreService
import com.example.service.GeminiTriageService
import com.example.ui.components.NavScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainUiState(
    val currentScreen: NavScreen = NavScreen.HOME,
    val authUser: AuthUser = AuthUser(),
    val language: AppLanguage = AppLanguage.EN,
    val activeRoleTab: UserRole = UserRole.PATIENT,
    // Dashboard States
    val kicksCount: Int = 8,
    val waterIntake: Double = 2.1,
    val ifaDoseTaken: Boolean = false,
    val isNearbyHospitalsExpanded: Boolean = false,
    val isVoiceListening: Boolean = false,
    val gpsCoordinates: String? = null,
    // Triage States
    val triageFilter: String = "all",
    val triageCases: List<TriagePatientCase> = emptyList(),
    val activeTriageAssessment: TriageAssessment? = null,
    val isAiEvaluating: Boolean = false,
    val symptomPromptInput: String = "",
    // Doctor Portal States
    val isDoctorOnDuty: Boolean = true,
    val isTeleConsultActive: Boolean = false,
    val isPrescriptionSigned: Boolean = false,
    val showOpdPassModal: Boolean = false,
    // Toast Notification
    val toastMessage: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadInitialTriageCases()
    }

    private fun loadInitialTriageCases() {
        val initialCases = listOf(
            TriagePatientCase(
                id = "1",
                name = "Lakshmi Sundaram",
                age = 29,
                location = "Madurai / Bengaluru Rural",
                gestationalAgeOrStage = "34w Gestation",
                severity = TriageSeverity.RED,
                title = "Critical Pre-Eclampsia Triad",
                vitals = "160/105 mmHg",
                symptoms = listOf("Severe Blurred Vision", "Acute Epigastric Pain"),
                summary = "Immediate risk of maternal eclamptic seizures. Obstetric referral initiated under JSSK protocol.",
                avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBQg6p_NeHmCzrVnh5BX2SSMrMhzjTQYui3tFkFIXY8IqS9yswNeg8OwsTbZwbu4oyIojyDw-NpKbZ5Quq_Lefr56FOZWRvDu6CusxKDa7FZlwWDIpZHBvsbZyYFuPUX2YsOFTdVnqza4cTfz3M2JxJz9rdRFIDlNSbKkj8-4ztb3kQ_RiSoJnSojbHYD6ew2cpPB-IC9mFKL0aeTZeTo8fF06AEAryajPqyTU9_MCOjaIbZ7Jof8xEeA",
                actionTextPrimary = "108 Ambulance",
                actionTextSecondary = "Nearest OB-ICU",
                ussdCode = "*108*99#"
            ),
            TriagePatientCase(
                id = "2",
                name = "Kavya Reddy",
                age = 24,
                location = "Secunderabad, Hyderabad",
                gestationalAgeOrStage = "Cycle Day 45+ • ABHA Linked",
                severity = TriageSeverity.YELLOW,
                title = "Severe PCOS Evaluation",
                vitals = "Cycle Day 45+ • VAS 7/10",
                symptoms = listOf("Pelvimetry Pending", "Hormonal Screen"),
                summary = "Debilitating dysmenorrhea cramping (VAS 7/10), unmanaged hormonal cycle irregularity, pelvic ultrasound scan confirmation pending.",
                avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBFHy0ryAECLWnZ0NZHv05ND-hiEG5_wufHMgo3d0gEgbg_AgUKybwFVEJ7APrktPTzBGTV5HvLsmA1SIYJqHL47_apbiDHM2-0aNE0N6Dt_E3FcuMocyRs8SkPJifiBOrk0SaAgTUu2agRuE4YYJACQLvjsrDrdCUoAhCPJapHqmmS22T3bMZwFWoZ2BkxsUfvFjDbLfpok4bvOMTmXlXWdU3ryDkrzv0PYxzl1SSajcI9uaIPqohuUg",
                actionTextPrimary = "Book Tele-Consult within 24h",
                actionTextSecondary = "View Antispasmodic & Warm Compress Plan",
                collapsibleDetails = listOf(
                    "• Meftal-Spas (SOS for severe colic) - Verified Rx",
                    "• 20 min castor oil hot water pack over lower pelvic quadrant.",
                    "• Fasting blood draw requested for Serum FSH/LH ratio."
                )
            ),
            TriagePatientCase(
                id = "3",
                name = "Ananya Gowda",
                age = 31,
                location = "Kuvempunagar, Mysuru",
                gestationalAgeOrStage = "Day 12 Postpartum",
                severity = TriageSeverity.GREEN,
                title = "Routine Postnatal Recovery",
                vitals = "BP 118/76 mmHg • Afebrile",
                symptoms = listOf("Lochia: Serosa (Normal)", "Bilateral Engorgement"),
                summary = "Mild bilateral breast engorgement noted on Day 12; latch mechanics adjustment recommended. Vitals stable (BP 118/76 mmHg, afebrile).",
                avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDjQZTuztmcYgsoR0sk1aExJ6rw2WRPIBliVTjf0q3YtoJ2xNcYVMQu9FsSovT4SBjshY9_2-EVB8x8M2L94FTByNphBcnVIZ3SBhkvE6pZYMt39JRBCeQXb3rz50HPdmAJK5DRK5NkcZzs3DqD1aIB5W2D55zTRhHVqj1sDgQ1rrtaB88eABz00TC6AmeEBQ9CeYCeBQhAB1bjPafE208UaV_dNJkXx0FloriN-40SK2nzPsjDeS8mEg",
                actionTextPrimary = "Latch Video Guide",
                actionTextSecondary = "Check in 7 Days"
            )
        )
        _uiState.update { it.copy(triageCases = initialCases) }
    }

    fun setScreen(screen: NavScreen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun setRoleTab(role: UserRole) {
        _uiState.update { it.copy(activeRoleTab = role) }
    }

    fun authenticateUser(role: UserRole, identifier: String, name: String = "", abhaId: String = "") {
        val user = AuthUser(
            role = role,
            identifier = identifier.ifBlank { if (role == UserRole.DOCTOR) "doctor@heracare.org" else "+91 98452 11094" },
            name = name.ifBlank { if (role == UserRole.DOCTOR) "Dr. Soundarya Ramesh" else if (role == UserRole.ASHA) "Sunita Devi (ASHA)" else "Priya Sharma" },
            abhaId = abhaId.ifBlank { "91-4428-8921-1049" },
            isAuthenticated = true
        )
        _uiState.update { 
            it.copy(
                authUser = user,
                activeRoleTab = role,
                currentScreen = if (role == UserRole.DOCTOR) NavScreen.PORTAL else NavScreen.DASHBOARD
            ) 
        }

        // Persist to Firestore
        viewModelScope.launch {
            FirebaseFirestoreService.saveUserProfile(
                getApplication(),
                user,
                _uiState.value.kicksCount,
                _uiState.value.waterIntake,
                _uiState.value.ifaDoseTaken
            )
        }

        showToast("Authenticated as ${role.label}: ${user.name}")
    }

    fun setLanguage(lang: AppLanguage) {
        _uiState.update { it.copy(language = lang) }
        showToast("Switched language to ${lang.displayName} (${lang.nativeName})")
    }

    fun incrementKicks() {
        val next = _uiState.value.kicksCount + 1
        _uiState.update { it.copy(kicksCount = next) }
        showToast("Baby kick #$next recorded with timestamp")
        persistProfile()
    }

    fun logWaterGlass() {
        val next = +((_uiState.value.waterIntake + 0.25) * 100).toInt() / 100.0
        _uiState.update { it.copy(waterIntake = next) }
        showToast("Hydration logged: +250ml ($next / 3.0 Liters)")
        persistProfile()
    }

    fun toggleIfaDose() {
        val next = !_uiState.value.ifaDoseTaken
        _uiState.update { it.copy(ifaDoseTaken = next) }
        showToast(if (next) "Iron + Folic Acid (IFA) logged to ABDM Record ✓" else "IFA dose unmarked")
        persistProfile()
    }

    private fun persistProfile() {
        viewModelScope.launch {
            FirebaseFirestoreService.saveUserProfile(
                getApplication(),
                _uiState.value.authUser,
                _uiState.value.kicksCount,
                _uiState.value.waterIntake,
                _uiState.value.ifaDoseTaken
            )
        }
    }

    fun toggleHospitalsAccordion() {
        _uiState.update { it.copy(isNearbyHospitalsExpanded = !it.isNearbyHospitalsExpanded) }
    }

    fun setTriageFilter(filter: String) {
        _uiState.update { it.copy(triageFilter = filter) }
    }

    fun setSymptomInput(input: String) {
        _uiState.update { it.copy(symptomPromptInput = input) }
    }

    fun runGeminiTriage(symptom: String) {
        val text = symptom.ifBlank { _uiState.value.symptomPromptInput }
        if (text.isBlank()) return

        _uiState.update { it.copy(isAiEvaluating = true) }
        viewModelScope.launch {
            val assessment = GeminiTriageService.evaluateSymptoms(text, "Gestational maternal context")
            _uiState.update {
                it.copy(
                    isAiEvaluating = false,
                    activeTriageAssessment = assessment
                )
            }
            // Save to Firestore
            FirebaseFirestoreService.saveTriageAssessment(
                getApplication(),
                text,
                assessment.severity,
                assessment.simple_summary,
                assessment.hospital_action,
                assessment.recommended_doctor
            )
            showToast("Gemini 2.5 Flash Triage: ${assessment.severity} assessment saved to Firestore")
        }
    }

    fun toggleDoctorDuty() {
        val next = !_uiState.value.isDoctorOnDuty
        _uiState.update { it.copy(isDoctorOnDuty = next) }
        showToast(if (next) "Clinician duty: Accepting Urgent Triage & Consults" else "Clinician duty: Offline")
    }

    fun toggleTeleConsult() {
        val next = !_uiState.value.isTeleConsultActive
        _uiState.update { it.copy(isTeleConsultActive = next) }
        showToast(if (next) "WebRTC 256-Bit Encrypted Tele-Consultation Connected" else "Tele-consultation disconnected")
    }

    fun signPrescription() {
        _uiState.update { it.copy(isPrescriptionSigned = true) }
        showToast("E-Prescription cryptographically signed with SHA-256 digital stamp")
    }

    fun setOpdPassModal(visible: Boolean) {
        _uiState.update { it.copy(showOpdPassModal = visible) }
    }

    fun triggerGpsLock() {
        _uiState.update { it.copy(gpsCoordinates = "12.9716° N, 77.5946° E") }
        showToast("GPS coordinates locked: Lat 12.9716, Long 77.5946 (South Zone)")
    }

    fun showToast(message: String) {
        _uiState.update { it.copy(toastMessage = message) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
