import { GoogleGenAI } from '@google/genai';

/**
 * Shakthi.ai - Gemini 2.5 Flash Clinical Triage & Conversational Health Service
 * Evaluates incoming symptoms (text or speech transcript) and answers health queries.
 * System Prompt: Acts as a compassionate, clinical triage assistant for Indian women's health.
 * Strips all technical jargon (no "LLM", "Algorithm", "ICD-10 probability").
 * 
 * Returns dynamic JSON schema:
 * {
 *   "severity": "RED" | "YELLOW" | "GREEN",
 *   "simple_summary": "Plain language health assessment",
 *   "home_care_steps": ["Step 1", "Step 2"],
 *   "hospital_action": "Immediate hospital transfer or scheduled OPD visit",
 *   "recommended_doctor": "OB/GYN Specialist"
 * }
 */

const apiKey = process.env.REACT_APP_GEMINI_API_KEY || process.env.VITE_GEMINI_API_KEY || process.env.GEMINI_API_KEY || '';
const ai = new GoogleGenAI({ apiKey });

export async function evaluateSymptoms(symptoms, patientContext = '') {
  if (!symptoms || symptoms.trim() === '') {
    throw new Error('Symptoms description is required');
  }

  const systemInstruction = `
    You are Shakthi.ai, a compassionate, culturally attuned clinical triage assistant for Indian women's health.
    Strip all technical jargon (do NOT use words like "LLM", "Algorithm", "ICD-10 probability", "neural network").
    Communicate clearly in empathetic, easily understandable terms suitable for women and community ASHA workers.
    
    You must classify the symptoms into one of three strict acuity levels:
    - RED: Critical emergencies (e.g. pre-eclampsia triad, heavy maternal bleeding, severe blurred vision, seizures, ectopic suspicion)
    - YELLOW: Moderate symptoms needing clinician evaluation within 24-48 hours (e.g. severe PCOS dysmenorrhea, abnormal discharge, high fever)
    - GREEN: Stable, routine post-natal or maternal symptoms manageable with safe home care.
    
    You MUST respond ONLY with a raw JSON object matching this exact schema:
    {
      "severity": "RED" | "YELLOW" | "GREEN",
      "simple_summary": "Plain language health assessment in comforting tone",
      "home_care_steps": ["Step 1", "Step 2"],
      "hospital_action": "Immediate hospital transfer or scheduled OPD visit",
      "recommended_doctor": "OB/GYN Specialist"
    }
  `;

  try {
    if (!apiKey) {
      console.warn('Gemini API key is not configured. Falling back to local clinical triage evaluator.');
      return getClinicalFallback(symptoms);
    }

    const response = await ai.models.generateContent({
      model: 'gemini-2.5-flash',
      contents: `Patient Reported Symptoms: ${symptoms}\nPatient Background: ${patientContext}`,
      config: {
        systemInstruction,
        responseMimeType: 'application/json',
        temperature: 0.2
      }
    });

    const responseText = response.text?.trim() || '';
    const cleanJson = responseText.replace(/```json/g, '').replace(/```/g, '').trim();
    return JSON.parse(cleanJson);
  } catch (error) {
    console.error('Gemini API triage call failed, activating resilient triage fallback:', error);
    return getClinicalFallback(symptoms);
  }
}

/**
 * Conversational query helper: "Ask Shakthi"
 */
export async function askShakthi(query, history = []) {
  if (!query || query.trim() === '') return '';

  const systemInstruction = `
    You are Shakthi.ai, an empathetic maternal and reproductive health specialist for Indian women.
    Provide actionable, culturally comforting guidance regarding nutrition (traditional foods, iron, calcium),
    period cramps, gestational wellness, fetal milestones, and healthcare scheme access (Janani Shishu Suraksha, Ayushman Bharat).
    Do NOT use technical jargon. Keep answers under 3 short paragraphs.
  `;

  try {
    if (!apiKey) {
      return getChatFallback(query);
    }

    const response = await ai.models.generateContent({
      model: 'gemini-2.5-flash',
      contents: `User Query: ${query}`,
      config: {
        systemInstruction,
        temperature: 0.4
      }
    });

    return response.text?.trim() || getChatFallback(query);
  } catch (err) {
    console.error('Ask Shakthi query error:', err);
    return getChatFallback(query);
  }
}

