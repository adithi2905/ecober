import React, { useEffect, useState } from 'react';
import { BadgeShield } from './EcoBadge';
import { LeafIcon, CloudIcon, TrendingDownIcon, AwardIcon } from './Icons';
import { Co2Equiv, RealWorldTile } from './EcoUtils';

const TYPE_COLORS = [
  'text-blue-600 bg-blue-50',
  'text-purple-600 bg-purple-50',
  'text-amber-600 bg-amber-50',
  'text-pink-600 bg-pink-50',
];

function EcoReport() {
  const [monthlyData,  setMonthlyData]  = useState([]);
  const [rideTypeData, setRideTypeData] = useState([]);
  const [poolingData,  setPoolingData]  = useState([]);
  const [ecoBadge,     setEcoBadge]     = useState('');
  const [target,       setTarget]       = useState(30);
  const [newTarget,    setNewTarget]    = useState('');
  const [editingTarget, setEditingTarget] = useState(false);
  const [loading,      setLoading]      = useState(true);
  const [error,        setError]        = useState(null);

  useEffect(() => {
    const token = localStorage.getItem('token');
    Promise.all([
      fetch('http://localhost:8080/driver/me/eco-report',  { headers: { Authorization: `Bearer ${token}` } }),
      fetch('http://localhost:8080/driver/me/past-trips',  { headers: { Authorization: `Bearer ${token}` } }),
      fetch('http://localhost:8080/driver/me/getProfile',  { headers: { Authorization: `Bearer ${token}` } }),
    ])
      .then(async ([ecoRes, tripsRes, profileRes]) => {
        if (!ecoRes.ok)   throw new Error('Failed to fetch eco report');
        if (!tripsRes.ok) throw new Error('Failed to fetch trips');
        const [eco, trips, profile] = await Promise.all([ecoRes.json(), tripsRes.json(), profileRes.ok ? profileRes.json() : {}]);
        setMonthlyData(eco.monthlyCo2Savings || []);
        setRideTypeData(eco.rideTypeDistribution || []);
        setEcoBadge(profile.ecoBadge || '');
        const pooled = trips.filter(t => t.is_pooling === 1).length;
        setPoolingData([
          { name: 'Pooling', value: pooled,              color: 'bg-emerald-400' },
          { name: 'Solo',    value: trips.length - pooled, color: 'bg-slate-200' },
        ]);
      })
      .catch(e => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <div className="p-8 space-y-4 max-w-2xl mx-auto">
        {[1, 2, 3].map(i => <div key={i} className="bg-white rounded-2xl h-28 animate-pulse" />)}
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-8 flex items-center justify-center">
        <p className="text-sm text-red-500">{error}</p>
      </div>
    );
  }

  const currentMonthKey   = new Date().toLocaleString('default', { month: 'short' }).toUpperCase();
  const currentMonthSaved = monthlyData.find(m => m.month === currentMonthKey)?.co2 ?? 0;
  const goalProgress      = Math.min((currentMonthSaved / target) * 100, 100);
  const maxCo2            = Math.max(...monthlyData.map(m => m.co2 || 0), 1);
  const totalSaved        = monthlyData.reduce((s, m) => s + (m.co2 || 0), 0);
  const totalRides        = poolingData.reduce((s, p) => s + p.value, 0);
  const totalPooled       = poolingData.find(p => p.name === 'Pooling')?.value ?? 0;

  const handleSetTarget = () => {
    const v = parseFloat(newTarget);
    if (!isNaN(v) && v > 0) { setTarget(v); setEditingTarget(false); setNewTarget(''); }
  };

  return (
    <div className="p-8 max-w-2xl mx-auto space-y-6">

      {/* ── Badge + Goal combined card ── */}
      <div className="bg-white rounded-lg border border-gray-300 shadow-md overflow-hidden">
        <div className="h-3 bg-gradient-to-r from-emerald-600 to-emerald-500" />
        <div className="p-6 flex items-start gap-6">
          {/* Badge */}
          {ecoBadge && (
            <div className="flex flex-col items-center gap-1.5 flex-shrink-0">
              <BadgeShield badgeName={ecoBadge} size={64} />
              <p className="text-xs font-semibold text-gray-700 text-center leading-tight max-w-16">{ecoBadge}</p>
            </div>
          )}

          {/* Goal */}
          <div className="flex-1">
            <div className="flex items-center justify-between mb-3">
              <div className="flex items-center gap-1.5">
                <AwardIcon size={15} className="text-amber-600" />
                <h3 className="text-sm font-semibold text-gray-900">Monthly CO₂ Goal</h3>
              </div>
              <button
                onClick={() => setEditingTarget(!editingTarget)}
                className="text-xs px-2.5 py-1 rounded-lg bg-gray-200 hover:bg-gray-300 text-gray-700 font-medium transition-all shadow-sm"
              >
                {editingTarget ? 'Cancel' : 'Set Target'}
              </button>
            </div>

            {editingTarget && (
              <div className="flex items-center gap-2 mb-3">
                <input
                  type="number"
                  placeholder="Target (kg CO₂)"
                  value={newTarget}
                  onChange={e => setNewTarget(e.target.value)}
                  className="border border-gray-300 rounded-lg px-3 py-1.5 text-sm w-36 focus:outline-none focus:ring-2 focus:ring-emerald-500 shadow-sm"
                />
                <button
                  onClick={handleSetTarget}
                  className="px-3 py-1.5 rounded-lg bg-gradient-to-br from-emerald-600 to-emerald-700 text-white text-sm font-medium hover:shadow-lg transition-all shadow-md"
                >
                  Save
                </button>
              </div>
            )}

            <div className="flex items-baseline justify-between mb-2">
              <span className="text-xl font-bold text-emerald-700">
                {currentMonthSaved.toFixed(2)} <span className="text-xs text-gray-600 font-normal">kg saved</span>
              </span>
              <span className="text-xs text-gray-600">target: {target} kg</span>
            </div>
            <div className="h-2.5 bg-gray-300 rounded-full overflow-hidden shadow-inner">
              <div
                className="h-full bg-gradient-to-r from-emerald-600 to-emerald-500 rounded-full transition-all duration-700 shadow-sm"
                style={{ width: `${goalProgress}%` }}
              />
            </div>
            <Co2Equiv kg={currentMonthSaved} className="mt-3 text-gray-700" />
            <div className="flex justify-between mt-1.5">
              <span className="text-xs text-gray-600">{goalProgress.toFixed(0)}% complete</span>
              {goalProgress >= 100 && <span className="text-xs text-emerald-700 font-semibold">Goal reached!</span>}
            </div>
          </div>
        </div>
      </div>

      {/* ── Summary stats ── */}
      <div className="grid grid-cols-3 gap-4">
        <StatCard Icon={LeafIcon}        iconClass="text-emerald-600" value={totalSaved.toFixed(1)} label="kg CO₂ saved" />
        <StatCard Icon={CloudIcon}       iconClass="text-gray-700"    value={totalRides}             label="total rides" />
        <StatCard Icon={TrendingDownIcon} iconClass="text-teal-600"   value={totalPooled}            label="pooled rides" />
      </div>

      <div className="grid grid-cols-3 gap-4">
        <RealWorldTile icon="" label="Trees equivalent" value={`${(totalSaved / 22).toFixed(1)}`} />
        <RealWorldTile icon="" label="Flights offset" value={`${Math.max(0, Math.round(totalSaved / 90))}`} />
        <RealWorldTile icon="" label="Factories neutralized" value={`${(totalSaved / 1800).toFixed(1)}`} />
      </div>

      {/* ── Monthly bar list ── */}
      {monthlyData.length > 0 && (
        <div className="bg-white rounded-lg border border-gray-300 p-6 shadow-md">
          <div className="flex items-center gap-2 mb-4">
            <TrendingDownIcon size={15} className="text-emerald-600" />
            <h3 className="text-sm font-semibold text-gray-900">Monthly CO₂ Savings</h3>
          </div>
          <div className="space-y-3">
            {monthlyData.map(m => {
              const pct = `${((m.co2 || 0) / maxCo2) * 100}%`;
              const isMay = m.month === 'MAY';
              return (
                <div key={m.month} className="flex items-center gap-3 relative">
                  <span className="text-xs text-gray-600 font-medium w-8 flex-shrink-0">{m.month}</span>
                  <div className="relative flex-1 h-2.5 bg-gray-300 rounded-full overflow-hidden shadow-inner">
                    <div
                      className={`absolute inset-y-0 left-0 rounded-full transition-all duration-500 ${isMay ? 'bg-gradient-to-r from-emerald-500 to-emerald-600' : 'bg-emerald-500'}`}
                      style={{ width: pct }}
                    />
                    {isMay && (
                      <span className="absolute -right-4 top-1/2 -translate-y-1/2 text-xs text-emerald-700 font-semibold">MAY</span>
                    )}
                  </div>
                  <span className="text-xs font-semibold text-gray-900 w-14 text-right flex-shrink-0">
                    {(m.co2 || 0).toFixed(1)} kg
                  </span>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* ── Ride type breakdown ── */}
      {rideTypeData.length > 0 && (
        <div className="bg-white rounded-lg border border-gray-300 p-6 shadow-md">
          <div className="flex items-center gap-2 mb-4">
            <CloudIcon size={15} className="text-gray-700" />
            <h3 className="text-sm font-semibold text-gray-900">Ride Types</h3>
          </div>
          <div className="grid grid-cols-2 gap-3">
            {rideTypeData.map((rt, i) => (
              <div key={rt.name} className={`${TYPE_COLORS[i % TYPE_COLORS.length]} rounded-lg p-4 shadow-sm border border-gray-200`}>
                <p className="text-xl font-bold">{rt.value}</p>
                <p className="text-xs font-medium mt-0.5">{rt.name}</p>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* ── Pooling split ── */}
      {totalRides > 0 && (
        <div className="bg-white rounded-lg border border-gray-300 p-6 shadow-md">
          <div className="flex items-center gap-2 mb-3">
            <LeafIcon size={15} className="text-emerald-600" />
            <h3 className="text-sm font-semibold text-gray-900">Pooling vs Solo</h3>
          </div>
          <div className="flex h-3 rounded-full overflow-hidden gap-0.5 shadow-sm">
            {poolingData.map(p => (
              <div
                key={p.name}
                className={`${p.color} rounded-full transition-all duration-500`}
                style={{ width: `${(p.value / totalRides) * 100}%` }}
              />
            ))}
          </div>
          <div className="flex items-center gap-5 mt-2">
            {poolingData.map(p => (
              <div key={p.name} className="flex items-center gap-1.5">
                <div className={`w-2 h-2 rounded-full ${p.color}`} />
                <span className="text-xs text-gray-600">{p.name}: <strong className="text-gray-900">{p.value}</strong></span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}

function StatCard({ Icon, iconClass, value, label }) {
  return (
    <div className="bg-white rounded-lg p-5 border border-gray-300 text-center shadow-md hover:shadow-lg transition-shadow">
      <div className="flex justify-center mb-3">
        <Icon size={18} className={iconClass} />
      </div>
      <p className="text-2xl font-bold text-gray-900">{value}</p>
      <p className="text-xs text-gray-600 mt-1">{label}</p>
    </div>
  );
}

export default EcoReport;
