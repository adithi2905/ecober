import React, { useEffect, useState } from 'react';
import { RealWorldTile, Co2Equiv } from './EcoUtils';
import { LeafIcon, CloudIcon, CarIcon } from './Icons';

const AVG_CAR_KG    = 2.31;
const MONTH_NAMES   = ['JAN','FEB','MAR','APR','MAY','JUN','JUL','AUG','SEP','OCT','NOV','DEC'];

function UserEcoReport() {
  const [report, setReport]           = useState(null);
  const [trips, setTrips]             = useState([]);
  const [loading, setLoading]         = useState(true);
  const [target, setTarget]           = useState(30);
  const [newTarget, setNewTarget]     = useState('');
  const [editingTarget, setEditingTarget] = useState(false);
  const token = localStorage.getItem('token');

  useEffect(() => {
    Promise.all([
      fetch('http://localhost:8080/user/eco/ecoReport',  { headers: { Authorization: `Bearer ${token}` } }),
      fetch('http://localhost:8080/user/tripsHistory',   { headers: { Authorization: `Bearer ${token}` } }),
    ])
      .then(async ([ecoRes, tripsRes]) => {
        const [eco, tripsData] = await Promise.all([
          ecoRes.ok   ? ecoRes.json()   : {},
          tripsRes.ok ? tripsRes.json() : [],
        ]);
        setReport(eco);
        setTrips(tripsData || []);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [token]);

  if (loading) {
    return (
      <div className="p-8 space-y-4 max-w-2xl mx-auto">
        {[1, 2, 3].map(i => <div key={i} className="bg-white rounded-2xl h-28 animate-pulse" />)}
      </div>
    );
  }

  if (!report) {
    return (
      <div className="p-8 flex items-center justify-center min-h-64">
        <p className="text-sm text-slate-400">Could not load Eco Report.</p>
      </div>
    );
  }

  const totalTrips   = report.totalTrips    ?? 0;
  const totalEmit    = report.totalEmissions ?? 0;
  const totalSaved   = Math.max(0, totalTrips * AVG_CAR_KG - totalEmit);
  const pooledRides  = trips.filter(t => t.is_pooling === 1).length;

  // Build monthly savings from trip history
  const monthMap = {};
  trips.forEach(t => {
    const d = t.endTime || t.startTime;
    if (!d) return;
    const key    = MONTH_NAMES[new Date(d).getMonth()];
    const cc     = t.carbonCost ?? t.route?.carbonCost ?? AVG_CAR_KG;
    monthMap[key] = (monthMap[key] || 0) + Math.max(0, AVG_CAR_KG - cc);
  });
  const monthlyData       = Object.entries(monthMap).map(([month, co2]) => ({ month, co2 }));
  const maxCo2            = Math.max(...monthlyData.map(m => m.co2), 1);
  const currentMonthKey   = MONTH_NAMES[new Date().getMonth()];
  const currentMonthSaved = monthMap[currentMonthKey] || 0;
  const goalProgress      = Math.min((currentMonthSaved / target) * 100, 100);

  const treesEquiv = (totalSaved / 22).toFixed(1);
  const flights    = (totalSaved / 250).toFixed(2);
  const houseDays  = (totalSaved / 4.4).toFixed(1);

  const handleSetTarget = () => {
    const v = parseFloat(newTarget);
    if (!isNaN(v) && v > 0) { setTarget(v); setEditingTarget(false); setNewTarget(''); }
  };

  return (
    <div className="p-8 max-w-2xl mx-auto space-y-6">

      {/* Monthly CO₂ Goal card */}
      <div className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden">
        <div className="h-2 bg-gradient-to-r from-emerald-400 to-teal-500" />
        <div className="p-6">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-1.5">
              <LeafIcon size={14} className="text-emerald-500" />
              <h3 className="text-sm font-semibold text-slate-700">Monthly CO₂ Goal</h3>
            </div>
            <button
              onClick={() => setEditingTarget(!editingTarget)}
              className="text-xs px-2.5 py-1 rounded-lg bg-green-50 border border-green-200 text-emerald-700 font-medium hover:bg-green-100 transition-colors"
            >
              {editingTarget ? 'Cancel' : 'Set Target'}
            </button>
          </div>

          {editingTarget && (
            <div className="flex gap-2 mb-4">
              <input
                type="number"
                placeholder="Target (kg CO₂)"
                value={newTarget}
                onChange={e => setNewTarget(e.target.value)}
                className="border border-slate-200 rounded-lg px-3 py-1.5 text-sm w-36 focus:outline-none focus:ring-2 focus:ring-emerald-300"
              />
              <button
                onClick={handleSetTarget}
                className="px-3 py-1.5 rounded-lg bg-emerald-600 text-white text-sm font-medium hover:bg-emerald-700"
              >
                Save
              </button>
            </div>
          )}

          <div className="flex items-baseline justify-between mb-2">
            <span className="text-2xl font-bold text-emerald-600">
              {currentMonthSaved.toFixed(2)}
              <span className="text-sm font-normal text-slate-400 ml-1">kg saved</span>
            </span>
            <span className="text-xs text-slate-400">target: {target} kg</span>
          </div>
          <div className="h-2.5 bg-slate-100 rounded-full">
            <div
              className="h-full bg-gradient-to-r from-emerald-400 to-teal-500 rounded-full transition-all duration-700"
              style={{
                width: `${goalProgress}%`,
                boxShadow: goalProgress > 0 ? '0 0 8px 1px rgba(16,185,129,0.55)' : 'none',
              }}
            />
          </div>
          <div className="flex justify-between mt-1.5">
            <span className="text-xs text-slate-400">{goalProgress.toFixed(0)}% complete</span>
            {goalProgress >= 100 && <span className="text-xs text-emerald-600 font-semibold">Goal reached! 🌟</span>}
          </div>
          {currentMonthSaved > 0 && <Co2Equiv kg={currentMonthSaved} className="mt-2" />}
        </div>
      </div>

      {/* Stat cards */}
      <div className="grid grid-cols-3 gap-4">
        <StatCard Icon={LeafIcon}  iconClass="text-emerald-500" value={totalSaved.toFixed(1)} unit="kg CO₂" label="total saved" />
        <StatCard Icon={CarIcon}   iconClass="text-blue-400"    value={totalTrips}             unit=""       label="total rides" />
        <StatCard Icon={CloudIcon} iconClass="text-teal-500"    value={pooledRides}            unit=""       label="pooled rides" />
      </div>

      {/* Real-world impact */}
      {totalSaved > 0 && (
        <div className="grid grid-cols-3 gap-3">
          <RealWorldTile icon="🌳" value={treesEquiv} label="trees for 1 year" />
          <RealWorldTile icon="✈️" value={flights}    label="flights offset" />
          <RealWorldTile icon="💡" value={houseDays}  label="household days" />
        </div>
      )}

      {/* Monthly savings bar chart */}
      {monthlyData.length > 0 && (
        <div className="bg-white rounded-2xl border border-slate-100 p-6">
          <div className="flex items-center gap-2 mb-4">
            <LeafIcon size={14} className="text-emerald-500" />
            <h3 className="text-sm font-semibold text-slate-700">Monthly CO₂ Savings</h3>
          </div>
          <div className="space-y-3">
            {monthlyData.map(m => {
              const isCurrent = m.month === currentMonthKey;
              return (
                <div key={m.month} className="flex items-center gap-3">
                  <span className="text-xs text-slate-400 font-medium w-8 flex-shrink-0">{m.month}</span>
                  <div className="flex-1 h-2.5 bg-slate-100 rounded-full overflow-hidden">
                    <div
                      className="h-full rounded-full transition-all duration-500"
                      style={{
                        width: `${(m.co2 / maxCo2) * 100}%`,
                        background: isCurrent ? '#10b981' : '#6ee7b7',
                      }}
                    />
                  </div>
                  <div className="flex items-center gap-1 flex-shrink-0 w-16 justify-end">
                    {isCurrent && (
                      <svg width="10" height="11" viewBox="0 0 64 72" fill="none">
                        <path d="M32 68 C32 68 6 50 6 28 C6 12 18 3 32 3 C46 3 58 12 58 28 C58 50 32 68 32 68Z" fill="#10b981" />
                      </svg>
                    )}
                    <span className="text-xs font-semibold text-slate-700">{m.co2.toFixed(1)} kg</span>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
}

function StatCard({ Icon, iconClass, value, unit, label }) {
  return (
    <div className="bg-green-50 border border-green-200 rounded-2xl p-5 text-center">
      <div className="flex justify-center mb-2">
        <Icon size={17} className={iconClass} />
      </div>
      <p className="text-2xl font-bold text-slate-800">{value}</p>
      {unit && <p className="text-xs text-slate-500 font-medium">{unit}</p>}
      <p className="text-xs text-slate-400 mt-1">{label}</p>
    </div>
  );
}

export default UserEcoReport;
