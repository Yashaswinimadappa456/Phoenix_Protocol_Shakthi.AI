import React, { useState } from 'react';
import { evaluateSymptoms } from '../services/geminiService';

export default function TriageCanvas({ incomingSymptom = '', language, t }) {
  const [filter, setFilter] = useState('all');
  const [symptomInput, setSymptomInput] = useState(incomingSymptom);
  const [isEvaluating, setIsEvaluating] = useState(false);
  const [aiResult, setAiResult] = useState(null);
  const [rxOpen, setRxOpen] = useState(false);
  const [toastMessage, setToastMessage] = useState('');

  const showToast = (msg) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(''), 3000);
  };

  const handleRunAiTriage = async () => {
    if (!symptomInput.trim()) return;
    setIsEvaluating(true);
    try {
      const res = await evaluateSymptoms(symptomInput, 'Maternal Triage South Zone');
      setAiResult(res);
      showToast('Gemini 2.5 Flash completed clinical assessment');
    } catch (e) {
      showToast('Error in AI evaluation, using clinical rules');
    } finally {
      setIsEvaluating(false);
    }
  };

  const triggerUssd = (code) => {
    showToast(`Triggering offline USSD: ${code}`);
    window.location.href = `tel:${encodeURIComponent(code)}`;
  };

  return (
    <div className="flex flex-col w-full px-4 space-y-4 pb-12">
      {/* Top Context & Offline Status Badge */}
      <div className="w-full bg-slate-100 rounded-2xl p-4 border border-slate-300 flex flex-col gap-2">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <span className="material-symbols-outlined text-[#B90538] text-[20px]">emergency_home</span>
            <span className="text-sm font-extrabold text-slate-900">{t?.triageCanvas || "Emergency & Triage Canvas"}</span>
          </div>
          <div className="flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-emerald-100 text-[#006C49] border border-emerald-300">
            <span className="w-2 h-2 rounded-full bg-[#006C49] animate-pulse"></span>
            <span className="text-xs font-bold">Offline Mesh Ready</span>
          </div>
        </div>
        <div className="flex items-center justify-between text-xs text-slate-700 font-medium">
          <span>Priority sorting: South Zone (TN • KA • TS)</span>
          <span className="flex items-center gap-1 text-[#00628D] font-bold">
            <span className="material-symbols-outlined text-[14px]">sync</span> Live Sync Active
          </span>
        </div>
      </div>

      {/* Interactive Gemini AI Triage Assistant */}
      <div className="w-full bg-white rounded-2xl p-4 shadow-md border-2 border-rose-200 flex flex-col gap-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <span className="w-7 h-7 rounded-full bg-rose-100 text-[#B90538] flex items-center justify-center">
              <span className="material-symbols-outlined text-[16px]">psychology</span>
            </span>
            <h3 className="text-sm font-extrabold text-slate-900">{t?.liveAiTriage || "Live AI Clinical Triage"}</h3>
          </div>
          <span className="px-2.5 py-0.5 rounded-full bg-rose-100 text-[#B90538] text-[10px] font-extrabold border border-rose-200">
            Gemini 2.5 Flash
          </span>
        </div>

        <div className="flex gap-2">
          <input
            type="text"
            value={symptomInput}
            onChange={(e) => setSymptomInput(e.target.value)}
            placeholder="Type or dictate symptoms (e.g. blurred vision, fever)..."
            className="flex-1 py-2.5 px-3 rounded-xl bg-slate-50 border-2 border-slate-300 text-xs text-slate-900 font-semibold outline-none focus:border-[#B90538]"
          />
          <button
            onClick={handleRunAiTriage}
            disabled={isEvaluating}
            className="px-4 py-2.5 rounded-xl bg-[#B90538] text-white text-xs font-bold active:scale-95 shadow-md flex items-center gap-1.5"
          >
            <span className="material-symbols-outlined text-[16px]">
              {isEvaluating ? 'sync' : 'auto_awesome'}
            </span>
            <span>{isEvaluating ? 'Evaluating...' : (t?.evaluate || 'Triage')}</span>
          </button>
        </div>

        {/* AI Result Card */}
        {aiResult && (
          <div className={`p-3.5 rounded-xl border-2 flex flex-col gap-2.5 shadow-sm ${
            aiResult.severity === 'RED'
              ? 'bg-rose-50 border-red-600 text-slate-900'
              : aiResult.severity === 'YELLOW'
              ? 'bg-amber-50 border-amber-500 text-slate-900'
              : 'bg-emerald-50 border-emerald-600 text-slate-900'
          }`}>
            <div className="flex items-center justify-between">
              <span className={`px-2.5 py-1 rounded-full text-xs font-bold ${
                aiResult.severity === 'RED'
                  ? 'bg-red-600 text-white'
                  : aiResult.severity === 'YELLOW'
                  ? 'bg-amber-500 text-white'
                  : 'bg-emerald-600 text-white'
              }`}>
                {aiResult.severity === 'RED' ? 'Level 1 RED (Emergency)' : aiResult.severity === 'YELLOW' ? 'Level 2 YELLOW (Consult)' : 'Level 3 GREEN (Home Care)'}
              </span>
              <span className="text-xs font-bold text-slate-900">{aiResult.recommended_doctor}</span>
            </div>

            <p className="text-xs leading-relaxed font-bold text-slate-900">{aiResult.simple_summary}</p>

            <div className="bg-white p-2.5 rounded-lg text-xs flex flex-col gap-1 border border-slate-200">
              <span className="font-extrabold text-slate-900">Home Care Steps:</span>
              <ul className="list-disc pl-4 space-y-0.5 text-slate-800 font-medium">
                {aiResult.home_care_steps?.map((step, idx) => (
                  <li key={idx}>{step}</li>
                ))}
              </ul>
            </div>

            <div className="text-xs font-bold flex items-center gap-1 text-[#B90538]">
              <span className="material-symbols-outlined text-[16px]">local_hospital</span>
              <span>Hospital Action: {aiResult.hospital_action}</span>
            </div>
          </div>
        )}
      </div>

      {/* Filter Pills */}
      <div className="flex items-center gap-2 overflow-x-auto pb-1 no-scrollbar">
        <button
          onClick={() => setFilter('all')}
          className={`shrink-0 px-3.5 py-1.5 rounded-full text-xs font-bold transition-all border ${
            filter === 'all' ? 'bg-[#B90538] text-white border-[#B90538] shadow-sm' : 'bg-white text-slate-800 border-slate-300'
          }`}
        >
          All Triage (3)
        </button>
        <button
          onClick={() => setFilter('red')}
          className={`shrink-0 px-3.5 py-1.5 rounded-full text-xs font-bold flex items-center gap-1 transition-all border ${
            filter === 'red' ? 'bg-[#B90538] text-white border-[#B90538] shadow-sm' : 'bg-white text-slate-800 border-slate-300'
          }`}
        >
          <span className="w-2 h-2 rounded-full bg-red-600"></span> Urgent Level 1
        </button>
        <button
          onClick={() => setFilter('yellow')}
          className={`shrink-0 px-3.5 py-1.5 rounded-full text-xs font-bold flex items-center gap-1 transition-all border ${
            filter === 'yellow' ? 'bg-[#B90538] text-white border-[#B90538] shadow-sm' : 'bg-white text-slate-800 border-slate-300'
          }`}
        >
          <span className="w-2 h-2 rounded-full bg-amber-500"></span> Level 2 Consult
        </button>
        <button
          onClick={() => setFilter('green')}
          className={`shrink-0 px-3.5 py-1.5 rounded-full text-xs font-bold flex items-center gap-1 transition-all border ${
            filter === 'green' ? 'bg-[#B90538] text-white border-[#B90538] shadow-sm' : 'bg-white text-slate-800 border-slate-300'
          }`}
        >
          <span className="w-2 h-2 rounded-full bg-emerald-600"></span> Level 3 Home
        </button>
      </div>

      {/* 1. RED CARD (EMERGENCY / IMMEDIATE TRANSFER) */}
      {(filter === 'all' || filter === 'red') && (
        <div className="w-full bg-white rounded-2xl p-4 border-2 border-red-600 shadow-lg flex flex-col gap-3 relative overflow-hidden">
          {/* High Alert Urgency Banner */}
          <div className="bg-red-600 text-white p-2 rounded-xl flex items-center gap-2 shadow-xs">
            <span className="material-symbols-outlined text-[18px] animate-pulse">e911_emergency</span>
            <span className="text-[11px] font-extrabold tracking-wide uppercase leading-tight">
              {t?.redCardTitle || "HIGH ALERT: IMMEDIATE HOSPITAL TRANSFER (Pre-Eclampsia / Hemorrhage)"}
            </span>
          </div>

          <div className="flex items-start justify-between">
            <div className="flex items-center gap-2.5">
              <div className="relative shrink-0">
                <img
                  src="https://lh3.googleusercontent.com/aida-public/AB6AXuBQg6p_NeHmCzrVnh5BX2SSMrMhzjTQYui3tFkFIXY8IqS9yswNeg8OwsTbZwbu4oyIojyDw-NpKbZ5Quq_Lefr56FOZWRvDu6CusxKDa7FZlwWDIpZHBvsbZyYFuPUX2YsOFTdVnqza4cTfz3M2JxJz9rdRFIDlNSbKkj8-4ztb3kQ_RiSoJnSojbHYD6ew2cpPB-IC9mFKL0aeTZeTo8fF06AEAryajPqyTU9_MCOjaIbZ7Jof8xEeA"
                  alt="Lakshmi Sundaram"
                  className="w-12 h-12 rounded-full object-cover ring-2 ring-red-400 shadow-sm"
                />
                <span className="absolute -bottom-0.5 -right-0.5 w-4 h-4 rounded-full bg-red-600 flex items-center justify-center text-white text-[10px] font-bold">!</span>
              </div>
              <div>
                <div className="flex items-center gap-1.5">
                  <h3 className="text-sm font-extrabold text-slate-900">Lakshmi Sundaram</h3>
                  <span className="text-xs text-slate-700 font-bold">29y</span>
                </div>
                <div className="flex items-center gap-1 text-xs text-slate-700 font-medium">
                  <span className="material-symbols-outlined text-[13px] text-[#B90538]">location_on</span>
                  <span>Madurai / Bengaluru Rural • 34w Gestation</span>
                </div>
              </div>
            </div>
            <span className="px-2.5 py-1 rounded-full bg-red-600 text-white text-xs font-extrabold uppercase animate-pulse shadow-xs">
              Level 1 Red
            </span>
          </div>

          <div className="bg-rose-50 rounded-xl p-3 border border-red-200 flex flex-col gap-2">
            <div className="flex items-center justify-between">
              <span className="text-xs font-extrabold text-red-700 flex items-center gap-1">
                <span className="material-symbols-outlined text-[16px]">warning</span> Critical Pre-Eclampsia Triad
              </span>
              <span className="text-xs text-white bg-red-600 px-2.5 py-0.5 rounded font-extrabold shadow-xs">160/105 mmHg</span>
            </div>
            <div className="grid grid-cols-2 gap-1.5 text-xs">
              <div className="bg-white p-2 rounded-lg flex items-center gap-1.5 text-slate-900 font-bold border border-rose-200">
                <span className="material-symbols-outlined text-red-600 text-[16px]">visibility_off</span>
                <span>Severe Blurred Vision</span>
              </div>
              <div className="bg-white p-2 rounded-lg flex items-center gap-1.5 text-slate-900 font-bold border border-rose-200">
                <span className="material-symbols-outlined text-red-600 text-[16px]">personal_injury</span>
                <span>Acute Epigastric Pain</span>
              </div>
            </div>
            <p className="text-xs text-slate-900 font-medium leading-relaxed">
              Immediate risk of maternal eclamptic seizures. Emergency Obstetric referral initiated under 100% free JSSK scheme.
            </p>
          </div>

          <div className="grid grid-cols-2 gap-2">
            <a
              href="tel:108"
              className="h-10 bg-red-600 text-white rounded-xl flex items-center justify-center gap-1.5 text-xs font-extrabold shadow-md active:scale-95"
            >
              <span className="material-symbols-outlined text-[18px]">e911_emergency</span>
              <span>{t?.ambulance108 || "108 Ambulance"}</span>
            </a>
            <a
              href="tel:0802221111"
              className="h-10 bg-white text-[#B90538] rounded-xl flex items-center justify-center gap-1.5 text-xs font-extrabold shadow-sm active:scale-95 border-2 border-red-600"
            >
              <span className="material-symbols-outlined text-[18px]">call</span>
              <span>{t?.nearestObIcu || "Nearest OB-ICU"}</span>
            </a>
          </div>

          {/* Low Bandwidth USSD */}
          <div className="bg-slate-100 rounded-xl p-2.5 flex items-center justify-between border border-slate-300">
            <div className="flex items-center gap-2">
              <span className="material-symbols-outlined text-[#B90538] text-[20px]">signal_cellular_connected_no_internet_0_bar</span>
              <div>
                <span className="text-xs font-bold text-slate-900 block">{t?.offlineUssd || "No Internet? Dial Offline USSD"}</span>
                <span className="text-xs text-[#B90538] font-mono font-extrabold">*108*99#</span>
              </div>
            </div>
            <button
              onClick={() => triggerUssd('*108*99#')}
              className="px-3.5 py-1.5 bg-[#B90538] text-white rounded-lg text-xs font-bold active:scale-95 flex items-center gap-1 shadow-sm"
            >
              <span className="material-symbols-outlined text-[14px]">call</span>
              <span>{t?.trigger || "Trigger"}</span>
            </button>
          </div>
        </div>
      )}

      {/* 2. YELLOW CARD (SECONDARY / 24HR CONSULTATION) */}
      {(filter === 'all' || filter === 'yellow') && (
        <div className="w-full bg-white rounded-2xl p-4 border-2 border-amber-500 shadow-md flex flex-col gap-3">
          {/* Moderate Urgency Banner */}
          <div className="bg-amber-100 text-amber-900 p-2 rounded-xl flex items-center gap-2 border border-amber-300">
            <span className="material-symbols-outlined text-[18px] text-amber-700">schedule</span>
            <span className="text-[11px] font-extrabold uppercase leading-tight">
              {t?.yellowCardTitle || "LEVEL 2: CONSULTATION REQUIRED WITHIN 24 HOURS (PCOS / Monitor)"}
            </span>
          </div>

          <div className="flex items-start justify-between">
            <div className="flex items-center gap-2.5">
              <img
                src="https://lh3.googleusercontent.com/aida-public/AB6AXuBFHy0ryAECLWnZ0NZHv05ND-hiEG5_wufHMgo3d0gEgbg_AgUKybwFVEJ7APrktPTzBGTV5HvLsmA1SIYJqHL47_apbiDHM2-0aNE0N6Dt_E3FcuMocyRs8SkPJifiBOrk0SaAgTUu2agRuE4YYJACQLvjsrDrdCUoAhCPJapHqmmS22T3bMZwFWoZ2BkxsUfvFjDbLfpok4bvOMTmXlXWdU3ryDkrzv0PYxzl1SSajcI9uaIPqohuUg"
                alt="Kavya Reddy"
                className="w-12 h-12 rounded-full object-cover ring-2 ring-amber-300 shadow-sm"
              />
              <div>
                <div className="flex items-center gap-1.5">
                  <h3 className="text-sm font-extrabold text-slate-900">Kavya Reddy</h3>
                  <span className="text-xs text-slate-700 font-bold">24y</span>
                </div>
                <p className="text-xs text-slate-700 font-medium">Secunderabad, Hyderabad • ABHA Linked</p>
              </div>
            </div>
            <span className="px-2.5 py-1 rounded-full bg-amber-500 text-white text-xs font-extrabold uppercase shadow-xs">
              Level 2 Consult
            </span>
          </div>

          <div className="bg-slate-50 rounded-xl p-3 text-xs flex flex-col gap-1.5 border border-slate-200">
            <span className="font-extrabold text-slate-900">Severe PCOS Evaluation • Cycle Day 45+</span>
            <p className="text-slate-800 font-medium">
              Debilitating dysmenorrhea cramping (VAS 7/10), unmanaged hormonal cycle irregularity, pelvic ultrasound scan confirmation pending.
            </p>
          </div>

          <button
            onClick={() => showToast('Tele-consultation booked with Gynecologist for 4:30 PM')}
            className="h-10 w-full bg-[#00628D] text-white rounded-xl text-xs font-bold flex items-center justify-center gap-1.5 shadow-md active:scale-95"
          >
            <span className="material-symbols-outlined text-[16px]">videocam</span>
            <span>{t?.bookTeleConsult || "Book Tele-Consult within 24h"}</span>
          </button>

          <button
            onClick={() => setRxOpen(!rxOpen)}
            className="w-full p-2.5 bg-slate-100 rounded-xl text-xs font-bold text-slate-900 flex items-center justify-between border border-slate-200"
          >
            <span>View Antispasmodic & Warm Compress Plan</span>
            <span className="material-symbols-outlined text-[18px]">
              {rxOpen ? 'expand_less' : 'expand_more'}
            </span>
          </button>

          {rxOpen && (
            <div className="p-3 bg-white rounded-xl border border-slate-200 text-xs text-slate-800 space-y-1 font-medium">
              <p>• Dicyclomine 20mg / Mefenamic Acid prescribed as clinically needed for acute spasmodic pain.</p>
              <p>• Apply warm herbal compress with ajwain decoction to alleviate pelvic smooth muscle tension.</p>
            </div>
          )}
        </div>
      )}

      {/* 3. GREEN CARD (SAFE ROUTINE CARE) */}
      {(filter === 'all' || filter === 'green') && (
        <div className="w-full bg-white rounded-2xl p-4 border-2 border-emerald-600 shadow-md flex flex-col gap-3">
          {/* Safe Routine Banner */}
          <div className="bg-emerald-100 text-emerald-900 p-2 rounded-xl flex items-center gap-2 border border-emerald-300">
            <span className="material-symbols-outlined text-[18px] text-emerald-700">check_circle</span>
            <span className="text-[11px] font-extrabold uppercase leading-tight">
              {t?.greenCardTitle || "LEVEL 3: SAFE ROUTINE HOME WELLNESS & POSTPARTUM CARE"}
            </span>
          </div>

          <div className="flex items-start justify-between">
            <div className="flex items-center gap-2.5">
              <img
                src="https://lh3.googleusercontent.com/aida-public/AB6AXuDjQZTuztmcYgsoR0sk1aExJ6rw2WRPIBliVTjf0q3YtoJ2xNcYVMQu9FsSovT4SBjshY9_2-EVB8x8M2L94FTByNphBcnVIZ3SBhkvE6pZYMt39JRBCeQXb3rz50HPdmAJK5DRK5NkcZzs3DqD1aIB5W2D55zTRhHVqj1sDgQ1rrtaB88eABz00TC6AmeEBQ9CeYCeBQhAB1bjPafE208UaV_dNJkXx0FloriN-40SK2nzPsjDeS8mEg"
                alt="Ananya Gowda"
                className="w-12 h-12 rounded-full object-cover ring-2 ring-emerald-400 shadow-sm"
              />
              <div>
                <div className="flex items-center gap-1.5">
                  <h3 className="text-sm font-extrabold text-slate-900">Ananya Gowda</h3>
                  <span className="text-xs text-slate-700 font-bold">31y</span>
                </div>
                <p className="text-xs text-slate-700 font-medium">Kuvempunagar, Mysuru • Day 12 Postpartum</p>
              </div>
            </div>
            <span className="px-2.5 py-1 rounded-full bg-emerald-600 text-white text-xs font-extrabold uppercase shadow-xs">
              Level 3 Home
            </span>
          </div>

          <div className="bg-slate-50 rounded-xl p-3 text-xs flex flex-col gap-1 border border-slate-200">
            <span className="font-extrabold text-[#006C49]">Routine Postnatal Recovery</span>
            <p className="text-slate-800 font-medium">
              Mild bilateral breast engorgement noted on Day 12; latch mechanics adjustment recommended. Vitals stable (BP 118/76 mmHg, afebrile).
            </p>
          </div>

          <div className="grid grid-cols-2 gap-2">
            <button
              onClick={() => showToast('Playing bilingual latch video guide')}
              className="h-10 bg-[#006C49] text-white rounded-xl text-xs font-bold flex items-center justify-center gap-1 active:scale-95 shadow-md"
            >
              <span className="material-symbols-outlined text-[16px]">play_circle</span>
              <span>{t?.latchGuide || "Latch Video Guide"}</span>
            </button>
            <button
              onClick={() => showToast('ASHA post-natal home visit slotted for 7 days')}
              className="h-10 bg-slate-100 text-slate-900 border border-slate-300 rounded-xl text-xs font-bold flex items-center justify-center gap-1 active:scale-95 shadow-sm"
            >
              <span className="material-symbols-outlined text-[16px] text-[#006C49]">event_available</span>
              <span>{t?.check7Days || "Check in 7 Days"}</span>
            </button>
          </div>
        </div>
      )}

      {/* National Women Helpline 181 */}
      <div className="w-full bg-white rounded-2xl p-4 shadow-sm border border-slate-200 flex items-center justify-between">
        <div className="flex items-center gap-2.5">
          <div className="w-9 h-9 rounded-full bg-rose-100 text-[#B90538] flex items-center justify-center shrink-0">
            <span className="material-symbols-outlined text-[18px]">support_agent</span>
          </div>
          <div>
            <span className="text-xs font-extrabold text-slate-900 block">{t?.nationalHelpline || "National Women Helpline"}</span>
            <span className="text-[11px] text-slate-700 font-medium">Free 24x7 Emergency Assistance</span>
          </div>
        </div>
        <a
          href="tel:181"
          className="h-8 px-3.5 bg-[#B90538] text-white rounded-full text-xs font-bold flex items-center gap-1 active:scale-95 shadow-sm"
        >
          <span className="material-symbols-outlined text-[14px]">call</span>
          <span>{t?.dial181 || "Dial 181"}</span>
        </a>
      </div>

      {toastMessage && (
        <div className="fixed bottom-24 inset-x-4 z-50 p-3 rounded-xl bg-slate-900 text-white text-xs flex items-center gap-2 shadow-xl animate-fade-in font-medium">
          <span className="material-symbols-outlined text-[18px] text-emerald-400">check_circle</span>
          <span>{toastMessage}</span>
        </div>
      )}
    </div>
  );
}
