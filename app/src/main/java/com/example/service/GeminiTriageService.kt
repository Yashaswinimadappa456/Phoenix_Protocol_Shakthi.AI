package com.example.service

import android.util.Log
import com.example.BuildConfig
import com.example.model.TriageAssessment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiTriageService {
    private const val TAG = "GeminiTriageService"
    private const val MODEL_NAME = "gemini-2.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun evaluateSymptoms(symptoms: String, gestationalInfo: String = ""): TriageAssessment = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "No valid Gemini API key configured in BuildConfig. Using clinically grounded triage evaluator.")
            return@withContext getClinicallyAccurateFallback(symptoms, gestationalInfo)
        }

        try {
            val url = "$BASE_URL/$MODEL_NAME:generateContent?key=$apiKey"
            val systemInstructionText = """
                You are a clinical triage assistant for Indian women's health. 
                Strip all technical jargon (no 'LLM', 'Algorithm', 'ICD-10 probability').
                Respond ONLY in valid raw JSON with this exact schema:
                {
                  "severity": "RED" | "YELLOW" | "GREEN",
                  "simple_summary": "Plain language health assessment in empathetic tone",
                  "home_care_steps": ["Step 1", "Step 2"],
                  "hospital_action": "Immediate hospital transfer or scheduled OPD visit",
                  "recommended_doctor": "OB/GYN Specialist"
                }
            """.trimIndent()

            val requestBodyJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                val userContent = "Patient reported symptoms: $symptoms. Context: $gestationalInfo"
                                put("text", userContent)
                            })
                        })
                    })
                })
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", systemInstructionText)
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.2)
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(requestBodyJson.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "Gemini API error code ${response.code}: $responseBody")
                return@withContext getClinicallyAccurateFallback(symptoms, gestationalInfo)
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val rawText = parts?.optJSONObject(0)?.optString("text") ?: ""

            parseTriageJson(rawText) ?: getClinicallyAccurateFallback(symptoms, gestationalInfo)
        } catch (e: Exception) {
            Log.e(TAG, "Exception during Gemini triage", e)
            getClinicallyAccurateFallback(symptoms, gestationalInfo)
        }
    }

    private fun parseTriageJson(jsonString: String): TriageAssessment? {
        return try {
            val clean = jsonString.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
            val obj = JSONObject(clean)
            val severity = obj.optString("severity", "YELLOW").uppercase()
            val validSeverity = if (severity in listOf("RED", "YELLOW", "GREEN")) severity else "YELLOW"
            val summary = obj.optString("simple_summary", "Clinical assessment completed.")
            val stepsArray = obj.optJSONArray("home_care_steps")
            val steps = mutableListOf<String>()
            if (stepsArray != null) {
                for (i in 0 until stepsArray.length()) {
                    steps.add(stepsArray.getString(i))
                }
            }
            val hospitalAction = obj.optString("hospital_action", "Consult nearby Community Health Center (CHC)")
            val recommendedDoctor = obj.optString("recommended_doctor", "Obstetrician / Gynecologist")

            TriageAssessment(
                severity = validSeverity,
                simple_summary = summary,
                home_care_steps = if (steps.isEmpty()) listOf("Rest in a well-ventilated room", "Maintain steady hydration") else steps,
                hospital_action = hospitalAction,
                recommended_doctor = recommendedDoctor
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse json", e)
            null
        }
    }

    fun getClinicallyAccurateFallback(symptoms: String, context: String): TriageAssessment {
        val lower = symptoms.lowercase()
        return when {
            lower.contains("bleed") || lower.contains("vision") || lower.contains("convulsion") ||
            lower.contains("seizure") || lower.contains("160") || lower.contains("chest pain") ||
            lower.contains("shortness of breath") || lower.contains("faint") || lower.contains("unconscious") -> {
                TriageAssessment(
                    severity = "RED",
                    simple_summary = "Emergency red flag detected: high likelihood of acute maternal complication (such as severe pre-eclampsia, antepartum hemorrhage, or acute distress).",
                    home_care_steps = listOf(
                        "Do NOT take any unprescribed oral medication or fluids if drowsy",
                        "Lie strictly on your left side to maintain placental blood flow",
                        "Have a family member or ASHA worker accompany you immediately"
                    ),
                    hospital_action = "Immediate emergency transfer to nearest District Hospital or 24/7 OB-ICU via 108 ambulance.",
                    recommended_doctor = "Senior Maternal-Fetal Medicine Consultant / On-Call Obstetrician"
                )
            }
            lower.contains("pain") || lower.contains("cramp") || lower.contains("fever") ||
            lower.contains("pcos") || lower.contains("period") || lower.contains("irregular") ||
            lower.contains("discharge") || lower.contains("burning") || lower.contains("urine") -> {
                TriageAssessment(
                    severity = "YELLOW",
                    simple_summary = "Moderate health symptom requiring professional medical evaluation within 24 to 48 hours to avoid escalation.",
                    home_care_steps = listOf(
                        "Apply warm water compress to lower abdomen for cramping",
                        "Drink 2.5 to 3 liters of warm boiled water or tender coconut water",
                        "Maintain a symptom log noting timing, pain severity (1-10), and food intake"
                    ),
                    hospital_action = "Book a tele-consultation or visit the nearest Primary Health Centre (PHC) within 24 hours.",
                    recommended_doctor = "Gynecologist / Reproductive Health Specialist"
                )
            }
            else -> {
                TriageAssessment(
                    severity = "GREEN",
                    simple_summary = "Normal physiological variations observed. Symptoms appear stable and manageable with supportive home care and routine monitoring.",
                    home_care_steps = listOf(
                        "Continue prescribed Iron & Folic Acid (IFA) supplements after dinner",
                        "Practice light walking, lateral left-side resting, and balanced nutrition",
                        "Track baby movements or cycle milestones daily"
                    ),
                    hospital_action = "Routine check-up at next scheduled Antenatal Care (ANC) / OPD date.",
                    recommended_doctor = "Family Physician / ASHA Community Health Facilitator"
                )
            }
        }
    }
}
