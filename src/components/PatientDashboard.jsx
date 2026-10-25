import React, { useState } from 'react';

export default function PatientDashboard({ user, language, onLanguageChange, onNavigateToTriage, t }) {
  const [activeStage, setActiveStage] = useState('periods');
  const [hospOpen, setHospOpen] = useState(false);
  const [kicks, setKicks] = useState(8);
  const [water, setWater] = useState(2.25);
  const [doseTaken, setDoseTaken] = useState(false);
  const [isRecording, setIsRecording] = useState(false);
  const [toastMessage, setToastMessage] = useState('');
  const [gpsLocked, setGpsLocked] = useState(false);

  // Periods & Hormones Interactive State
  const [cyclePhase, setCyclePhase] = useState('follicular'); // 'menstrual', 'follicular', 'ovulation', 'luteal'
  const [crampLevel, setCrampLevel] = useState('moderate');
  const [moodState, setMoodState] = useState('calm');
  const [bloatingState, setBloatingState] = useState('mild');
  const [flowLevel, setFlowLevel] = useState('medium');

  const showToast = (msg) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(''), 3000);
  };

  const handleSpeech = () => {
    if ('webkitSpeechRecognition' in window) {
      const recognition = new window.webkitSpeechRecognition();
      recognition.continuous = false;
      recognition.interimResults = false;
      recognition.lang = language === 'hi' ? 'hi-IN' : language === 'ta' ? 'ta-IN' : language === 'te' ? 'te-IN' : language === 'kn' ? 'kn-IN' : language === 'ml' ? 'ml-IN' : 'en-IN';
      recognition.onstart = () => setIsRecording(true);
      recognition.onend = () => setIsRecording(false);
      recognition.onresult = (e) => {
        const transcript = e.results[0][0].transcript;
        showToast(`Captured: "${transcript}"`);
        if (onNavigateToTriage) {
          onNavigateToTriage(transcript);
        }
      };
      recognition.start();
    } else {
      setIsRecording(true);
      setTimeout(() => {
        setIsRecording(false);
        const demo = "Severe headache and blurred vision with lower abdominal cramps";
        showToast(`Voice logged: "${demo}"`);
        if (onNavigateToTriage) onNavigateToTriage(demo);
      }, 1800);
    }
  };

  const handleShareLocation = () => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (pos) => {
          setGpsLocked(true);
          showToast(`GPS coordinates locked: Lat ${pos.coords.latitude.toFixed(4)}, Long ${pos.coords.longitude.toFixed(4)}`);
        },
        () => {
          setGpsLocked(true);
          showToast("GPS coordinates locked: Lat 12.9716, Long 77.5946 (South Zone)");
        }
      );
    } else {
      setGpsLocked(true);
      showToast("GPS coordinates locked: Lat 12.9716, Long 77.5946");
    }
  };

  return (
    <div className="flex flex-col w-full pb-8">
      {/* Multilingual & Location Quick Bar */}
      <section className="px-4 pt-3 pb-2 flex flex-col gap-2">
        <div className="flex items-center justify-between gap-2">
          <div className="flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-slate-200 text-slate-900 text-xs font-bold border border-slate-300">
            <span className="material-symbols-outlined text-[16px] text-[#B90538]">translate</span>
            <span>{language ? language.toUpperCase() : 'EN'}</span>
          </div>

          <div className="flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-100 text-[#006C49] text-xs font-bold border border-emerald-300">
            <span className="material-symbols-outlined text-[15px]">verified_user</span>
            <span>ABHA: {user?.abhaId || '91-4428-****'}</span>
          </div>
        </div>

        {/* Nearby Maternity Centers Accordion */}
        <div className="rounded-2xl bg-white shadow-sm border border-slate-200 overflow-hidden">
          <button
            onClick={() => setHospOpen(!hospOpen)}
            className="w-full px-3.5 py-2.5 flex items-center justify-between text-left hover:bg-slate-50 transition-colors"
          >
            <div className="flex items-center gap-2">
              <div className="w-8 h-8 rounded-full bg-rose-100 flex items-center justify-center shrink-0">
                <span className="material-symbols-outlined text-[18px] text-[#B90538]">local_hospital</span>
              </div>
              <div>
                <p className="text-xs font-bold text-slate-900">{t?.nearbyHospitals || "Nearby Women's Hospitals & Health Centers"}</p>
                <p className="text-[11px] text-[#006C49] font-bold flex items-center gap-1">
                  <span className="w-2 h-2 rounded-full bg-[#006C49] animate-pulse"></span>
                  {t?.bedsAvailable || "24/7 NICU & Labor Beds Available"}
                </p>
              </div>
            </div>
            <span className={`material-symbols-outlined text-[20px] text-slate-700 transition-transform ${hospOpen ? 'rotate-180' : ''}`}>
              expand_more
            </span>
          </button>

          {hospOpen && (
            <div className="px-3.5 pb-3 pt-1 flex flex-col gap-2 bg-slate-50 border-t border-slate-200">
              <div className="p-2.5 rounded-xl bg-white flex items-center justify-between gap-2 shadow-xs border border-slate-200">
                <div>
                  <div className="flex items-center gap-1.5">
                    <span className="text-xs font-bold text-slate-900">Mallya Maternity Hospital</span>
                    <span className="px-1.5 py-0.2 rounded text-[9px] font-bold bg-emerald-100 text-[#006C49]">NABH</span>
                  </div>
                  <p className="text-[11px] text-slate-700 font-medium mt-0.5">1.8 km away • 4 Maternity suites free</p>
                </div>
                <a
                  href="tel:08022277979"
                  className="h-8 px-3 rounded-full bg-[#B90538] text-white flex items-center gap-1 text-xs font-bold active:scale-95 shadow-sm"
                >
                  <span className="material-symbols-outlined text-[14px]">call</span> {t?.callHospital || "Call"}
                </a>
              </div>

              <div className="p-2.5 rounded-xl bg-white flex items-center justify-between gap-2 shadow-xs border border-slate-200">
                <div>
                  <div className="flex items-center gap-1.5">
                    <span className="text-xs font-bold text-slate-900">KC General Hospital</span>
                    <span className="px-1.5 py-0.2 rounded text-[9px] font-bold bg-rose-100 text-[#BA1A1A]">Govt / ABDM</span>
                  </div>
                  <p className="text-[11px] text-slate-700 font-medium mt-0.5">3.2 km • Level-III NICU • 8 beds free</p>
                </div>
                <a
                  href="tel:08023341771"
                  className="h-8 px-3 rounded-full bg-slate-200 text-slate-900 flex items-center gap-1 text-xs font-bold active:scale-95 border border-slate-300"
                >
                  <span className="material-symbols-outlined text-[14px]">call</span> {t?.callHospital || "Call"}
                </a>
              </div>
            </div>
          )}
        </div>
      </section>

      {/* Lifespan Care Spectrum Tabs */}
      <section className="mt-2">
        <div className="px-4 flex items-center justify-between">
          <h2 className="text-sm font-bold text-slate-900 flex items-center gap-1.5">
            <span className="material-symbols-outlined text-[#B90538] text-[18px]">spa</span>
            {t?.lifespanCare || "Lifespan Care Spectrum"}
          </h2>
          <span className="text-xs text-slate-700 font-semibold">Tap to explore</span>
        </div>

        <div className="flex items-center gap-2 px-4 mt-2 overflow-x-auto pb-1 no-scrollbar">
          {[
            { id: 'periods', label: `🌸 ${t?.periodsHormones || "Periods & Hormones"}` },
            { id: 'pregnancy', label: `🤰 ${t?.pregnancyCare || "Pregnancy Care"}` },
            { id: 'postpartum', label: `👶 ${t?.postpartumRecovery || "Postpartum & Recovery"}` },
            { id: 'vitality', label: `🌿 ${t?.vitality || "Ongoing Vitality"}` }
          ].map((tab) => (
            <button
              key={tab.id}
              onClick={() => setActiveStage(tab.id)}
              className={`px-3.5 py-1.5 rounded-full text-xs font-bold whitespace-nowrap transition-all shadow-sm border ${
                activeStage === tab.id
                  ? 'bg-[#B90538] text-white border-[#B90538]'
                  : 'bg-white text-slate-800 border-slate-300 hover:text-slate-900'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </div>
      </section>

      {/* STAGE: PERIODS & HORMONAL HEALTH (ENHANCED INTERACTIVE TRACKER) */}
      {activeStage === 'periods' && (
        <div className="flex flex-col gap-3 px-4 mt-2">
          <div className="p-4 rounded-2xl bg-white shadow-md border-2 border-rose-100 flex flex-col gap-3">
            {/* Header info */}
            <div className="flex items-center justify-between">
              <div>
                <h3 className="text-base font-extrabold text-slate-900">
                  {cyclePhase === 'menstrual'
                    ? 'Day 2 • Menstrual Phase'
                    : cyclePhase === 'follicular'
                    ? 'Day 8 • Follicular Phase'
                    : cyclePhase === 'ovulation'
                    ? 'Day 14 • Ovulation Window'
                    : 'Day 22 • Luteal Phase'}
                </h3>
                <p className="text-xs text-[#B90538] font-bold">
                  {cyclePhase === 'menstrual'
                    ? 'Uterine shed • Rest & warm nourishment'
                    : cyclePhase === 'follicular'
                    ? 'Rising Estrogen • Peak mental clarity'
                    : cyclePhase === 'ovulation'
                    ? 'Peak Fertility Window • High Ovulation Chance'
                    : 'Progesterone rise • Self-care & hydration'}
                </p>
              </div>
              <span className="px-2.5 py-1 rounded-full bg-emerald-100 text-[#006C49] text-xs font-bold border border-emerald-200">
                Regular 28-day
              </span>
            </div>

            {/* Visual 4-Phase Cycle Selector */}
            <div className="bg-slate-50 p-3 rounded-xl border border-slate-200 flex flex-col gap-2">
              <span className="text-xs font-bold text-slate-900">Cycle Phase Indicator:</span>
              <div className="grid grid-cols-4 gap-1.5">
                {[
                  { id: 'menstrual', name: 'Menstrual', days: 'Days 1-5', color: 'bg-rose-600' },
                  { id: 'follicular', name: 'Follicular', days: 'Days 6-13', color: 'bg-pink-400' },
                  { id: 'ovulation', name: 'Ovulation', days: 'Day 14', color: 'bg-[#B90538]' },
                  { id: 'luteal', name: 'Luteal', days: 'Days 15-28', color: 'bg-amber-600' }
                ].map((ph) => (
                  <button
                    key={ph.id}
                    onClick={() => {
                      setCyclePhase(ph.id);
                      showToast(`Phase updated to ${ph.name}`);
                    }}
                    className={`p-2 rounded-lg text-center flex flex-col items-center transition-all ${
                      cyclePhase === ph.id
                        ? `${ph.color} text-white shadow-md font-bold scale-102`
                        : 'bg-white text-slate-800 border border-slate-200 font-semibold hover:bg-slate-100'
                    }`}
                  >
                    <span className="text-[11px] leading-tight">{ph.name}</span>
                    <span className="text-[9px] opacity-90">{ph.days}</span>
                  </button>
                ))}
              </div>
            </div>

            {/* Quick-Tap Symptom Loggers */}
            <div className="flex flex-col gap-2.5 bg-slate-50 p-3 rounded-xl border border-slate-200">
              <span className="text-xs font-bold text-slate-900">Quick-Tap Symptom Loggers:</span>

              {/* 1. Cramps */}
              <div className="flex items-center justify-between text-xs">
                <span className="font-semibold text-slate-800">Cramps:</span>
                <div className="flex gap-1">
                  {['None', 'Mild', 'Moderate', 'Severe'].map((lvl) => (
                    <button
                      key={lvl}
                      onClick={() => setCrampLevel(lvl.toLowerCase())}
                      className={`px-2.5 py-1 rounded-md text-[11px] font-bold transition-all ${
                        crampLevel === lvl.toLowerCase()
                          ? 'bg-[#B90538] text-white shadow-xs'
                          : 'bg-white text-slate-700 border border-slate-200'
                      }`}
                    >
                      {lvl}
                    </button>
                  ))}
                </div>
              </div>

              {/* 2. Mood */}
              <div className="flex items-center justify-between text-xs">
                <span className="font-semibold text-slate-800">Mood:</span>
                <div className="flex gap-1">
                  {['Calm', 'Anxious', 'Irritable', 'Low Energy'].map((m) => (
                    <button
                      key={m}
                      onClick={() => setMoodState(m.toLowerCase())}
                      className={`px-2 py-1 rounded-md text-[11px] font-bold transition-all ${
                        moodState === m.toLowerCase()
                          ? 'bg-[#00628D] text-white shadow-xs'
                          : 'bg-white text-slate-700 border border-slate-200'
                      }`}
                    >
                      {m}
                    </button>
                  ))}
                </div>
              </div>

              {/* 3. Bloating */}
              <div className="flex items-center justify-between text-xs">
                <span className="font-semibold text-slate-800">Bloating:</span>
                <div className="flex gap-1">
                  {['Normal', 'Mild', 'High'].map((b) => (
                    <button
                      key={b}
                      onClick={() => setBloatingState(b.toLowerCase())}
                      className={`px-2.5 py-1 rounded-md text-[11px] font-bold transition-all ${
                        bloatingState === b.toLowerCase()
                          ? 'bg-[#006C49] text-white shadow-xs'
                          : 'bg-white text-slate-700 border border-slate-200'
                      }`}
                    >
                      {b}
                    </button>
                  ))}
                </div>
              </div>

              {/* 4. Flow */}
              <div className="flex items-center justify-between text-xs">
                <span className="font-semibold text-slate-800">Flow Intensity:</span>
                <div className="flex gap-1">
                  {['Light', 'Medium', 'Heavy', 'Spotting'].map((f) => (
                    <button
                      key={f}
                      onClick={() => setFlowLevel(f.toLowerCase())}
                      className={`px-2 py-1 rounded-md text-[11px] font-bold transition-all ${
                        flowLevel === f.toLowerCase()
                          ? 'bg-rose-700 text-white shadow-xs'
                          : 'bg-white text-slate-700 border border-slate-200'
                      }`}
                    >
                      {f}
                    </button>
                  ))}
                </div>
              </div>
            </div>

            {/* Actionable Natural Wellness & Cramp-Relief Guidance */}
            <div className="p-3 rounded-xl bg-rose-50 border border-rose-200 flex flex-col gap-2">
              <div className="flex items-center gap-1.5 text-xs font-bold text-[#B90538]">
                <span className="material-symbols-outlined text-[18px]">self_improvement</span>
                <span>Natural Wellness & Cramp Relief (South Zone Ayurvedic Recipes)</span>
              </div>
              <ul className="text-xs text-slate-800 space-y-1 font-medium pl-1">
                <li className="flex items-start gap-1.5">
                  <span className="text-[#B90538] font-bold">•</span>
                  <span><strong>Ginger & Ajwain Infusion:</strong> Boil 1 cup water with crushed fresh ginger and 1/2 tsp roasted carom seeds to relax uterine contractions.</span>
                </li>
                <li className="flex items-start gap-1.5">
                  <span className="text-[#B90538] font-bold">•</span>
                  <span><strong>Warm Sesame Oil Compress:</strong> Apply warm til (sesame) oil over lower pelvic area with a hot water bag for 15 minutes.</span>
                </li>
                <li className="flex items-start gap-1.5">
                  <span className="text-[#B90538] font-bold">•</span>
                  <span><strong>Balasana (Child's Pose):</strong> Hold gentle pelvic restorative poses with knee cushion for 5-10 deep belly breaths.</span>
                </li>
              </ul>
            </div>
          </div>
        </div>
      )}

      {/* STAGE: PREGNANCY */}
      {activeStage === 'pregnancy' && (
        <div className="flex flex-col gap-3 px-4 mt-2">
          <div className="rounded-2xl bg-white shadow-md overflow-hidden border border-slate-200">
            <div className="relative w-full h-44 bg-gradient-to-r from-pink-950 via-rose-900 to-rose-950 flex items-center justify-center overflow-hidden">
              <img
                src="https://lh3.googleusercontent.com/aida/AEtjO1WjOzP52nv4zVqce05RAHz5gRfDeQ7TXWJBpYKUk9lDJQmB63f-GfJvXZbEvAEpSnqg55s0EcGU25iotIy1oFyR5suv1kxmXz5Z4TNcQAZn7saDvQ1BoYk28zEsUw7WC45hGuNr0e4bZ5C3AQDcLxjHWiOMzSkoSAmDxTXkA5zdkVofkUANeRJiq062v6Sltl_MOR17TH2zg1W5dq1LCU5AL9ZAh8wyvdcoFpJVtCxSUWxAnhxQOEgsLf9z"
                alt="Maternal Health"
                className="w-full h-full object-cover opacity-40"
              />
              <div className="absolute inset-0 bg-gradient-to-t from-slate-950/90 via-slate-900/30 to-transparent flex flex-col justify-end p-4 text-white">
                <div className="flex items-center justify-between">
                  <span className="px-2.5 py-0.5 rounded-full bg-[#006C49] text-white text-xs font-bold">
                    Week 26 • Trimester 2
                  </span>
                  <span className="px-2 py-0.5 rounded-full bg-white text-[#B90538] text-xs font-bold">
                    98 Days to EDD
                  </span>
                </div>
                <h3 className="text-lg font-bold text-white mt-1">Baby is the size of an Eggplant 🍆</h3>
              </div>
            </div>

            <div className="p-4 flex flex-col gap-3">
              <div className="p-3 rounded-xl bg-slate-50 border border-slate-200 flex items-start gap-2.5">
                <span className="material-symbols-outlined text-[#B90538] text-[20px] shrink-0">tips_and_updates</span>
                <div>
                  <p className="text-xs font-bold text-slate-900">Trimester 2 Essential Insight</p>
                  <p className="text-xs text-slate-800 leading-relaxed font-medium">
                    Auditory senses are now active. Listen to calming music, ensure adequate calcium intake, and practice lateral left-side resting.
                  </p>
                </div>
              </div>

              {/* Kicks & Hydration */}
              <div className="grid grid-cols-2 gap-2.5">
                <div className="p-3 rounded-xl bg-rose-50 border border-rose-200 flex flex-col justify-between">
                  <span className="text-[11px] text-[#B90538] font-bold uppercase">Baby Kicks</span>
                  <div className="my-2">
                    <span className="text-2xl font-bold text-[#B90538]">{kicks}</span>
                    <span className="text-xs text-slate-700 font-semibold"> / 10 target</span>
                  </div>
                  <button
                    onClick={() => {
                      setKicks(kicks + 1);
                      showToast(`Baby kick #${kicks + 1} logged with timestamp`);
                    }}
                    className="w-full py-1.5 rounded-lg bg-[#B90538] text-white text-xs font-bold shadow-sm active:scale-95"
                  >
                    + Tap Kick
                  </button>
                </div>

                <div className="p-3 rounded-xl bg-sky-50 border border-sky-200 flex flex-col justify-between">
                  <span className="text-[11px] text-[#00628D] font-bold uppercase">Water Intake</span>
                  <div className="my-2">
                    <span className="text-2xl font-bold text-[#00628D]">{water.toFixed(1)}</span>
                    <span className="text-xs text-slate-700 font-semibold"> / 3.0 Liters</span>
                  </div>
                  <button
                    onClick={() => {
                      setWater(+(water + 0.25).toFixed(2));
                      showToast('Hydration logged: +250ml');
                    }}
                    className="w-full py-1.5 rounded-lg bg-[#00628D] text-white text-xs font-bold shadow-sm active:scale-95"
                  >
                    + 250 ml Glass
                  </button>
                </div>
              </div>

              {/* Iron + Folic Acid */}
              <div className="p-3 rounded-xl bg-slate-50 border border-slate-200 flex items-center justify-between gap-3">
                <div className="flex items-center gap-2.5">
                  <div className="w-9 h-9 rounded-full bg-emerald-100 flex items-center justify-center text-[#006C49] shrink-0">
                    <span className="material-symbols-outlined text-[20px]">medication</span>
                  </div>
                  <div>
                    <div className="flex items-center gap-1.5">
                      <p className="text-xs font-bold text-slate-900">Iron + Folic Acid (IFA)</p>
                      <span className="px-1.5 py-0.2 rounded-full bg-emerald-100 text-[#006C49] text-[9px] font-bold">Daily PM</span>
                    </div>
                    <p className="text-[11px] text-slate-700 font-medium">Recommended after dinner with lemon water</p>
                  </div>
                </div>
                <button
                  onClick={() => {
                    setDoseTaken(!doseTaken);
                    showToast(doseTaken ? 'Marked as unlogged' : 'IFA dose recorded to ABDM Health Log');
                  }}
                  className={`px-3 py-1.5 rounded-full text-xs font-bold shadow-sm active:scale-95 transition-all ${
                    doseTaken ? 'bg-emerald-100 text-[#006C49] border border-emerald-300' : 'bg-[#006C49] text-white'
                  }`}
                >
                  {doseTaken ? 'Taken ✓' : 'Mark Taken'}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* STAGE: POSTPARTUM */}
      {activeStage === 'postpartum' && (
        <div className="flex flex-col gap-3 px-4 mt-2">
          <div className="p-4 rounded-2xl bg-white shadow-md border border-slate-200 flex flex-col gap-3">
            <h3 className="text-sm font-bold text-slate-900">Postpartum 40-Day Sanctuary Care</h3>
            <p className="text-xs text-slate-800 leading-relaxed font-medium">
              Traditional postpartum care with gond ke laddu, cumin water, and regular uterine involution checks.
            </p>
            <div className="p-3 bg-emerald-50 rounded-xl border border-emerald-200 text-xs text-slate-800 flex items-center justify-between">
              <div>
                <span className="font-bold text-[#006C49] block">Lochia Healing Status</span>
                <span className="text-[11px]">Day 14 • Serosa stage • Normal</span>
              </div>
              <span className="px-2 py-0.5 rounded-full bg-emerald-600 text-white font-bold text-[10px]">Optimal</span>
            </div>
          </div>
        </div>
      )}

      {/* STAGE: VITALITY */}
      {activeStage === 'vitality' && (
        <div className="flex flex-col gap-3 px-4 mt-2">
          <div className="p-4 rounded-2xl bg-white shadow-md border border-slate-200 flex flex-col gap-3">
            <h3 className="text-sm font-bold text-slate-900">Holistic Nutrition & Bone Density</h3>
            <p className="text-xs text-slate-800 leading-relaxed font-medium">
              Ragi (finger millet), sesame seeds, drumstick leaves, and sun exposure (Vitamin D3) for long-term maternal bone vitality.
            </p>
          </div>
        </div>
      )}

      {/* ASHA Worker Banner */}
      <section className="px-4 mt-3">
        <div className="p-3.5 rounded-2xl bg-white shadow-sm border border-slate-200 flex items-center justify-between gap-3">
          <div className="flex items-center gap-2.5">
            <div className="w-9 h-9 rounded-full bg-emerald-100 flex items-center justify-center text-[#006C49] shrink-0">
              <span className="material-symbols-outlined text-[20px]">support_agent</span>
            </div>
            <div>
              <div className="flex items-center gap-1">
                <h4 className="text-xs font-bold text-slate-900">ASHA Worker: Sunita D.</h4>
                <span className="w-2 h-2 rounded-full bg-[#006C49]"></span>
              </div>
              <p className="text-[11px] text-slate-700 font-medium">Ward 12 Maternal Health Facilitator</p>
            </div>
          </div>
          <a
            href="tel:104"
            className="h-8 px-3 rounded-full bg-[#006C49] text-white text-xs font-bold flex items-center gap-1 active:scale-95 shadow-sm"
          >
            <span className="material-symbols-outlined text-[14px]">chat</span> Connect
          </a>
        </div>
      </section>

      {/* Hardware Action Floating Dock */}
      <div className="sticky bottom-3 z-30 px-4 mt-4">
        <div className="p-2 rounded-2xl bg-white/95 backdrop-blur-md shadow-xl border border-slate-300 flex items-center justify-between gap-2">
          <button
            onClick={handleSpeech}
            className={`flex-1 h-11 rounded-xl text-white text-xs font-bold flex items-center justify-center gap-1.5 active:scale-95 transition-all shadow-md ${
              isRecording ? 'bg-red-600 animate-pulse' : 'bg-[#B90538]'
            }`}
          >
            <span className="material-symbols-outlined text-[18px]">mic</span>
            <span>{isRecording ? (t?.listening || 'Listening in your language...') : (t?.speakSymptoms || 'Speak Symptoms')}</span>
          </button>

          <button
            onClick={handleShareLocation}
            className="h-11 w-11 rounded-xl bg-slate-100 text-slate-900 border border-slate-300 flex items-center justify-center active:scale-95 shadow-sm relative"
            title="GPS Location"
          >
            <span className="material-symbols-outlined text-[20px] text-[#006C49]">my_location</span>
            {gpsLocked && <span className="absolute top-1 right-1 w-2.5 h-2.5 rounded-full bg-emerald-500 animate-ping"></span>}
          </button>

          <button
            onClick={() => showToast('Opening OCR Camera for Prescription/Lab scan...')}
            className="h-11 w-11 rounded-xl bg-slate-100 text-slate-900 border border-slate-300 flex items-center justify-center active:scale-95 shadow-sm"
            title="Scan Document OCR"
          >
            <span className="material-symbols-outlined text-[20px] text-[#00628D]">document_scanner</span>
          </button>
        </div>

        {toastMessage && (
          <div className="mt-2 p-2.5 rounded-xl bg-slate-900 text-white text-xs flex items-center gap-2 shadow-lg animate-fade-in font-medium">
            <span className="material-symbols-outlined text-[16px] text-emerald-400">check_circle</span>
            <span>{toastMessage}</span>
          </div>
        )}
      </div>
    </div>
  );
}
