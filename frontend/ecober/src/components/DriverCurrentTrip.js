import React, { useEffect, useState } from 'react';
import { LeafIllustration, Co2Equiv } from './EcoUtils';

function DriverCurrentTrip() {
  const [trip, setTrip]                 = useState(null);
  const [loading, setLoading]           = useState(true);
  const [error, setError]               = useState(null);
  const [actionLoading, setActionLoading] = useState(false);
  const [lifetimeStats, setLifetimeStats] = useState({ kgSaved: '0', rides: 0, trust: null });

  useEffect(() => {
    const token = localStorage.getItem('token');

    fetch('http://localhost:8080/driver/me/current-trip', {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(r => r.ok ? r.json() : null)
      .then(setTrip)
      .catch(e => setError(e.message))
      .finally(() => setLoading(false));

    Promise.all([
      fetch('http://localhost:8080/driver/me/eco-report',  { headers: { Authorization: `Bearer ${token}` } }),
      fetch('http://localhost:8080/driver/me/trip-count',  { headers: { Authorization: `Bearer ${token}` } }),
      fetch('http://localhost:8080/driver/me/getProfile',  { headers: { Authorization: `Bearer ${token}` } }),
    ]).then(async ([ecoRes, countRes, profileRes]) => {
      const [eco, count, profile] = await Promise.all([
        ecoRes.ok    ? ecoRes.json()    : {},
        countRes.ok  ? countRes.json()  : {},
        profileRes.ok ? profileRes.json() : {},
      ]);
      const kgSaved = (eco.monthlyCo2Savings || []).reduce((s, m) => s + (m.co2 || 0), 0);
      setLifetimeStats({
        kgSaved:   kgSaved.toFixed(1),
        rides:     count.totalRides || 0,
        ecoScore:  eco.ecoScore ?? profile.ecoScore ?? 30,
      });
    }).catch(() => {});
  }, []);

  const handleStartTrip = async () => {
    setActionLoading(true);
    const token = localStorage.getItem('token');
    const res = await fetch(`http://localhost:8080/driver/start-trip/${trip.tripId}`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${token}` },
    });
    if (res.ok) setTrip({ ...trip, status: 'IN_PROGRESS' });
    else setError('Failed to start trip.');
    setActionLoading(false);
  };

  const handleEndTrip = async () => {
    setActionLoading(true);
    const token = localStorage.getItem('token');
    const res = await fetch(`http://localhost:8080/driver/end-trip/${trip.tripId}`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${token}` },
    });
    if (res.ok) setTrip(null);
    else setError('Failed to end trip.');
    setActionLoading(false);
  };

  if (loading) {
    return (
      <div className="p-8">
        <div className="bg-white rounded-2xl h-64 animate-pulse max-w-xl" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-8">
        <div className="bg-red-50 border border-red-100 rounded-2xl p-8 text-center max-w-xl">
          <p className="text-sm text-red-600">{error}</p>
        </div>
      </div>
    );
  }

  if (!trip) {
    return (
      <div className="p-8 max-w-xl">
        <style>{`
          @keyframes leafPulse {
            0%, 100% { transform: scale(1) rotate(-2deg); opacity: 0.9; }
            50%       { transform: scale(1.08) rotate(2deg); opacity: 1; }
          }
        `}</style>
        <div className="bg-white border border-gray-300 rounded-xl p-10 text-center shadow-md">
          <div className="flex justify-center mb-5">
            <LeafIllustration />
          </div>
          <h3 className="text-base font-semibold text-gray-900 mb-1.5">No active trip</h3>
          <p className="text-sm text-gray-700 mb-6">Accept a ride from Available Rides to begin your next journey.</p>

          <LifetimeStatsRow stats={lifetimeStats} />
          <Co2Equiv kg={parseFloat(lifetimeStats.kgSaved)} className="mt-4 text-gray-700" />
        </div>
      </div>
    );
  }

  const isAccepted   = trip.status === 'ACCEPTED';
  const isInProgress = trip.status === 'IN_PROGRESS';

  return (
    <div className="p-8">
      <div className="bg-white rounded-xl border border-gray-300 shadow-md overflow-hidden">
        {/* Status strip */}
        <div className={`flex items-center space-x-2 px-5 py-3 border-b ${
          isAccepted   ? 'bg-amber-50 text-amber-800 border-amber-200' :
          isInProgress ? 'bg-emerald-50 text-emerald-800 border-emerald-200' :
                         'bg-gray-50 text-gray-700 border-gray-200'
        }`}>
          <span className={`w-2 h-2 rounded-full animate-pulse flex-shrink-0 ${
            isAccepted ? 'bg-amber-400' : isInProgress ? 'bg-emerald-400' : 'bg-slate-400'
          }`} />
          <span className="text-sm font-semibold">
            {isAccepted ? 'Ready to Start' : isInProgress ? 'Trip in Progress' : trip.status}
          </span>
          <span className="text-xs ml-auto opacity-60">Trip #{trip.tripId}</span>
        </div>

        {/* Route */}
        <div className="px-5 py-5 flex items-stretch gap-4">
          <div className="flex flex-col items-center py-0.5 flex-shrink-0">
            <div className="w-3 h-3 rounded-full bg-blue-500 ring-4 ring-blue-50" />
            <div className="w-px flex-1 bg-slate-200 my-1.5" />
            <div className="w-3 h-3 rounded-full bg-red-400 ring-4 ring-red-50" />
          </div>
          <div className="flex-1 flex flex-col justify-between gap-2 min-w-0">
            <div>
              <p className="text-xs text-slate-400 uppercase tracking-wide font-medium">Pickup</p>
              <p className="text-sm font-semibold text-slate-700 mt-0.5">
                {trip.route?.source?.address || 'N/A'}
              </p>
            </div>
            <div>
              <p className="text-xs text-slate-400 uppercase tracking-wide font-medium">Drop-off</p>
              <p className="text-sm font-semibold text-slate-700 mt-0.5">
                {trip.route?.destination?.address || 'N/A'}
              </p>
            </div>
          </div>
        </div>

        {/* Rider */}
        <div className="px-5 py-4 bg-slate-50 border-t border-slate-100 flex items-center space-x-3">
          <div className="w-10 h-10 bg-gradient-to-br from-slate-300 to-slate-400 rounded-full flex items-center justify-center text-white text-sm font-bold flex-shrink-0">
            {trip.user?.username?.[0]?.toUpperCase() ?? '?'}
          </div>
          <div>
            <p className="text-sm font-semibold text-slate-700">{trip.user?.username || 'Unknown Rider'}</p>
            <p className="text-xs text-slate-400">Rider</p>
          </div>
        </div>

        {/* Action */}
        <div className="px-5 py-4 border-t border-slate-100">
          {isAccepted && (
            <button
              onClick={handleStartTrip}
              disabled={actionLoading}
              className="w-full py-3.5 bg-blue-500 hover:bg-blue-600 disabled:opacity-60 text-white rounded-xl font-semibold text-sm transition-all shadow-sm"
            >
              {actionLoading ? 'Starting…' : 'Start Trip'}
            </button>
          )}
          {isInProgress && (
            <button
              onClick={handleEndTrip}
              disabled={actionLoading}
              className="w-full py-3.5 bg-emerald-500 hover:bg-emerald-600 disabled:opacity-60 text-white rounded-xl font-semibold text-sm transition-all shadow-sm"
            >
              {actionLoading ? 'Ending…' : 'Complete Trip'}
            </button>
          )}
        </div>

        {/* Motivational banner */}
        <div className="px-5 py-3 bg-gray-50 border-t border-gray-200 text-center">
          <p className="text-xs font-medium text-gray-700">Every trip you complete contributes to measurable cleaner air.</p>
        </div>
      </div>

      <div className="mt-4 max-w-xl">
        <LifetimeStatsRow stats={lifetimeStats} />
      </div>
    </div>
  );
}

function LifetimeStatsRow({ stats }) {
  return (
    <div className="bg-gradient-to-br from-gray-50 to-gray-100 border border-gray-300 rounded-lg p-5 text-gray-800 shadow-sm">
      <div className="grid grid-cols-3 gap-4 text-sm font-semibold">
        <div className="rounded-lg bg-white border border-gray-200 p-3 shadow-sm">
          <div className="text-2xl text-emerald-700">{stats.kgSaved}</div>
          <div className="text-xs text-gray-600 mt-1">kg CO₂ saved</div>
        </div>
        <div className="rounded-lg bg-white border border-gray-200 p-3 shadow-sm">
          <div className="text-2xl text-gray-900">{stats.rides}</div>
          <div className="text-xs text-gray-600 mt-1">rides</div>
        </div>
        <div className="rounded-lg bg-white border border-gray-200 p-3 shadow-sm">
          <div className="text-2xl text-emerald-700">{stats.ecoScore}</div>
          <div className="text-xs text-gray-600 mt-1">Eco Score</div>
        </div>
      </div>
    </div>
  );
}

export default DriverCurrentTrip;
