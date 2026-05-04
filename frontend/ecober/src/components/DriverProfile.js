import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { BadgeShield, BADGE_TIERS, BADGE_LABELS, BADGE_ORDER } from './EcoBadge';
import { StarIcon, CarIcon, LeafIcon } from './Icons';

const VEHICLE_ICONS = { Sedan: '🚗', SUV: '🚙', Van: '🚐', Electric: '⚡' };

function DriverProfile() {
  const [driver, setDriver]   = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError]     = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('token');
    fetch('http://localhost:8080/driver/me/getProfile', {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(r => r.ok ? r.json() : Promise.reject())
      .then(setDriver)
      .catch(() => setError('Failed to load profile.'))
      .finally(() => setLoading(false));
  }, []);

  const handleLogout = async () => {
    const token = localStorage.getItem('token');
    await fetch('http://localhost:8080/driver/logout', {
      method: 'POST',
      headers: { Authorization: `Bearer ${token}` },
    }).catch(() => {});
    localStorage.removeItem('token');
    navigate('/');
  };

  if (loading) {
    return (
      <div className="p-8 max-w-xl mx-auto space-y-4">
        <div className="h-64 bg-white rounded-2xl animate-pulse" />
        <div className="grid grid-cols-2 gap-4">
          {[1, 2, 3, 4].map(i => <div key={i} className="h-24 bg-white rounded-2xl animate-pulse" />)}
        </div>
      </div>
    );
  }

  if (error || !driver) {
    return (
      <div className="p-8 flex items-center justify-center">
        <p className="text-sm text-red-500">{error || 'No profile data found.'}</p>
      </div>
    );
  }

  const tierIndex = Math.max(BADGE_ORDER.indexOf(driver.ecoBadge), 0);
  const tierCfg   = BADGE_TIERS[tierIndex];
  const badgeLabel = BADGE_LABELS[driver.ecoBadge] || '';
  const initial   = driver.driverName?.[0]?.toUpperCase() ?? 'D';

  return (
    <div className="p-8 max-w-xl mx-auto space-y-5">

      {/* ── Badge Hero ── */}
      <div
        className="rounded-2xl p-8 text-white shadow-lg relative overflow-hidden"
        style={{ background: `linear-gradient(135deg, ${tierCfg.fill} 0%, ${tierCfg.stroke} 100%)` }}
      >
        {/* Decorative circles */}
        <div className="absolute -top-12 -right-12 w-48 h-48 rounded-full" style={{ background: 'rgba(255,255,255,0.12)' }} />
        <div className="absolute bottom-0 left-0 w-32 h-32 rounded-full" style={{ background: 'rgba(0,0,0,0.08)' }} />

        <div className="relative flex items-center gap-6">
          {/* Large shield badge */}
          <div className="flex-shrink-0 drop-shadow-lg">
            <BadgeShield badgeName={driver.ecoBadge} size={96} />
          </div>

          <div>
            {/* Driver name */}
            <div className="flex items-center gap-3 mb-3">
              <div className="w-12 h-12 bg-white/20 border-2 border-white/40 rounded-xl flex items-center justify-center text-xl font-bold">
                {initial}
              </div>
              <div>
                <h2 className="text-xl font-bold leading-tight">{driver.driverName}</h2>
                <p className="text-white/70 text-xs">Ecober Driver</p>
              </div>
            </div>

            {/* Badge name + label */}
            <div className="bg-white/15 backdrop-blur-sm rounded-xl px-4 py-2.5">
              <p className="text-sm font-bold leading-tight">{driver.ecoBadge || 'Standard Driver'}</p>
              <p className="text-white/70 text-xs mt-0.5">{badgeLabel}</p>
            </div>

            {/* Badge tier dots */}
            <div className="flex gap-1.5 mt-3">
              {BADGE_ORDER.map((_, i) => (
                <div
                  key={i}
                  className="w-2 h-2 rounded-full transition-all"
                  style={{ backgroundColor: i <= tierIndex ? 'rgba(255,255,255,0.9)' : 'rgba(255,255,255,0.25)' }}
                />
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* ── Stats grid ── */}
      <div className="grid grid-cols-2 gap-4">
        {/* Trust Score */}
        <div className="bg-white rounded-2xl p-5 border border-slate-100">
          <div className="flex items-center gap-2 mb-3">
            <StarIcon size={16} className="text-amber-400" filled />
            <span className="text-xs text-slate-400 font-medium uppercase tracking-wide">Trust Score</span>
          </div>
          <p className="text-2xl font-bold text-slate-800">{driver.trustScore ?? '—'}</p>
          <p className="text-xs text-slate-400 mt-0.5">rider confidence rating</p>
        </div>

        {/* Vehicle */}
        <div className="bg-white rounded-2xl p-5 border border-slate-100">
          <div className="flex items-center gap-2 mb-3">
            <CarIcon size={16} className="text-blue-400" />
            <span className="text-xs text-slate-400 font-medium uppercase tracking-wide">Vehicle</span>
          </div>
          <p className="text-2xl font-bold text-slate-800">
            {VEHICLE_ICONS[driver.vehicleType] ?? ''} {driver.vehicleType || 'N/A'}
          </p>
          <p className="text-xs text-slate-400 mt-0.5">{driver.vehicleNo || 'N/A'}</p>
        </div>

        {/* Fuel Efficiency */}
        <div className="bg-white rounded-2xl p-5 border border-slate-100 col-span-2">
          <div className="flex items-center gap-2 mb-3">
            <LeafIcon size={16} className="text-emerald-500" />
            <span className="text-xs text-slate-400 font-medium uppercase tracking-wide">Fuel Efficiency</span>
          </div>
          <div className="flex items-baseline gap-2">
            <p className="text-2xl font-bold text-emerald-600">{driver.fuelEfficiency || 'N/A'}</p>
            <p className="text-sm text-slate-400">km/L</p>
          </div>
          <p className="text-xs text-slate-400 mt-0.5">Higher efficiency = lower CO₂ per trip</p>
        </div>
      </div>

      {/* Sign out */}
      <button
        onClick={handleLogout}
        className="w-full py-3.5 rounded-xl font-semibold text-sm text-slate-500 bg-white border border-slate-200 hover:bg-red-50 hover:border-red-200 hover:text-red-500 transition-all"
      >
        Sign Out
      </button>
    </div>
  );
}

export default DriverProfile;