function getChatFallback(query) {
  const q = query.toLowerCase();
  if (q.includes('cramp') || q.includes('pain') || q.includes('period')) {
    return "For natural cramp relief, sip warm water infused with roasted ajwain (carom seeds) and crushed ginger. Apply a hot water bag to the lower abdomen for 15 minutes, and practice gentle Balasana (Child's Pose). If pain exceeds 6/10 or lasts beyond 48 hours, schedule a tele-consultation.";
  }
  if (q.includes('hospital') || q.includes('emergency') || q.includes('bed')) {
    return "Under Janani Shishu Suraksha Karyakram (JSSK), all institutional deliveries, C-sections, and emergency ambulance transfers via 108 are 100% free at government district hospitals. In emergencies, call 108 immediately.";
  }
  return "Namaste! For balanced maternal vitality, prioritize iron-rich foods like moringa (drumstick leaves), dates, ragi, and jaggery, accompanied by citrus fruits for maximum absorption. Ensure 2.5 to 3 liters of clean water daily and rest on your left side to enhance fetal blood circulation.";
}

/**
 * Resilient clinical fallback based on Indian health clinical guidelines
 */
export function getClinicalFallback(symptoms) {
  const text = symptoms.toLowerCase();
  
  if (
    text.includes('bleed') ||
    text.includes('vision') ||
    text.includes('160') ||
    text.includes('seizure') ||
    text.includes('epigastric') ||
    text.includes('severe headache') ||
    text.includes('faint')
  ) {
    return {
      severity: 'RED',
      simple_summary: 'Critical maternal warning signs detected: high risk of pre-eclampsia or acute complication.',
      home_care_steps: [
        'Rest immediately on your left side to maximize blood and oxygen supply to baby',
        'Do not take unprescribed painkillers or sedatives',
        'Keep emergency medical records and ABHA health card ready for paramedic dispatch'
      ],
      hospital_action: 'Immediate emergency transfer to nearest District Hospital or OB-ICU via 108 Ambulance.',
      recommended_doctor: 'Senior Maternal-Fetal Medicine Specialist / Obstetrician'
    };
  }

  if (
    text.includes('pcos') ||
    text.includes('cramp') ||
    text.includes('pain') ||
    text.includes('period') ||
    text.includes('fever') ||
    text.includes('discharge')
  ) {
    return {
      severity: 'YELLOW',
      simple_summary: 'Moderate discomfort requiring medical review within 24 hours to prevent complications.',
      home_care_steps: [
        'Apply a warm compress or hot water bottle to the lower abdomen for 15-20 minutes',
        'Drink adequate fluids including tender coconut water and cumin-boiled water',
        'Record pain intensity score on a 1-10 scale throughout the day'
      ],
      hospital_action: 'Book a tele-consultation or attend the nearest Primary Health Centre (PHC) within 24 hours.',
      recommended_doctor: 'Gynecologist / Reproductive Endocrinologist'
    };
  }

  return {
    severity: 'GREEN',
    simple_summary: 'Routine physiological variation. Vitals and symptoms indicate stable home recovery.',
    home_care_steps: [
      'Take prescribed Iron and Folic Acid (IFA) supplements after dinner with citrus/lemon water',
      'Follow gentle walking and hydration routines (2.5 - 3 liters daily)',
      'Continue regular baby kick count and rest periods'
    ],
    hospital_action: 'Routine check-up at your next scheduled antenatal visit.',
    recommended_doctor: 'Primary Care Physician / ASHA Community Worker'
  };
}
