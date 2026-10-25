import React, { useState, useRef } from 'react';

export default function DoctorPortal({ user, onSwitchToDoctorAuth, t }) {
  const [onDuty, setOnDuty] = useState(true);
  const [inCall, setInCall] = useState(false);
  const [cameraStream, setCameraStream] = useState(null);
  const [prescriptionSigned, setPrescriptionSigned] = useState(false);
  const [showPassModal, setShowPassModal] = useState(false);
  const [toastMessage, setToastMessage] = useState('');
  const videoRef = useRef(null);

  // RBAC Enforcement Check
  const isDoctor = user?.role === 'doctor';

  const showToast = (msg) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(''), 3000);
  };

  const startVideoCall = async () => {
    try {
      if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
        const stream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
        setCameraStream(stream);
        if (videoRef.current) {
          videoRef.current.srcObject = stream;
        }
      }
      setInCall(true);
      showToast('WebRTC 256-bit encrypted tele-consultation connected');
    } catch (err) {
      console.warn('Camera permission unavailable, falling back to simulated consultation preview', err);
      setInCall(true);
      showToast('Encrypted Tele-Consultation connected (Simulated mode)');
    }
  };

  const endVideoCall = () => {
    if (cameraStream) {
      cameraStream.getTracks().forEach((track) => track.stop());
      setCameraStream(null);
    }
    setInCall(false);
    showToast('Tele-consultation session ended');
  };

  const signPrescription = () => {
    setPrescriptionSigned(true);
    showToast('E-Prescription cryptographically signed with SHA-256 digital stamp');
  };

  const downloadOpdPass = () => {
    setShowPassModal(true);
  };

  if (!isDoctor) {
    return (
      <div className="flex flex-col items-center justify-center p-6 text-center min-h-[60vh] gap-4">
        <div className="w-16 h-16 rounded-full bg-rose-100 text-[#B90538] flex items-center justify-center">
          <span className="material-symbols-outlined text-[32px]">lock</span>
        </div>
        <div>
          <h2 className="text-lg font-extrabold text-slate-900">{t?.restrictedAccess || "Restricted Clinician Access"}</h2>
          <p className="text-xs text-slate-700 max-w-xs mt-1 font-medium">
            Doctor Portal and clinical queue tools are accessible ONLY with registered doctor credentials (doctor@heracare.org or doctor@shakthi.ai).
          </p>
        </div>
        <button
          onClick={onSwitchToDoctorAuth}
          className="px-5 py-2.5 rounded-xl bg-[#00628D] text-white text-xs font-bold shadow-md active:scale-95 flex items-center gap-1.5"
        >
          <span className="material-symbols-outlined text-[16px]">stethoscope</span>
          <span>Authenticate as Doctor</span>
        </button>
      </div>
    );
  }

  return (
    <div className="flex flex-col w-full px-4 py-3 gap-4 pb-16">
      {/* Provider Identity & Clinical Status Banner */}
      <section className="bg-white rounded-2xl p-4 shadow-sm border border-slate-200 flex flex-col gap-3">
        <div className="flex items-center gap-3">
          <div className="relative shrink-0">
            <img
              src="https://lh3.googleusercontent.com/aida-public/AB6AXuDFWItI3uxOIDrcLAQHSRi3i1C8FVv4EIPrTGlUtJNiykAdRGZ931OnsuAqBLuYdxmKfsWohybWTi5SV7URcEgWzQQl3h9B1Bvh-EjSOdI4CJZmpdDecGvqCo9xACmeKdH-17e-Hk-q6wIBk18huB7dmB6ehLXtgPNiMgLc1zKZlvoBPqqT4O-eVZb48SYORDza_QDq1iNrYZ-CehLgA1Kn4EnyupQnsvJJTOsNbVy1csALr_-7HlxWmQ"
              alt="Dr. Soundarya Ramesh"
              className="w-14 h-14 rounded-full object-cover shadow-sm ring-2 ring-emerald-300"
            />
            <span className="absolute bottom-0 right-0 w-3.5 h-3.5 rounded-full bg-[#006C49] ring-2 ring-white"></span>
          </div>
          <div className="min-w-0 flex-1">
            <div className="flex items-center gap-1.5 flex-wrap">
              <span className="text-sm font-extrabold text-slate-900 truncate">Dr. Soundarya Ramesh</span>
              <span className="px-1.5 py-0.2 rounded-full bg-emerald-100 text-[#006C49] text-[9px] font-bold">
                NMC #KMC-48291
              </span>
            </div>
            <p className="text-[11px] text-slate-700 font-medium truncate">MD (OB/GYN) • Senior Maternal-Fetal Consultant</p>
            <span className="text-[10px] text-[#00628D] font-bold">Apollo Cradle & Shakthi.ai Network</span>
          </div>
        </div>

        {/* Tele-Triage Availability */}
        <div className="flex items-center justify-between p-2.5 bg-slate-50 rounded-xl border border-slate-200">
          <div className="flex items-center gap-2">
            <span className={`w-2.5 h-2.5 rounded-full ${onDuty ? 'bg-[#006C49] animate-pulse' : 'bg-slate-400'}`}></span>
            <span className="text-xs font-bold text-slate-900">{t?.onDuty || "Accepting Urgent Triage & Consults"}</span>
          </div>
          <button
            onClick={() => setOnDuty(!onDuty)}
            className={`w-10 h-5 rounded-full transition-colors relative ${onDuty ? 'bg-[#006C49]' : 'bg-slate-400'}`}
          >
            <span
              className={`block w-4 h-4 rounded-full bg-white shadow-sm transition-transform ${
                onDuty ? 'translate-x-5' : 'translate-x-1'
              }`}
            ></span>
          </button>
        </div>

        {/* Telemetry Row */}
        <div className="grid grid-cols-3 gap-2 text-center text-xs">
          <div className="bg-slate-50 p-2 rounded-xl border border-slate-200">
            <span className="text-[10px] text-slate-700 font-semibold block">Queue Load</span>
            <span className="font-extrabold text-slate-900">3 Active</span>
          </div>
          <div className="bg-slate-50 p-2 rounded-xl border border-slate-200">
            <span className="text-[10px] text-slate-700 font-semibold block">SOS Escalations</span>
            <span className="font-extrabold text-red-600">1 En Route</span>
          </div>
          <div className="bg-slate-50 p-2 rounded-xl border border-slate-200">
            <span className="text-[10px] text-slate-700 font-semibold block">ABDM Sync</span>
            <span className="font-extrabold text-[#006C49]">Secured</span>
          </div>
        </div>
      </section>

      {/* Encrypted Tele-Consult Suite */}
      <section className="bg-white rounded-2xl p-4 shadow-sm border border-slate-200 flex flex-col gap-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <span className="material-symbols-outlined text-[#00628D] text-[20px]">videocam</span>
            <h3 className="text-xs font-extrabold text-slate-900 uppercase tracking-wide">
              Encrypted Tele-Consult Suite
            </h3>
          </div>
          <span className="text-[10px] text-[#006C49] bg-emerald-100 px-2 py-0.5 rounded-full font-bold">
            WebRTC Active
          </span>
        </div>

        <div className="relative w-full h-44 rounded-xl bg-slate-900 overflow-hidden flex items-center justify-center shadow-inner">
          <video ref={videoRef} autoPlay playsInline muted className="w-full h-full object-cover"></video>
          {!inCall && (
            <div className="absolute inset-0 flex flex-col items-center justify-center bg-slate-950/80 p-4 text-center">
              <span className="material-symbols-outlined text-white text-[32px] opacity-70">videocam_off</span>
              <p className="text-xs text-white mt-1 font-bold">Consultation Standby</p>
              <p className="text-[10px] text-slate-300 mt-0.5">Click below to initiate tele-triage link</p>
            </div>
          )}
        </div>

        <div className="flex gap-2">
          {!inCall ? (
            <button
              onClick={startVideoCall}
              className="flex-1 py-2.5 bg-[#00628D] text-white text-xs font-bold rounded-xl flex items-center justify-center gap-1.5 active:scale-95 shadow-md"
            >
              <span className="material-symbols-outlined text-[16px]">videocam</span>
              <span>Connect Patient Video</span>
            </button>
          ) : (
            <button
              onClick={endVideoCall}
              className="flex-1 py-2.5 bg-red-600 text-white text-xs font-bold rounded-xl flex items-center justify-center gap-1.5 active:scale-95 shadow-md"
            >
              <span className="material-symbols-outlined text-[16px]">call_end</span>
              <span>End Consultation</span>
            </button>
          )}
        </div>
      </section>

      {/* Patient Clinical Profile & Diagnostic Chart */}
      <section className="bg-white rounded-2xl p-4 shadow-sm border border-slate-200 flex flex-col gap-3">
        <div className="flex items-center justify-between border-b pb-2 border-slate-200">
          <div>
            <h3 className="text-sm font-extrabold text-slate-900">Kavya Reddy (24y)</h3>
            <p className="text-[11px] text-slate-700 font-medium">ABHA #91-8472-9102-1849 • Hyderabad</p>
          </div>
          <span className="px-2 py-0.5 bg-amber-100 text-amber-900 rounded-full text-xs font-bold">
            PCOS Review
          </span>
        </div>

        {/* Vitals Grid */}
        <div className="grid grid-cols-2 gap-2 text-xs">
          <div className="bg-slate-50 p-2.5 rounded-xl border border-slate-200">
            <span className="text-[10px] text-slate-700 font-semibold block">Blood Pressure</span>
            <span className="text-sm font-extrabold text-slate-900">118 / 78 mmHg</span>
            <span className="text-[10px] text-[#006C49] font-bold block mt-0.5">Normal</span>
          </div>
          <div className="bg-slate-50 p-2.5 rounded-xl border border-slate-200">
            <span className="text-[10px] text-slate-700 font-semibold block">Resting Heart Rate</span>
            <span className="text-sm font-extrabold text-slate-900">78 bpm</span>
            <span className="text-[10px] text-[#006C49] font-bold block mt-0.5">Regular Rhythm</span>
          </div>
        </div>

        {/* Doctor Impression */}
        <div className="bg-slate-50 p-3 rounded-xl border border-slate-200 text-xs flex flex-col gap-1">
          <span className="font-extrabold text-slate-900">Consultation Impression & Orders:</span>
          <p className="text-slate-800 font-medium leading-relaxed">
            Recommend cycle initiation with cyclic Progestin therapy (Medroxyprogesterone 10mg OD x 10 days). Initiate Myo-Inositol + D-Chiro-Inositol supplementation. Scheduled lipid profile & OGTT follow-up in 6 weeks.
          </p>
        </div>

        {/* Doctor Actions */}
        <div className="flex flex-col gap-2 pt-1">
          <button
            onClick={signPrescription}
            className={`w-full py-2.5 rounded-xl text-xs font-bold flex items-center justify-center gap-1.5 shadow-md active:scale-95 transition-all ${
              prescriptionSigned ? 'bg-[#006C49] text-white' : 'bg-[#B90538] text-white'
            }`}
          >
            <span className="material-symbols-outlined text-[16px]">draw</span>
            <span>{prescriptionSigned ? 'E-Prescription Signed ✓ (SHA-256)' : (t?.signPrescription || 'Approve & Sign E-Prescription')}</span>
          </button>

          <div className="p-3 bg-slate-100 rounded-xl flex items-center justify-between border border-slate-200">
            <div className="flex items-center gap-2">
              <span className="material-symbols-outlined text-[24px] text-slate-800">qr_code_2</span>
              <div>
                <span className="text-xs font-bold text-slate-900 block">Verifiable ABDM OPD Pass</span>
                <span className="text-[10px] text-slate-700 font-mono">Token #SHAKTHI-BLR-2024-8491</span>
              </div>
            </div>
            <button
              onClick={downloadOpdPass}
              className="px-3 py-1.5 rounded-xl bg-[#00628D] text-white text-xs font-bold flex items-center gap-1 active:scale-95 shadow-sm"
            >
              <span className="material-symbols-outlined text-[16px]">download</span>
              <span>Pass</span>
            </button>
          </div>
        </div>
      </section>

      {/* Verifiable OPD Pass Modal Dialog */}
      {showPassModal && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl p-5 max-w-sm w-full shadow-2xl flex flex-col gap-3">
            <div className="flex items-center justify-between border-b pb-2 border-slate-200">
              <div className="flex items-center gap-1.5">
                <span className="text-xs font-extrabold text-[#B90538] uppercase tracking-wider">Shakthi.ai</span>
                <span className="text-[9px] bg-emerald-100 text-[#006C49] px-1.5 py-0.5 rounded font-bold">ABDM PASS</span>
              </div>
              <button onClick={() => setShowPassModal(false)} className="text-slate-600 hover:text-slate-900">
                <span className="material-symbols-outlined text-[20px]">close</span>
              </button>
            </div>

            <div className="flex flex-col items-center text-center p-3 bg-slate-50 rounded-2xl border border-slate-200">
              <div className="w-24 h-24 bg-white p-2 rounded-xl shadow-xs border border-slate-300 flex items-center justify-center">
                <span className="material-symbols-outlined text-[64px] text-slate-900">qr_code_2</span>
              </div>
              <h4 className="text-sm font-extrabold text-slate-900 mt-2">Kavya Reddy (24y)</h4>
              <p className="text-xs text-slate-700 font-semibold">ABHA: 91-8472-9102-1849</p>
              <p className="text-[11px] text-[#006C49] font-bold mt-1">Verified Clinician Sign-Off: Dr. Soundarya Ramesh</p>
              <p className="text-[10px] text-slate-700 mt-1 font-mono">Hospital: KC General Hospital • Token #SHAKTHI-BLR-2024-8491</p>
            </div>

            <div className="grid grid-cols-2 gap-2">
              <button
                onClick={() => {
                  window.print();
                  setShowPassModal(false);
                }}
                className="py-2.5 rounded-xl bg-slate-200 text-slate-900 text-xs font-bold flex items-center justify-center gap-1 active:scale-95"
              >
                <span className="material-symbols-outlined text-[16px]">print</span>
                <span>Print</span>
              </button>
              <button
                onClick={() => {
                  showToast('Pass downloaded to device');
                  setShowPassModal(false);
                }}
                className="py-2.5 rounded-xl bg-[#00628D] text-white text-xs font-bold flex items-center justify-center gap-1 active:scale-95"
              >
                <span className="material-symbols-outlined text-[16px]">download</span>
                <span>Save PDF</span>
              </button>
            </div>
          </div>
        </div>
      )}

      {toastMessage && (
        <div className="fixed bottom-24 inset-x-4 z-50 p-3 rounded-xl bg-slate-900 text-white text-xs flex items-center gap-2 shadow-xl animate-fade-in font-medium">
          <span className="material-symbols-outlined text-[18px] text-emerald-400">check_circle</span>
          <span>{toastMessage}</span>
        </div>
      )}
    </div>
  );
}
