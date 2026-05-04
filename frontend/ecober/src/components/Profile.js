import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { BadgeShield } from './EcoBadge';
import { LeafIcon, StarIcon, CarIcon } from './Icons';

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
      <div className="p-6 max-w-lg mx-auto" style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
        <div className="bg-white rounded-xl animate-pulse" style={{ height: '120px' }} />
        <div className="bg-white rounded-xl animate-pulse" style={{ height: '112px' }} />
        <div className="grid grid-cols-3" style={{ gap: '12px' }}>
          {[1, 2, 3].map(i => <div key={i} className="bg-white rounded-xl animate-pulse" style={{ height: '96px' }} />)}
        </div>
      </div>
    );
  }

  if (!rider) return null;

  const initial    = rider.username?.[0]?.toUpperCase() ?? '?';
  const co2Saved   = rider.totalCO2Saved ?? 0;
  const tripCount  = rider.tripCount ?? 0;
  const treesEquiv = (co2Saved / 22).toFixed(1);
  const avgCO2     = tripCount > 0 ? (co2Saved / tripCount).toFixed(2) : '0.00';

  return (
    <div className="p-6 max-w-lg mx-auto" style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>

      {/* 1. Banner — standalone, no avatar */}
      <div
        className="w-full rounded-xl overflow-hidden"
        style={{ height: '100px', background: 'linear-gradient(to right, #10b981, #0d9488)', position: 'relative' }}
      >
        <div
          style={{
            position:        'absolute',
            inset:           0,
            backgroundImage: 'radial-gradient(circle, white 1px, transparent 1px)',
            backgroundSize:  '24px 24px',
            opacity:         0.08,
          }}
        />
      </div>

      {/* 2. Profile info card — avatar lives inside, no overlap */}
      <div className="bg-white border border-gray-100 rounded-xl p-5" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <div
          className="bg-emerald-600 rounded-full flex items-center justify-center mb-3"
          style={{ width: '56px', height: '56px' }}
        >
          <span className="text-xl font-bold text-white">{initial}</span>
        </div>
        <p className="text-base font-medium text-slate-800">{rider.username}</p>
        <span className="mt-2 bg-emerald-100 text-emerald-700 text-xs px-3 py-1 rounded-full">
          Eco Rider
        </span>
      </div>

      {/* 3. Stats row — Total Rides / CO₂ Saved / Trees Equivalent */}
      <div className="grid grid-cols-3" style={{ gap: '12px' }}>
        <StatCard Icon={CarIcon}  iconClass="text-blue-400"    label="Total Rides"  value={tripCount} />
        <StatCard Icon={LeafIcon} iconClass="text-emerald-500" label="CO₂ Saved"    value={`${co2Saved.toFixed(1)}kg`} />
        <StatCard Icon={StarIcon} iconClass="text-amber-400"   label="Trees Equiv." value={treesEquiv} />
      </div>

      {/* 4. Eco Champion banner */}
      {rider.ecoBadge && (
        <div className="bg-green-50 border border-green-200 rounded-xl p-4" style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <BadgeShield badgeName={rider.ecoBadge} size={40} />
          <div>
            <p className="font-bold text-emerald-700">Eco Champion</p>
            <p className="text-sm text-emerald-600">Top 10% of EcoBer riders this month</p>
          </div>
        </div>
      )}

      {/* 5. Detail cards — Avg CO₂ per ride + Trees planted */}
      <div className="grid grid-cols-2" style={{ gap: '12px' }}>
        <DetailCard
          Icon={LeafIcon}
          iconClass="text-emerald-500"
          value={`${avgCO2} kg`}
          label="Avg CO₂ / Ride"
          subLabel="Per trip saved"
        />
        <DetailCard
          Icon={StarIcon}
          iconClass="text-amber-400"
          value={treesEquiv}
          label="Trees Equivalent"
          subLabel="Lifetime impact"
        />
      </div>

      {/* 6. Sign Out */}
      <div style={{ textAlign: 'center', marginTop: '4px' }}>
        <a
          href="#sign-out"
          onClick={handleLogout}
          className="text-sm text-gray-400 underline"
        >
          Sign Out
        </a>
      </div>

    </div>
  );
}

function StatCard({ Icon, iconClass, label, value }) {
  return (
    <div className="bg-white rounded-xl border border-gray-100" style={{ padding: '16px', textAlign: 'center' }}>
      <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '8px' }}>
        <Icon size={18} className={iconClass} />
      </div>
      <p className="text-lg font-bold text-slate-800">{value}</p>
      <p className="text-xs text-slate-400" style={{ marginTop: '4px' }}>{label}</p>
    </div>
  );
}

function DetailCard({ Icon, iconClass, value, label, subLabel }) {
  return (
    <div className="bg-white rounded-xl border border-gray-100" style={{ padding: '16px', textAlign: 'center' }}>
      <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '8px' }}>
        <Icon size={18} className={iconClass} />
      </div>
      <p className="text-xl font-bold text-slate-800">{value}</p>
      <p className="text-sm text-slate-600" style={{ marginTop: '4px' }}>{label}</p>
      <p className="text-xs text-slate-400" style={{ marginTop: '2px' }}>{subLabel}</p>
    </div>
  );
}

export default Profile;
