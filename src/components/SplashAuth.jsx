import React, { useState } from 'react';

export default function SplashAuth({ onLogin, activeRole, onRoleChange, language, onLanguageChange, t }) {
  const [mobile, setMobile] = useState('');
  const [abhaId, setAbhaId] = useState('');
  const [otpChannel, setOtpChannel] = useState('whatsapp');
  const [ashaId, setAshaId] = useState('MH-PUN-ASHA-0491');
  const [wardPin, setWardPin] = useState('Village PHC Ward 12');
  const [doctorEmail, setDoctorEmail] = useState('doctor@heracare.org');
  const [doctorPin, setDoctorPin] = useState('123456');
  const [otpSent, setOtpSent] = useState(false);
  const [isListeningVoice, setIsListeningVoice] = useState(false);

  const languages = [
    { code: 'en', label: 'English' },
    { code: 'hi', label: 'हिंदी' },
    { code: 'ta', label: 'தமிழ்' },
    { code: 'te', label: 'తెలుగు' },
    { code: 'kn', label: 'ಕನ್ನಡ' },
    { code: 'ml', label: 'മലയാളം' }
  ];

  const handleSendOtp = (e) => {
    e.preventDefault();
    setOtpSent(true);
    setTimeout(() => {
      onLogin({
        role: 'patient',
        identifier: mobile || '+91 98452 11094',
        name: 'Priya Sharma',
        abhaId: abhaId || '91-4428-8921-1049'
      });
    }, 1000);
  };

  const handleAshaSubmit = (e) => {
    e.preventDefault();
    onLogin({
      role: 'asha',
      identifier: ashaId,
      name: 'Sunita Devi (ASHA)',
      ward: wardPin
    });
  };

  const handleDoctorSubmit = (e) => {
    e.preventDefault();
    onLogin({
      role: 'doctor',
      identifier: doctorEmail,
      name: 'Dr. Soundarya Ramesh',
      regNo: 'KMC-48291'
    });
  };

  const startVoiceInput = () => {
    if ('webkitSpeechRecognition' in window) {
      const recognition = new window.webkitSpeechRecognition();
      recognition.lang = language === 'hi' ? 'hi-IN' : language === 'ta' ? 'ta-IN' : language === 'te' ? 'te-IN' : language === 'kn' ? 'kn-IN' : language === 'ml' ? 'ml-IN' : 'en-IN';
      recognition.onstart = () => setIsListeningVoice(true);
      recognition.onend = () => setIsListeningVoice(false);
      recognition.onresult = (event) => {
        const transcript = event.results[0][0].transcript;
        const numbers = transcript.replace(/\D/g, '').slice(-10);
        if (numbers) setMobile(numbers);
      };
      recognition.start();
    } else {
      setIsListeningVoice(true);
      setTimeout(() => {
        setMobile('9845211094');
        setIsListeningVoice(false);
      }, 1200);
    }
  };

  return (
    <div className="flex flex-col w-full pb-8">
      {/* Multilingual Selector Strip */}
      <div className="px-4 pt-2 pb-2">
        <div className="flex items-center gap-1.5 overflow-x-auto no-scrollbar py-1">
          <div className="flex items-center gap-1 shrink-0 px-2.5 py-1 rounded-full bg-slate-200 text-slate-900 font-bold">
            <span className="material-symbols-outlined text-[16px] text-[#B90538]">translate</span>
            <span className="text-xs">भाषा:</span>
          </div>
          {languages.map((l) => (
            <button
              key={l.code}
              onClick={() => onLanguageChange(l.code)}
              className={`shrink-0 px-3 py-1 rounded-full text-xs font-bold transition-all ${
                language === l.code
                  ? 'bg-[#B90538] text-white shadow-sm'
                  : 'bg-white text-slate-800 border border-slate-300 hover:bg-slate-100'
              }`}
            >
              {l.label}
            </button>
          ))}
        </div>
      </div>

      {/* Hero Card */}
      <div className="px-4 mb-4">
        <div className="relative w-full overflow-hidden rounded-2xl bg-white shadow-md border border-slate-200">
          <div className="relative h-48 w-full bg-gradient-to-r from-rose-950 via-rose-900 to-pink-900 flex items-center justify-center overflow-hidden">
            <img
              src="https://lh3.googleusercontent.com/aida/AEtjO1WjOzP52nv4zVqce05RAHz5gRfDeQ7TXWJBpYKUk9lDJQmB63f-GfJvXZbEvAEpSnqg55s0EcGU25iotIy1oFyR5suv1kxmXz5Z4TNcQAZn7saDvQ1BoYk28zEsUw7WC45hGuNr0e4bZ5C3AQDcLxjHWiOMzSkoSAmDxTXkA5zdkVofkUANeRJiq062v6Sltl_MOR17TH2zg1W5dq1LCU5AL9ZAh8wyvdcoFpJVtCxSUWxAnhxQOEgsLf9z"
              alt="Shakthi.ai Hero"
              className="absolute inset-0 w-full h-full object-cover opacity-35"
            />
            <div className="absolute inset-0 bg-gradient-to-t from-slate-950/95 via-slate-900/50 to-transparent"></div>
            <div className="absolute top-3 left-3 flex items-center gap-1.5 px-3 py-1 rounded-full bg-white shadow-md">
              <span className="w-2.5 h-2.5 rounded-full bg-[#006C49] animate-pulse"></span>
              <span className="text-xs text-slate-900 font-bold tracking-wide">{t.ayushmanReady}</span>
            </div>
            <div className="absolute bottom-3 inset-x-3">
              <div className="flex items-center gap-1 text-pink-300 mb-0.5">
                <span className="material-symbols-outlined text-[16px]">favorite</span>
                <span className="text-xs font-bold tracking-wide uppercase">{t.caringMilestone}</span>
              </div>
              <p className="text-base text-white leading-tight font-extrabold drop-shadow-sm">
                {t.tagline}
              </p>
            </div>
          </div>

          {/* Quick Trust Metric Ribbon */}
          <div className="grid grid-cols-3 gap-1 p-3 bg-slate-100 text-center border-t border-slate-200">
            <div className="flex flex-col items-center">
              <span className="text-base text-[#B90538] font-extrabold">2.4M+</span>
              <span className="text-xs text-slate-700 font-semibold">{t.womenCared}</span>
            </div>
            <div className="flex flex-col items-center">
              <span className="text-base text-[#006C49] font-extrabold">14,200</span>
              <span className="text-xs text-slate-700 font-semibold">{t.ashasActive}</span>
            </div>
            <div className="flex flex-col items-center">
              <span className="text-base text-[#00628D] font-extrabold">100%</span>
              <span className="text-xs text-slate-700 font-semibold">{t.encryptedSafe}</span>
            </div>
          </div>
        </div>
      </div>

      {/* Role-Based Interactive Authentication Container */}
      <div className="px-4 mb-4">
        <div className="bg-white rounded-2xl shadow-md p-4 border border-slate-200">
          {/* Segmented Role Switcher */}
          <div className="flex p-1 rounded-xl bg-slate-100 mb-4 gap-1 border border-slate-200">
            <button
              onClick={() => onRoleChange('patient')}
              className={`flex-1 py-2 px-1 rounded-lg text-xs font-bold transition-all flex items-center justify-center gap-1.5 ${
                activeRole === 'patient'
                  ? 'bg-white text-[#B90538] shadow-sm border border-slate-200'
                  : 'text-slate-700 hover:text-slate-900'
              }`}
            >
              <span>🌸</span>
              <span>{t.patient}</span>
            </button>
            <button
              onClick={() => onRoleChange('asha')}
              className={`flex-1 py-2 px-1 rounded-lg text-xs font-bold transition-all flex items-center justify-center gap-1.5 ${
                activeRole === 'asha'
                  ? 'bg-white text-[#006C49] shadow-sm border border-slate-200'
                  : 'text-slate-700 hover:text-slate-900'
              }`}
            >
              <span>🥻</span>
              <span>{t.ashaDidi}</span>
            </button>
            <button
              onClick={() => onRoleChange('doctor')}
              className={`flex-1 py-2 px-1 rounded-lg text-xs font-bold transition-all flex items-center justify-center gap-1.5 ${
                activeRole === 'doctor'
                  ? 'bg-white text-[#00628D] shadow-sm border border-slate-200'
                  : 'text-slate-700 hover:text-slate-900'
              }`}
            >
              <span>🩺</span>
              <span>{t.doctor}</span>
            </button>
          </div>

          {/* TAB 1: PATIENT */}
          {activeRole === 'patient' && (
            <div className="flex flex-col gap-3">
              <div className="flex items-start gap-2.5 bg-rose-50 p-3 rounded-xl border border-rose-200">
                <span className="material-symbols-outlined text-[#B90538] text-[20px] shrink-0 mt-0.5">verified_user</span>
                <div>
                  <h4 className="text-sm font-bold text-slate-900">Seamless ABHA-Linked Entry</h4>
                  <p className="text-xs text-slate-700 mt-0.5">Sign in securely using your 10-digit mobile number or 14-digit ABHA Health Card.</p>
                </div>
              </div>

              <form onSubmit={handleSendOtp} className="flex flex-col gap-3">
                <div>
                  <label className="text-xs text-slate-900 font-bold block mb-1">
                    {t.mobileNumber}
                  </label>
                  <div className="flex items-center rounded-xl bg-slate-50 px-3 border-2 border-slate-300 focus-within:border-[#B90538]">
                    <span className="text-xs text-slate-900 font-extrabold pr-2 shrink-0">🇮🇳 +91</span>
                    <input
                      type="tel"
                      value={mobile}
                      onChange={(e) => setMobile(e.target.value)}
                      placeholder="9845211094"
                      maxLength="10"
                      className="w-full py-2.5 bg-transparent text-sm text-slate-900 font-semibold outline-none"
                    />
                    <button
                      type="button"
                      onClick={startVoiceInput}
                      className="text-slate-700 hover:text-[#B90538] shrink-0"
                      title="Speak number"
                    >
                      <span className="material-symbols-outlined text-[18px]">mic</span>
                    </button>
                  </div>
                  {isListeningVoice && (
                    <span className="text-[11px] text-[#B90538] font-bold mt-1 block animate-pulse">
                      {t.listening}
                    </span>
                  )}
                </div>

                <div>
                  <div className="flex justify-between items-center mb-1">
                    <label className="text-xs text-slate-900 font-bold">
                      {t.abhaId} (Optional)
                    </label>
                    <span className="text-[11px] text-[#00628D] font-bold underline cursor-pointer">{t.findAbha}</span>
                  </div>
                  <div className="flex items-center rounded-xl bg-slate-50 px-3 border border-slate-300">
                    <span className="material-symbols-outlined text-[18px] text-slate-600 pr-2">badge</span>
                    <input
                      type="text"
                      value={abhaId}
                      onChange={(e) => setAbhaId(e.target.value)}
                      placeholder="91-4428-8921-1049"
                      className="w-full py-2.5 bg-transparent text-sm text-slate-900 font-medium outline-none"
                    />
                  </div>
                </div>

                <div className="flex items-center justify-between py-1 text-slate-800">
                  <span className="text-xs font-semibold">{t.deliveringOtp}</span>
                  <div className="flex gap-2">
                    <label className="flex items-center gap-1.5 cursor-pointer px-2.5 py-1 rounded-full bg-slate-100 text-xs font-bold border border-slate-200">
                      <input
                        type="radio"
                        name="otp_channel"
                        value="whatsapp"
                        checked={otpChannel === 'whatsapp'}
                        onChange={() => setOtpChannel('whatsapp')}
                      />
                      <span className="text-[#006C49]">WhatsApp</span>
                    </label>
                    <label className="flex items-center gap-1.5 cursor-pointer px-2.5 py-1 rounded-full bg-slate-100 text-xs font-bold border border-slate-200">
                      <input
                        type="radio"
                        name="otp_channel"
                        value="sms"
                        checked={otpChannel === 'sms'}
                        onChange={() => setOtpChannel('sms')}
                      />
                      <span className="text-slate-800">SMS</span>
                    </label>
                  </div>
                </div>

                <button
                  type="submit"
                  className="w-full py-3 rounded-xl bg-[#B90538] text-white text-xs font-bold tracking-wide active:scale-95 shadow-md flex items-center justify-center gap-1.5"
                >
                  <span className="material-symbols-outlined text-[18px]">lock</span>
                  <span>{otpSent ? 'Signing In...' : t.sendOtp}</span>
                </button>
              </form>
            </div>
          )}

          {/* TAB 2: ASHA DIDI */}
          {activeRole === 'asha' && (
            <div className="flex flex-col gap-3">
              <div className="flex items-start gap-2 bg-emerald-50 p-3 rounded-xl border border-emerald-200">
                <span className="material-symbols-outlined text-[#006C49] text-[20px] shrink-0 mt-0.5">group_work</span>
                <div>
                  <h4 className="text-sm font-bold text-slate-900">ASHA Field Worker Console</h4>
                  <p className="text-xs text-slate-700">Multi-patient offline logging & doorstep antenatal visits.</p>
                </div>
              </div>

              <form onSubmit={handleAshaSubmit} className="flex flex-col gap-3">
                <div>
                  <label className="text-xs text-slate-900 font-bold block mb-1">ASHA ID / आशा आईडी</label>
                  <input
                    type="text"
                    value={ashaId}
                    onChange={(e) => setAshaId(e.target.value)}
                    className="w-full py-2.5 px-3 rounded-xl bg-slate-50 border border-slate-300 text-sm text-slate-900 font-semibold outline-none"
                  />
                </div>
                <div>
                  <label className="text-xs text-slate-900 font-bold block mb-1">Assigned Ward / पीएचसी वार्ड</label>
                  <input
                    type="text"
                    value={wardPin}
                    onChange={(e) => setWardPin(e.target.value)}
                    className="w-full py-2.5 px-3 rounded-xl bg-slate-50 border border-slate-300 text-sm text-slate-900 font-semibold outline-none"
                  />
                </div>
                <button
                  type="submit"
                  className="w-full py-3 rounded-xl bg-[#006C49] text-white text-xs font-bold active:scale-95 shadow-md"
                >
                  Enter ASHA Field Hub
                </button>
              </form>
            </div>
          )}

          {/* TAB 3: DOCTOR */}
          {activeRole === 'doctor' && (
            <div className="flex flex-col gap-3">
              <div className="flex items-start gap-2 bg-sky-50 p-3 rounded-xl border border-sky-200">
                <span className="material-symbols-outlined text-[#00628D] text-[20px] shrink-0 mt-0.5">medical_services</span>
                <div>
                  <h4 className="text-sm font-bold text-slate-900">Doctor & Clinician Gate</h4>
                  <p className="text-xs text-slate-700">Strictly for certified OB/GYN clinicians with active NMC/State registration.</p>
                </div>
              </div>

              <form onSubmit={handleDoctorSubmit} className="flex flex-col gap-3">
                <div>
                  <label className="text-xs text-slate-900 font-bold block mb-1">Doctor Email</label>
                  <input
                    type="email"
                    value={doctorEmail}
                    onChange={(e) => setDoctorEmail(e.target.value)}
                    className="w-full py-2.5 px-3 rounded-xl bg-slate-50 border border-slate-300 text-sm text-slate-900 font-semibold outline-none"
                  />
                </div>
                <div>
                  <label className="text-xs text-slate-900 font-bold block mb-1">Security PIN</label>
                  <input
                    type="password"
                    value={doctorPin}
                    onChange={(e) => setDoctorPin(e.target.value)}
                    className="w-full py-2.5 px-3 rounded-xl bg-slate-50 border border-slate-300 text-sm text-slate-900 font-semibold outline-none"
                  />
                </div>
                <button
                  type="submit"
                  className="w-full py-3 rounded-xl bg-[#00628D] text-white text-xs font-bold active:scale-95 shadow-md"
                >
                  Access Clinical OPD Queue
                </button>
              </form>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
