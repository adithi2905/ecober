import React, { useEffect, useState } from 'react';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Cell } from 'recharts';
import { EcoBadgeFull } from './EcoBadge';
import { LeafIcon, CloudIcon, CarIcon, TrendingDownIcon } from './Icons';

const BADGE_ORDER = ['Green Starter', 'Eco Rider', 'Green Champion', 'Eco Warrior', 'Eco Legend'];
const AVG_CAR_KG  = 2.31;

function UserEcoReport() {
  const [report, setReport]   = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError]     = useState(false);
  const token = localStorage.getItem('token');

  useEffect(() => {
    fetch('http://localhost:8080/user/eco/ecoReport', {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(r => { if (!r.ok) throw new Error(); return r.json(); })
      .then(setReport)
      .catch(() => setError(true))
      .finally(() => setLoading(false));
  }, [token]);

  if (loading) {
    return (
      <div className="p-8 space-y-4 max-w-3xl mx-auto">
        <div className="h-52 bg-white rounded-2xl animate-pulse" />
        <div className="grid grid-cols-3 gap-4">
          {[1, 2, 3].map(i => <div key={i} className="h-28 bg-white rounded-2xl animate-pulse" />)}
        </div>
        <div className="grid grid-cols-2 gap-6">
          <div className="h-56 bg-white rounded-2xl animate-pulse" />
          <div className="h-56 bg-white rounded-2xl animate-pulse" />
        </div>
      </div>
    );
  }

  if (error || !report) {
    return (
      <div className="p-8 flex items-center justify-center min-h-64">
        <p className="text-sm text-slate-500">Could not load Eco Report.</p>
      </div>
    );
  }

  const badgeIndex    = BADGE_ORDER.indexOf(report.ecoBadge);
  const nextBadge     = badgeIndex >= 0 && badgeIndex < BADGE_ORDER.length - 1 ? BADGE_ORDER[badgeIndex + 1] : null;
  const badgeProgress = badgeIndex === -1 ? 20 : ((badgeIndex + 1) / BADGE_ORDER.length) * 100;
  const avgPerTrip    = report.averageEmissionPerTrip ?? 0;
  const totalSaved    = Math.max(0, (AVG_CAR_KG - avgPerTrip) * (report.totalTrips ?? 0));
  const treesYear     = (totalSaved / 22).toFixed(1);
  const milesNotDriven = Math.round(totalSaved / 0.404);
  const pctBetter      = AVG_CAR_KG > 0 ? ((1 - avgPerTrip / AVG_CAR_KG) * 100).toFixed(0) : 0;

  const chartData = [
    { name: 'You',     value: parseFloat(avgPerTrip.toFixed(2)) },
    { name: 'Avg Car', value: AVG_CAR_KG },
  ];

  return (
    <div className="p-8 max-w-3xl mx-auto space-y-6">

      {/* ── Badge hero ── */}
      <div className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden">
        <div className="h-3 bg-gradient-to-r from-emerald-400 via-teal-400 to-cyan-400" />
        <div className="p-6 flex items-center gap-8">
          <EcoBadgeFull badgeName={report.ecoBadge} size={80} />
          <div className="flex-1">
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-1">Badge Progress</p>
            <div className="h-2.5 bg-slate-100 rounded-full overflow-hidden mb-1.5">
              <div
                className="h-full bg-gradient-to-r from-emerald-400 to-teal-500 rounded-full transition-all duration-700"
                style={{ width: `${badgeProgress}%` }}
              />
            </div>
            <div className="flex justify-between text-xs text-slate-400">
              <span>{Math.round(badgeProgress)}% through all levels</span>
              {nextBadge && <span>Next: {nextBadge}</span>}
              {!nextBadge && <span className="text-emerald-600 font-semibold">Max tier reached 🏆</span>}
            </div>
            <div className="flex gap-1.5 mt-3">
              {BADGE_ORDER.map((b, i) => (
                <div
                  key={b}
                  className="flex-1 h-1 rounded-full transition-all"
                  style={{ background: i <= badgeIndex ? '#10b981' : '#e2e8f0' }}
                />
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* ── Metrics ── */}
      <div className="grid grid-cols-3 gap-4">
        <MetricCard Icon={CarIcon}        iconClass="text-blue-400"    label="Total Trips"    value={report.totalTrips}                      unit="" />
        <MetricCard Icon={CloudIcon}      iconClass="text-slate-400"   label="Total Emissions" value={(report.totalEmissions ?? 0).toFixed(1)} unit="kg CO₂" />
        <MetricCard Icon={TrendingDownIcon} iconClass="text-emerald-500" label="Avg per Trip"  value={avgPerTrip.toFixed(2)}                   unit="kg CO₂" sub={`avg car: ${AVG_CAR_KG} kg`} />
      </div>

      {/* ── Two-column section ── */}
      <div className="grid grid-cols-2 gap-6">

        {/* Environmental equivalents */}
        <div className="bg-white rounded-2xl border border-slate-100 p-6">
          <div className="flex items-center gap-2 mb-1">
            <LeafIcon size={15} className="text-emerald-500" />
            <h3 className="text-sm font-semibold text-slate-700">CO₂ Saved vs Solo Car</h3>
          </div>
          <p className="text-2xl font-bold text-emerald-600 mt-1">{totalSaved.toFixed(1)} <span className="text-sm text-slate-400 font-normal">kg</span></p>
          <p className="text-xs text-slate-400 mb-5">compared to driving alone each trip</p>

          <div className="space-y-2.5">
            <EquivRow icon="🌳" value={treesYear}                       label="trees absorbing CO₂ for 1 year" bg="bg-emerald-50" />
            <EquivRow icon="🚗" value={milesNotDriven.toLocaleString()} label="miles not driven solo"           bg="bg-blue-50" />
          </div>
        </div>

        {/* Comparison bar chart */}
        <div className="bg-white rounded-2xl border border-slate-100 p-6 flex flex-col">
          <div className="flex items-center gap-2 mb-1">
            <CloudIcon size={15} className="text-slate-400" />
            <h3 className="text-sm font-semibold text-slate-700">Emissions per Trip</h3>
          </div>
          <p className="text-xs text-slate-400 mb-4">You vs. average car (kg CO₂)</p>
          <div className="flex-1">
            <ResponsiveContainer width="100%" height={150}>
              <BarChart data={chartData} barSize={44} margin={{ top: 4, right: 4, left: -20, bottom: 0 }}>
                <XAxis dataKey="name" tick={{ fontSize: 12, fill: '#64748b' }} axisLine={false} tickLine={false} />
                <YAxis tick={{ fontSize: 11, fill: '#94a3b8' }} axisLine={false} tickLine={false} />
                <Tooltip
                  cursor={{ fill: '#f8fafc' }}
                  contentStyle={{ borderRadius: '8px', border: '1px solid #e2e8f0', fontSize: 12 }}
                  formatter={v => [`${v} kg CO₂`, 'Emissions']}
                />
                <Bar dataKey="value" radius={[6, 6, 0, 0]}>
                  {chartData.map((_, i) => <Cell key={i} fill={i === 0 ? '#10b981' : '#cbd5e1'} />)}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>
          {avgPerTrip < AVG_CAR_KG && (
            <div className="flex items-center gap-1.5 mt-3 text-xs text-emerald-600 font-medium">
              <LeafIcon size={12} />
              You emit {pctBetter}% less than an average car
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

function MetricCard({ Icon, iconClass, label, value, unit, sub }) {
  return (
    <div className="bg-white rounded-2xl p-5 border border-slate-100">
      <Icon size={18} className={`${iconClass} mb-3`} />
      <p className="text-xs text-slate-400 uppercase tracking-wide font-medium mb-1">{label}</p>
      <p className="text-2xl font-bold text-slate-800">{value}</p>
      {unit && <p className="text-xs text-slate-500 font-medium mt-0.5">{unit}</p>}
      {sub  && <p className="text-xs text-slate-400 mt-1">{sub}</p>}
    </div>
  );
}

function EquivRow({ icon, value, label, bg }) {
  return (
    <div className={`flex items-center gap-3 p-3 ${bg} rounded-xl`}>
      <span className="text-xl flex-shrink-0">{icon}</span>
      <div>
        <p className="text-sm font-bold text-slate-800 leading-tight">{value}</p>
        <p className="text-xs text-slate-500">{label}</p>
      </div>
    </div>
  );
}

export default UserEcoReport;
