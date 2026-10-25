# 🌸 shakthi.ai — Vernacular Full-Lifespan Women's Healthcare Platform

> **Hackathon Final Submission** | **Team Phoenix Protocol**: Yashaswini & Thanuja  
> *Culturally attuned, dignified maternal and women’s healthcare for Indian women across every milestone.*

---

## 📌 Problem Statement & Context

In India, maternal health complications such as pre-eclampsia, postpartum hemorrhage, and unmanaged PCOS continue to pose severe risks due to three critical barriers:
1. **Language & Literacy Barriers:** Existing digital health tools rely on complex, jargon-heavy English interfaces inaccessible to low-literacy rural mothers.
2. **Digital Divide & Network Instability:** High-risk emergencies frequently happen in low-bandwidth 2G/3G zones where web apps fail.
3. **Fragmented Lifespan Care:** Most solutions address isolated health events rather than providing continuous care from menarche to postpartum recovery.

**shakthi.ai** bridges this gap by delivering a **voice-first, multilingual, zero-bandwidth-resilient health companion** connected directly to emergency services and India's Ayushman Bharat Digital Mission (ABDM) infrastructure.

---

## ✨ Key Features & Innovation

### 🎙️ 1. Vernacular Voice-First Interface
- Native speech recognition and audio feedback in **6 regional languages** (English, Tamil, Telugu, Kannada, Malayalam, Hindi).
- Jargon-free UI designed specifically for low-literacy rural mothers and community health workers.

### 🌸 2. Complete Lifespan Care Continuum
Covering the entire reproductive lifecycle across 4 distinct phases:
- **Periods & Hormonal Health:** Irregular cycle tracking, PCOS/Endometriosis risk screening, and cramp relief.
- **Pregnancy Care:** Month-by-month checks, pre-eclampsia symptom monitoring, and vital tracking.
- **Postpartum & Recovery:** Healing checks, pelvic floor care, and lactation guidance.
- **Ongoing Vitality:** Perimenopause awareness and routine preventive screenings.

### 🚥 3. Real-Time AI Triage Routing (Gemini 2.5 Flash)
- **🔴 Red Card (Urgent Emergency):** Triggers immediate 108 ambulance dispatch, GPS tracking, and offline fallback.
- **🟡 Yellow Card (Priority Consultation):** Schedules a gynaecologist tele-consultation within 24 hours.
- **🟢 Green Card (Safe Home Care):** Delivers actionable local self-care guidance and hydration/symptom logging.

### 📱 4. Zero-Bandwidth Offline Resilience
- **USSD Emergency Dialer:** Generates interactive USSD strings (`*108*99#`) for feature phones or offline 2G conditions.
- **GPS SMS Payload:** Compiles direct SMS dispatches with live latitude and longitude coordinates when internet fails.

### 🏥 5. ABDM Compliance & Closed-Loop Handoff
- **ABHA ID Integration:** Verified profile linking under Ayushman Bharat Digital Mission guidelines.
- **Digital OPD Pass:** Generates printable/downloadable consultation passes complete with digital signatures and scannable verification QR codes.
- **ASHA Field Mode:** Rapid multi-patient batch logging mode for community healthcare workers.

---

## 🛠️ Tech Stack Matrix

| Component | Technology | Rationale |
| :--- | :--- | :--- |
| **AI Reasoning Engine** | Google Gemini 2.5 Flash (`@google/genai`) | Fast, structured JSON clinical triage and natural language understanding |
| **Frontend Framework** | React 18 + Tailwind CSS + Lucide Icons | Responsive, accessible, high-contrast role-based design |
| **Voice Interface** | Web Speech API (`webkitSpeechRecognition`) | Hands-free 6-language intake for low-literacy users |
| **Telehealth Video** | WebRTC | Encrypted peer-to-peer doctor consultations |
| **Records & Compliance** | ABDM / FHIR-Ready Standards | Interoperable longitudinal ABHA health records |
| **Offline Fallback** | USSD `*108*99#` & Native Telephony API | Zero-data emergency dispatch resilience |

---

## 👥 Team Phoenix Protocol

- **Yashaswini** — Product Strategy, AI Engineering & ABDM Architecture  
- **Thanuja** — UI/UX Design, Multilingual Localization & Mobile Engineering  

---

## 📄 License & Compliance

Built for hackathon demonstration. Designed in alignment with Ayushman Bharat Digital Mission (ABDM) and Data Protection and Digital Privacy (DPDP) standards.
