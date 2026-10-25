import React, { useState } from 'react';
import SplashAuth from './components/SplashAuth';
import PatientDashboard from './components/PatientDashboard';
import TriageCanvas from './components/TriageCanvas';
import DoctorPortal from './components/DoctorPortal';
import { translations } from './utils/translations';

export default function App() {
  const [currentTab, setCurrentTab] = useState('home-auth');
  const [user, setUser] = useState({
    role: 'patient',
    identifier: '+91 98452 11094',
    name: 'Priya Sharma',
    abhaId: '91-4428-8921-1049',
    isAuthenticated: true
  });
  const [language, setLanguage] = useState('en');
  const [incomingSymptom, setIncomingSymptom] = useState('');

  const t = translations[language] || translations.en;

  const handleLogin = (newUser) => {
    setUser({ ...newUser, isAuthenticated: true });
    if (newUser.role === 'doctor') {
      setCurrentTab('clinical-portal');
    } else {
      setCurrentTab('lifespan-dashboard');
    }
  };

  const handleRoleChange = (role) => {
    setUser((prev) => ({ ...prev, role }));
  };

  return (
    <div className="bg-[#F8F9FF] min-h-screen text-[#0B1C30] flex flex-col font-sans selection:bg-pink-200">
      {/* Top Header */}
      <header className="fixed top-0 inset-x-0 z-50 bg-[#F8F9FF]/95 backdrop-blur-md shadow-xs border-b border-slate-200">
        <div className="max-w-md mx-auto h-16 px-4 flex items-center justify-between">
          <div className="flex items-center gap-2.5">
            <img
              src="https://lh3.googleusercontent.com/aida/AEtjO1WjOzP52nv4zVqce05RAHz5gRfDeQ7TXWJBpYKUk9lDJQmB63f-GfJvXZbEvAEpSnqg55s0EcGU25iotIy1oFyR5suv1kxmXz5Z4TNcQAZn7saDvQ1BoYk28zEsUw7WC45hGuNr0e4bZ5C3AQDcLxjHWiOMzSkoSAmDxTXkA5zdkVofkUANeRJiq062v6Sltl_MOR17TH2zg1W5dq1LCU5AL9ZAh8wyvdcoFpJVtCxSUWxAnhxQOEgsLf9z"
              alt="Shakthi.ai Logo"
              className="h-8 w-auto object-contain rounded-full border border-slate-200"
            />
            <div className="flex flex-col">
              <div className="flex items-center gap-1.5">
                <span className="text-xs text-[#B90538] font-bold tracking-wider uppercase">{t.appName}</span>
                <span className="px-1.5 py-0.2 rounded-full bg-emerald-100 text-[#006C49] text-[9px] font-bold">ABDM</span>
              </div>
              <span className="text-xs font-bold text-slate-900 capitalize">
                {currentTab === 'home-auth'
                  ? t.home
                  : currentTab === 'lifespan-dashboard'
                  ? t.dashboard
                  : currentTab === 'emergency-triage'
                  ? t.triage
                  : t.portal}
              </span>
            </div>
          </div>

          <div className="flex items-center gap-2">
            <a
              href="tel:108"
              aria-label="Emergency SOS Call 108"
              className="h-9 px-3 bg-[#BA1A1A] text-white rounded-full flex items-center gap-1 shadow-sm active:scale-95 text-xs font-bold"
            >
              <span className="material-symbols-outlined text-[16px]">e911_emergency</span>
              <span>108 SOS</span>
            </a>

            {/* Profile Avatar with Role Ring */}
            <div className="relative">
              <img
                src="https://lh3.googleusercontent.com/aida-public/AB6AXuDFWItI3uxOIDrcLAQHSRi3i1C8FVv4EIPrTGlUtJNiykAdRGZ931OnsuAqBLuYdxmKfsWohybWTi5SV7URcEgWzQQl3h9B1Bvh-EjSOdI4CJZmpdDecGvqCo9xACmeKdH-17e-Hk-q6wIBk18huB7dmB6ehLXtgPNiMgLc1zKZlvoBPqqT4O-eVZb48SYORDza_QDq1iNrYZ-CehLgA1Kn4EnyupQnsvJJTOsNbVy1csALr_-7HlxWmQ"
                alt="Profile"
                className="w-8 h-8 rounded-full object-cover ring-2 ring-white shadow-xs"
              />
              <span className={`absolute bottom-0 right-0 w-2.5 h-2.5 rounded-full ring-2 ring-white ${
                user.role === 'doctor' ? 'bg-[#00628D]' : user.role === 'asha' ? 'bg-[#006C49]' : 'bg-[#B90538]'
              }`}></span>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content Area - Generous safe area bottom padding to prevent mobile clipping */}
      <main className="flex-1 w-full pt-16 pb-28 max-w-md mx-auto">
        {currentTab === 'home-auth' && (
          <SplashAuth
            onLogin={handleLogin}
            activeRole={user.role}
            onRoleChange={handleRoleChange}
            language={language}
            onLanguageChange={setLanguage}
            t={t}
          />
        )}

        {currentTab === 'lifespan-dashboard' && (
          <PatientDashboard
            user={user}
            language={language}
            onLanguageChange={setLanguage}
            t={t}
            onNavigateToTriage={(symptom) => {
              setIncomingSymptom(symptom);
              setCurrentTab('emergency-triage');
            }}
          />
        )}

        {currentTab === 'emergency-triage' && (
          <TriageCanvas
            incomingSymptom={incomingSymptom}
            language={language}
            t={t}
          />
        )}

        {currentTab === 'clinical-portal' && (
          <DoctorPortal
            user={user}
            t={t}
            onSwitchToDoctorAuth={() => {
              setUser((prev) => ({ ...prev, role: 'doctor' }));
              setCurrentTab('home-auth');
            }}
          />
        )}
      </main>

      {/* Bottom Navigation - Fixed, high z-index, safe padding to completely prevent overflow clipping */}
      <nav className="fixed bottom-0 inset-x-0 z-50 bg-white/95 backdrop-blur-md border-t border-slate-200 shadow-[0_-2px_10px_rgba(0,0,0,0.06)] pb-[max(env(safe-area-inset-bottom,0px),0.5rem)]">
        <div className="max-w-md mx-auto grid grid-cols-4 items-center h-16 px-2">
          <button
            onClick={() => setCurrentTab('home-auth')}
            className={`flex flex-col items-center justify-center gap-1 h-full rounded-xl transition-all ${
              currentTab === 'home-auth' ? 'text-[#B90538] font-bold scale-105' : 'text-slate-600 hover:text-slate-900 font-medium'
            }`}
          >
            <span className="material-symbols-outlined text-[22px]">female</span>
            <span className="text-[11px] leading-tight truncate">{t.home}</span>
          </button>

          <button
            onClick={() => setCurrentTab('lifespan-dashboard')}
            className={`flex flex-col items-center justify-center gap-1 h-full rounded-xl transition-all ${
              currentTab === 'lifespan-dashboard' ? 'text-[#B90538] font-bold scale-105' : 'text-slate-600 hover:text-slate-900 font-medium'
            }`}
          >
            <span className="material-symbols-outlined text-[22px]">vital_signs</span>
            <span className="text-[11px] leading-tight truncate">{t.dashboard}</span>
          </button>

          <button
            onClick={() => setCurrentTab('emergency-triage')}
            className={`flex flex-col items-center justify-center gap-1 h-full rounded-xl transition-all ${
              currentTab === 'emergency-triage' ? 'text-[#B90538] font-bold scale-105' : 'text-slate-600 hover:text-slate-900 font-medium'
            }`}
          >
            <span className="material-symbols-outlined text-[22px]">emergency</span>
            <span className="text-[11px] leading-tight truncate">{t.triage}</span>
          </button>

          <button
            onClick={() => setCurrentTab('clinical-portal')}
            className={`flex flex-col items-center justify-center gap-1 h-full rounded-xl transition-all ${
              currentTab === 'clinical-portal' ? 'text-[#B90538] font-bold scale-105' : 'text-slate-600 hover:text-slate-900 font-medium'
            }`}
          >
            <span className="material-symbols-outlined text-[22px]">medical_services</span>
            <span className="text-[11px] leading-tight truncate">{t.portal}</span>
          </button>
        </div>
      </nav>
    </div>
  );
}
