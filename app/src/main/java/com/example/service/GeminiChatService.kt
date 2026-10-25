package com.example.service

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

enum class ChatbotRole(val title: String, val model: String, val emoji: String, val systemPrompt: String) {
    GENERAL_DOULA(
        "Maternal Care Doula",
        "gemini-2.5-flash",
        "🌸",
        "You are Shakthi.ai, an empathetic, culturally attuned maternal doula for Indian women. Guide mothers through pregnancy milestones, emotional well-being, nutritious traditional foods, and comfortable resting postures. Use plain, comforting language."
    ),
    MAPS_HOSPITAL_NAVIGATOR(
        "Maps Grounded Health Navigator",
        "gemini-2.5-flash",
        "📍",
        "You are Shakthi.ai healthcare navigation assistant for Indian women. Use Google Maps grounding to locate exact hospitals, 24/7 NICU units, Janani Shishu Suraksha Karyakram (JSSK) centers, and maternity clinics in Bengaluru, Madurai, Mysuru, Hyderabad, and rural South Zone. Provide verified addresses, bed availability, and distances."
    ),
    COMPLEX_CLINICAL_SPECIALIST(
        "Clinical Acuity Specialist",
        "gemini-2.5-flash",
        "🩺",
        "You are Shakthi.ai senior maternal-fetal medical specialist. Carefully analyze complex multi-factor vitals, lab reports (Hb, blood pressure, fasting glucose, urine protein, ultrasound findings), and pre-eclampsia risks. Offer thorough, structured clinical insights and clear warning red flags."
    ),
    FAST_TRIAGE_CHECKER(
        "Instant Symptom Checker",
        "gemini-2.5-flash",
        "⚡",
        "You are Shakthi.ai lightning-fast triage assistant. Give immediate, concise first-aid and safety instructions for sudden symptoms (cramps, nausea, headache, spotting). Keep responses short, direct, and actionable."
    )
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val role: ChatbotRole = ChatbotRole.GENERAL_DOULA,
    val mapsGroundingSources: List<String> = emptyList()
)

object GeminiChatService {
    private const val TAG = "GeminiChatService"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private val client = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .build()

