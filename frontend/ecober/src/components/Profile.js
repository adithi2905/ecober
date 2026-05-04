import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { EcoBadgeFull } from './EcoBadge';
import { LeafIcon, StarIcon, CloudIcon } from './Icons';

function Profile() {
  const [rider, setRider]     = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const token    = localStorage.getItem('token');

  useEffect(() => {
    fetch('http://localhost:8080/user/profile', {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(r => { if (!r.ok) throw new Error(); return r.json(); })
      .then(setRider)
      .catch(() => navigate('/login'))
      .finally(() => setLoading(false));
  }, [navigate, token]);

  const handleLogout = e => {
    e.preventDefault();
    localStorage.removeItem('token');
    navigate('/login');
  };

  if (loading) {
    return (
      <div className="p-8 max-w-xl mx-auto space-y-4">
        <div className="h-56 bg-white rounded-2xl animate-pulse" />
        <div className="grid grid-cols-2 gap-4">
          {[1, 2, 3, 4].map(i => <div key={i} className="h-24 bg-white rounded-2xl animate-pulse" />)}
        </div>
      </div>
    );
  }

  if (!rider) return null;

  const initial     = rider.username?.[0]?.toUpperCase() ?? '?';
  const co2Saved    = rider.totalCO2Saved ?? 0;
  const avgSaved    = rider.averageCO2Saved ?? 0;
  const treesEquiv  = (co2Saved / 22).toFixed(1);

  return (
    <div className="p-8 max-w-xl mx-auto space-y-5">
      {/* Profile + Badge card */}
      <div className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden">
        {/* Top banner */}
        <div className="h-24 bg-gradient-to-r from-emerald-500 via-teal-500 to-cyan-500 relative">
          <div className="absolute inset-0 opacity-20"
            style={{ backgroundImage: 'radial-gradient(circle at 20% 50%, white 1px, transparent 1px), radial-gradient(circle at 80% 20%, white 1px, transparent 1px)', backgroundSize: '32px 32px' }}
          />
        </div>

        <div className="px-6 pb-6 -mt-10 flex items-end gap-5">
          {/* Avatar */}
          <div className="w-20 h-20 bg-white rounded-2xl border-4 border-white shadow-lg flex items-center justify-center flex-shrink-0">
            <span className="text-2xl font-bold text-emerald-600">{initial}</span>
          </div>
          <div className="pb-1">
            <h2 className="text-xl font-bold text-slate-800">{rider.username}</h2>
            <p className="text-xs text-slate-400 flex items-center gap-1 mt-0.5">
              <LeafIcon size={12} className="text-emerald-500" /> Eco Rider
            </p>
          </div>
        </div>

        {/* Badge section */}
        <div className="mx-6 mb-6 bg-slate-50 rounded-2xl p-5 flex items-center gap-5">
          <EcoBadgeFull badgeName={rider.ecoBadge} size={72} />
          <div className="flex-1 space-y-3">
            <div>
              <p className="text-xs text-slate-400 uppercase tracking-wide font-medium">Total Rides</p>
              <p className="text-2xl font-bold text-slate-800">{rider.tripCount}</p>
            </div>
            <div>
              <p className="text-xs text-slate-400 uppercase tracking-wide font-medium">CO₂ Saved</p>
              <p className="text-xl font-bold text-emerald-600">{co2Saved.toFixed(1)} <span className="text-sm font-normal text-slate-400">kg</span></p>
            </div>
          </div>
        </div>
      </div>

      {/* Stats grid */}
      <div className="grid grid-cols-2 gap-4">
        <StatCard
          Icon={CloudIcon}
          iconClass="text-teal-500"
          label="Avg CO₂ per Ride"
          value={`${avgSaved.toFixed(2)} kg`}
          sub="vs solo car travel"
        />
        <StatCard
          Icon={LeafIcon}
          iconClass="text-green-500"
          label="Trees Equivalent"
          value={treesEquiv}
          sub="1 yr CO₂ absorption"
        />
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

function StatCard({ Icon, iconClass, label, value, sub }) {
  return (
    <div className="bg-white rounded-2xl p-5 border border-slate-100">
      <Icon size={18} className={`${iconClass} mb-3`} />
      <p className="text-xl font-bold text-slate-800">{value}</p>
      <p className="text-sm text-slate-600 font-medium mt-1">{label}</p>
      {sub && <p className="text-xs text-slate-400 mt-0.5">{sub}</p>}
    </div>
  );
}

export default Profile;
