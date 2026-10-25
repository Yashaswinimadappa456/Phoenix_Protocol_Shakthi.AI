package com.example.service

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.model.AuthUser
import com.example.model.UserRole
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

data class UserHealthProfile(
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val role: String = "PATIENT",
    val kicksCount: Int = 8,
    val waterIntake: Double = 2.1,
    val ifaDoseTaken: Boolean = false,
    val abhaId: String = "91-4428-8921-1049",
    val lastUpdated: Long = System.currentTimeMillis()
)

object FirebaseFirestoreService {
    private const val TAG = "FirebaseService"
    private var isInitialized = false

    private fun getFirestore(context: Context): FirebaseFirestore {
        val databaseId = try {
            context.getString(R.string.firestore_database_id)
        } catch (e: Exception) {
            "ai-studio-android-heracare-abbe5c8e-2c17-4ec7-8049-ac601dd25a91"
        }
        val app = FirebaseApp.getInstance()
        return FirebaseFirestore.getInstance(app, databaseId)
    }

    fun getCurrentFirebaseUser(): FirebaseUser? {
        return try {
            FirebaseAuth.getInstance().currentUser
        } catch (e: Exception) {
            null
        }
    }

    suspend fun signInWithGoogle(context: Context): AuthUser? {
        val credentialManager = CredentialManager.create(context)
        val webClientId = try {
            context.getString(R.string.default_web_client_id)
        } catch (e: Exception) {
            "398948201144-421o8s3tcdvc6hk5eh5m7si4up0uhg9b.apps.googleusercontent.com"
        }

        val googleIdOption = GetSignInWithGoogleOption.Builder(serverClientId = webClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(
                request = request,
                context = context
            )
            val credential = result.credential
            if (credential is GoogleIdTokenCredential) {
                val googleIdToken = credential.idToken
                val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                val authResult = FirebaseAuth.getInstance().signInWithCredential(authCredential).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null) {
                    val authUser = AuthUser(
                        role = UserRole.PATIENT,
                        identifier = firebaseUser.email ?: firebaseUser.phoneNumber ?: "+91 98452 11094",
                        name = firebaseUser.displayName ?: "Verified Patient",
                        abhaId = "91-4428-8921-1049",
                        token = firebaseUser.uid,
                        isAuthenticated = true
                    )
                    // Sync initial profile
                    saveUserProfile(context, authUser, 8, 2.1, false)
                    authUser
                } else null
            } else {
                null
            }
        } catch (e: GetCredentialCancellationException) {
            Log.w(TAG, "Google Sign-In flow was dismissed by user")
            null
        } catch (e: Exception) {
            Log.e(TAG, "Failed Google Sign-In with Firebase Auth", e)
            null
        }
    }

    suspend fun saveUserProfile(
        context: Context,
        user: AuthUser,
        kicks: Int,
        water: Double,
        ifaTaken: Boolean
    ) {
        val uid = getCurrentFirebaseUser()?.uid ?: user.token
        if (uid.isBlank()) return

        try {
            val db = getFirestore(context)
            val profile = hashMapOf(
                "userId" to uid,
                "displayName" to user.name,
                "email" to user.identifier,
                "role" to user.role.name,
                "kicksCount" to kicks,
                "waterIntake" to water,
                "ifaDoseTaken" to ifaTaken,
                "abhaId" to user.abhaId,
                "lastUpdated" to com.google.firebase.Timestamp.now()
            )
            db.collection("users").document(uid).set(profile, SetOptions.merge()).await()
            Log.d(TAG, "Saved health profile to Firestore for user: $uid")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving to Firestore", e)
        }
    }

    suspend fun loadUserProfile(context: Context, uid: String): UserHealthProfile? {
        return try {
            val db = getFirestore(context)
            val doc = db.collection("users").document(uid).get().await()
            if (doc.exists()) {
                UserHealthProfile(
                    userId = doc.getString("userId") ?: uid,
                    displayName = doc.getString("displayName") ?: "",
                    email = doc.getString("email") ?: "",
                    role = doc.getString("role") ?: "PATIENT",
                    kicksCount = doc.getLong("kicksCount")?.toInt() ?: 8,
                    waterIntake = doc.getDouble("waterIntake") ?: 2.1,
                    ifaDoseTaken = doc.getBoolean("ifaDoseTaken") ?: false,
                    abhaId = doc.getString("abhaId") ?: "91-4428-8921-1049"
                )
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load profile from Firestore", e)
            null
        }
    }

    suspend fun saveTriageAssessment(
        context: Context,
        symptoms: String,
        severity: String,
        summary: String,
        hospitalAction: String,
        doctor: String
    ) {
        val uid = getCurrentFirebaseUser()?.uid ?: "anonymous_patient"
        try {
            val db = getFirestore(context)
            val record = hashMapOf(
                "userId" to uid,
                "symptoms" to symptoms,
                "severity" to severity,
                "simple_summary" to summary,
                "hospital_action" to hospitalAction,
                "recommended_doctor" to doctor,
                "timestamp" to com.google.firebase.Timestamp.now()
            )
            db.collection("triage_assessments").add(record).await()
            Log.d(TAG, "Triage assessment persisted to Firestore")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save triage assessment to Firestore", e)
        }
    }

    fun signOut() {
        try {
            FirebaseAuth.getInstance().signOut()
        } catch (e: Exception) {
            Log.e(TAG, "Sign out error", e)
        }
    }
}
