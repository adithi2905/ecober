import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

const VEHICLE_ICONS = { Sedan: '🚗', SUV: '🚙', Van: '🚐', Electric: '⚡' };

const STATUS_CONFIG = {
  ACCEPTED:    { label: 'Driver Assigned',  dot: 'bg-amber-400',   badge: 'bg-amber-50 text-amber-700 border-amber-200' },
  IN_PROGRESS: { label: 'Ride in Progress', dot: 'bg-emerald-400', badge: 'bg-emerald-50 text-emerald-700 border-emerald-200' },
  COMPLETED:   { label: 'Completed',        dot: 'bg-slate-400',   badge: 'bg-slate-50 text-slate-600 border-slate-200' },
};

function CurrentRide() {
  const [ride, setRide]       = useState(null);
  const [loading, setLoading] = useState(true);
  const [profile, setProfile] = useState({ totalCO2Saved: 0, tripCount: 0 });
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('token');

    fetch('http://localhost:8080/user/trip/current', {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(r => r.ok ? r.json() : r.status === 404 ? null : null)
      .then(setRide)
      .catch(() => {})
      .finally(() => setLoading(false));

    fetch('http://localhost:8080/user/profile', {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(r => r.ok ? r.json() : null)
      .then(d => {
        if (d) setProfile({ totalCO2Saved: d.totalCO2Saved ?? 0, tripCount: d.tripCount ?? 0 });
      })
      .catch(() => {});
  }, []);

  if (loading) {
    return (
      <div className="p-8">
        <div className="bg-white rounded-2xl h-56 animate-pulse max-w-xl" />
      </div>
    );
  }

  const treesEquiv = (profile.totalCO2Saved / 22).toFixed(1);

  if (!ride) {
    return (
      <div className="p-8 max-w-xl">
        <style>{`
          @keyframes leafSwing {
            0%, 100% { transform: rotate(-5deg); }
            50%       { transform: rotate(5deg); }
          }
        `}</style>

        <div className="bg-white border border-green-200 rounded-2xl p-10 text-center">
          {/* Animated leaf */}
          <div className="flex justify-center mb-5">
            <div style={{ animation: 'leafSwing 3s ease-in-out infinite', transformOrigin: 'bottom center' }}>
              <svg width="52" height="58" viewBox="0 0 64 72" fill="none">
                <path
                  d="M32 68 C32 68 6 50 6 28 C6 12 18 3 32 3 C46 3 58 12 58 28 C58 50 32 68 32 68Z"
                  fill="#6ee7b7"
                  stroke="#10b981"
                  strokeWidth="2"
                />
                <line x1="32" y1="10" x2="32" y2="66" stroke="#059669" strokeWidth="1.5" strokeLinecap="round" opacity="0.45" />
                <line x1="32" y1="32" x2="20" y2="22" stroke="#059669" strokeWidth="1.2" strokeLinecap="round" opacity="0.55" />
                <line x1="32" y1="40" x2="44" y2="30" stroke="#059669" strokeWidth="1.2" strokeLinecap="round" opacity="0.55" />
              </svg>
            </div>
          </div>

          <h3 className="text-base font-semibold text-gray-700 mb-1.5">No active ride right now</h3>
          <p className="text-sm text-emerald-600 mb-6">Book a ride to start saving CO₂ with every trip</p>

          <button
            onClick={() => navigate('/rideBooking')}
            className="px-7 py-3 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-sm font-semibold transition-all shadow-sm"
          >
            Book a Ride
          </button>

          {profile.totalCO2Saved > 0 && (
            <>
              <div className="flex items-center gap-2 my-5">
                <div className="flex-1 h-px bg-slate-100" />
                <span className="text-xs text-slate-300">your progress</span>
                <div className="flex-1 h-px bg-slate-100" />
              </div>
              <p className="text-xs text-emerald-700 font-medium flex items-center justify-center gap-1.5">
                <svg width="11" height="12" viewBox="0 0 64 72" fill="none">
                  <path d="M32 68 C32 68 6 50 6 28 C6 12 18 3 32 3 C46 3 58 12 58 28 C58 50 32 68 32 68Z" fill="#6ee7b7" stroke="#10b981" strokeWidth="3" />
                </svg>
                You've already saved {profile.totalCO2Saved.toFixed(1)} kg CO₂ — keep it going!
              </p>
            </>
          )}
        </div>

        {/* Mini stat cards */}
        <div className="grid grid-cols-3 gap-3 mt-4">
          <MiniStat icon="🌿" value={`${profile.totalCO2Saved.toFixed(1)} kg`} label="CO₂ saved" />
          <MiniStat icon="🚗" value={profile.tripCount}                        label="total rides" />
          <MiniStat icon="🌳" value={treesEquiv}                               label="trees equiv." />
        </div>
      </div>
    );
  }

  const cfg = STATUS_CONFIG[ride.status] ?? {
    label: ride.status,
    dot:   'bg-slate-400',
    badge: 'bg-slate-50 text-slate-600 border-slate-200',
  };

  return (
    <div className="p-8">
      <div className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden max-w-xl">
        {/* Status strip */}
        <div className={`flex items-center space-x-2 px-5 py-3 border-b ${cfg.badge}`}>
          <span className={`w-2 h-2 rounded-full ${cfg.dot} animate-pulse flex-shrink-0`} />
          <span className="text-sm font-semibold">{cfg.label}</span>
          <span className="text-xs ml-auto opacity-60">Trip #{ride.tripId}</span>
        </div>

        {/* Route */}
        <div className="px-5 py-5 flex items-stretch gap-4">
          <div className="flex flex-col items-center py-0.5 flex-shrink-0">
            <div className="w-3 h-3 rounded-full bg-emerald-500 ring-4 ring-emerald-50" />
            <div className="w-px flex-1 bg-slate-200 my-1.5" />
            <div className="w-3 h-3 rounded-full bg-red-400 ring-4 ring-red-50" />
          </div>
          <div className="flex-1 flex flex-col justify-between gap-2 min-w-0">
            <div>
              <p className="text-xs text-slate-400 uppercase tracking-wide font-medium">Pickup</p>
              <p className="text-sm font-semibold text-slate-700 mt-0.5">
                {ride.route?.source?.address || ride.pickupLocation || 'N/A'}
              </p>
            </div>
            <div>
              <p className="text-xs text-slate-400 uppercase tracking-wide font-medium">Drop-off</p>
              <p className="text-sm font-semibold text-slate-700 mt-0.5">
                {ride.route?.destination?.address || ride.dropoffLocation || 'N/A'}
              </p>
            </div>
          </div>
          {ride.fare != null && (
            <div className="text-right flex-shrink-0">
              <p className="text-xs text-slate-400 mb-1">Est. Fare</p>
              <p className="text-xl font-bold text-emerald-600">${ride.fare.toFixed(2)}</p>
            </div>
          )}
        </div>

        {/* Driver */}
        <div className="px-5 py-4 bg-slate-50 border-t border-slate-100 flex items-center space-x-4">
          <div className="w-11 h-11 bg-gradient-to-br from-slate-200 to-slate-300 rounded-full flex items-center justify-center text-xl flex-shrink-0">
            {VEHICLE_ICONS[ride.driver?.vehicleType] ?? '🚗'}
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-sm font-semibold text-slate-700 truncate">
              {ride.driver?.driverName || 'Unknown Driver'}
            </p>
            <p className="text-xs text-slate-400">{ride.driver?.vehicleType || 'Vehicle'}</p>
          </div>
          <div className="w-9 h-9 bg-emerald-500 rounded-full flex items-center justify-center text-white text-sm font-bold flex-shrink-0">
            {ride.driver?.driverName?.[0]?.toUpperCase() ?? '?'}
          </div>
        </div>
      </div>
    </div>
  );
}

function MiniStat({ icon, value, label }) {
  return (
    <div className="bg-green-50 border border-green-100 rounded-2xl p-4 text-center">
      <span className="text-xl">{icon}</span>
      <p className="text-base font-bold text-slate-800 mt-1.5 leading-tight">{value}</p>
      <p className="text-xs text-slate-400 mt-0.5">{label}</p>
    </div>
  );
}

export default CurrentRide;