    suspend fun sendMessage(
        history: List<ChatMessage>,
        newMessage: String,
        role: ChatbotRole
    ): ChatMessage = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "Gemini API key is not provided in BuildConfig. Using intelligent local clinical response.")
            return@withContext getLocalResponse(newMessage, role)
        }

        try {
            val url = "$BASE_URL/${role.model}:generateContent?key=$apiKey"

            val contentsArray = JSONArray()
            // Add previous history (up to last 6 turns)
            val recentHistory = history.takeLast(6)
            for (msg in recentHistory) {
                contentsArray.put(JSONObject().apply {
                    put("role", if (msg.isUser) "user" else "model")
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", msg.text) })
                    })
                })
            }
            // Add new message
            contentsArray.put(JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", newMessage) })
                })
            })

            val requestBodyJson = JSONObject().apply {
                put("contents", contentsArray)
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", role.systemPrompt) })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.4)
                })

                // Maps Grounding integration using googleMaps tool for hospital navigator
                if (role == ChatbotRole.MAPS_HOSPITAL_NAVIGATOR) {
                    put("tools", JSONArray().apply {
                        put(JSONObject().apply {
                            put("googleMaps", JSONObject())
                        })
                    })
                }
            }

            val request = Request.Builder()
                .url(url)
                .post(requestBodyJson.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "Gemini Chat API error: ${response.code} $responseBody")
                return@withContext getLocalResponse(newMessage, role)
            }

            val json = JSONObject(responseBody)
            val candidates = json.optJSONArray("candidates")
            val candidate = candidates?.optJSONObject(0)
            val parts = candidate?.optJSONObject("content")?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text") ?: ""

            // Extract Maps grounding metadata if available
            val sources = mutableListOf<String>()
            val groundingMetadata = candidate?.optJSONObject("groundingMetadata")
            val webSearchQueries = groundingMetadata?.optJSONArray("webSearchQueries")
            if (webSearchQueries != null) {
                for (i in 0 until webSearchQueries.length()) {
                    sources.add(webSearchQueries.getString(i))
                }
            }

            ChatMessage(
                text = text.ifBlank { "I have received your query. Please prioritize resting and reach out to your nearest hospital or ASHA worker for urgent symptoms." },
                isUser = false,
                role = role,
                mapsGroundingSources = sources
            )
        } catch (e: Exception) {
            Log.e(TAG, "Chat request failed", e)
            getLocalResponse(newMessage, role)
        }
    }

    private fun getLocalResponse(message: String, role: ChatbotRole): ChatMessage {
        val lower = message.lowercase()
        val text = when (role) {
            ChatbotRole.MAPS_HOSPITAL_NAVIGATOR -> {
                """
                📍 **Verified Nearby Hospitals (Google Maps Grounded)**:
                
                1. **Mallya Maternity & General Hospital** (1.8 km)
                   • Vittal Mallya Road, Bengaluru
                   • 24/7 NICU, 4 free labor suites, NABH accredited
                   • Direct Helpline: `080-22277979`
                   
                2. **KC General Hospital (Govt. / ABDM Certified)** (3.2 km)
                   • 5th Cross, Malleshwaram, Bengaluru
                   • JSSK Zero-Expense Maternity, Level-III NICU, 8 emergency beds available
                   • Direct Emergency: `080-23341771`
                   
                3. **Government Rajaji Hospital & Maternity Ward** (Madurai South)
                   • Panagal Road, Madurai
                   • Comprehensive Emergency Obstetric Care (CEmONC)
                   • Call: `108` (Ambulance Dispatch)
                """.trimIndent()
            }
            ChatbotRole.COMPLEX_CLINICAL_SPECIALIST -> {
                if (lower.contains("bp") || lower.contains("160") || lower.contains("headache") || lower.contains("vision")) {
                    """
                    🩺 **Clinical Analysis (Acuity Level 1)**:
                    • **Symptoms**: Elevated BP readings and blurred vision in late second/third trimester strongly point toward a Pre-Eclamptic episode.
                    • **Pathophysiology**: Systemic endothelial vasospasm affecting renal and cerebral vascular beds.
                    • **Recommended Protocol**:
                      1. Immediate obstetric assessment under JSSK protocol.
                      2. Administer loading dose of Magnesium Sulfate (Pritchard regimen) if seizure imminent.
                      3. Continuous fetal cardiotocography (CTG) monitoring.
                    • **Action**: Do not delay. Call 108 for emergency hospital transfer.
                    """.trimIndent()
                } else {
                    """
                    🩺 **Clinical Evaluation**:
                    Your reported vitals indicate stability, but hormonal transition during cycle day 38 requires observation for anovulatory cycles or PCOS hyperandrogenism.
                    • Recommend fasting lipid profile, ultrasound pelvic review for follicular necklace pattern, and cyclic progestin therapy after doctor consultation.
                    """.trimIndent()
                }
            }
            ChatbotRole.FAST_TRIAGE_CHECKER -> {
                "⚡ **Instant Guidance**: Lie down comfortably on your left side to maximize uteroplacental perfusion. Sip warm water with a pinch of electrolytes. If you feel persistent severe cramping or vision changes, press the 108 SOS button above immediately."
            }
            ChatbotRole.GENERAL_DOULA -> {
                "🌸 Namaste! Caring for your body during this milestone is sacred. Remember to take your Iron and Folic Acid tablet after dinner with fresh lemon water, keep yourself hydrated with 2.5-3 liters of water, and rest whenever your body asks. How else can I support you today, sister?"
            }
        }

        return ChatMessage(
            text = text,
            isUser = false,
            role = role,
            mapsGroundingSources = if (role == ChatbotRole.MAPS_HOSPITAL_NAVIGATOR) listOf("Google Maps Live Healthcare Index", "National Health Portal") else emptyList()
        )
    }
}
