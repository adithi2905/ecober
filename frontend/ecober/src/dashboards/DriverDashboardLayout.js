import React, { useEffect, useState } from 'react';
import AvailableRides    from '../components/DriverAvailabletrips';
import DriverCurrentTrip from '../components/DriverCurrentTrip';
import DriverProfile     from '../components/DriverProfile';
import EcoReport         from '../components/ecoReport';
import DriverTripHistory from '../components/DriverTripHistory';
import { LeafIcon, CarIcon, NavigationIcon, HistoryIcon } from '../components/Icons';
import { EcoBadgePill } from '../components/EcoBadge';

const tabs = [
  { name: 'Available Rides', key: 'available', Icon: CarIcon },
  { name: 'Current Trip',    key: 'current',   Icon: NavigationIcon },
  { name: 'Eco Impact',      key: 'eco',        Icon: LeafIcon },
  { name: 'Trip History',    key: 'history',    Icon: HistoryIcon },
];

function DriverDashboardPage() {
  const [selectedTab, setSelectedTab] = useState('available');
  const [tripCounts, setTripCounts]   = useState({ totalRides: 0, ridesThisMonth: 0, ridesToday: 0 });
  const [driverProfile, setDriverProfile] = useState({ driverName: '', ecoBadge: '' });

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) return;

    fetch('http://localhost:8080/driver/me/trip-count', {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(r => r.ok ? r.json() : null)
      .then(d => { if (d) setTripCounts({ totalRides: d.totalRides || 0, ridesThisMonth: d.ridesThisMonth || 0, ridesToday: d.ridesToday || 0 }); })
      .catch(() => {});

    fetch('http://localhost:8080/driver/me/getProfile', {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(r => r.ok ? r.json() : null)
      .then(d => { if (d) setDriverProfile({ driverName: d.driverName || '', ecoBadge: d.ecoBadge || '' }); })
      .catch(() => {});
  }, []);

  const initial     = driverProfile.driverName ? driverProfile.driverName[0].toUpperCase() : 'D';
  const displayName = driverProfile.driverName || 'Driver';
  const currentTab  = tabs.find(t => t.key === selectedTab);

  const renderContent = () => {
    switch (selectedTab) {
      case 'available': return <AvailableRides ridesToday={tripCounts.ridesToday} />;
      case 'current':   return <DriverCurrentTrip />;
      case 'eco':       return <EcoReport />;
      case 'history':   return <DriverTripHistory />;
      case 'profile':   return <DriverProfile />;
      default:          return <AvailableRides />;
    }
  };

  return (
    <div className="flex h-screen bg-slate-50">
      {/* ── Sidebar ── */}
      <aside className="w-64 bg-white border-r border-slate-100 flex flex-col flex-shrink-0">
        {/* Logo */}
        <div className="px-6 py-5 border-b border-slate-100">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-xl flex items-center justify-center shadow-md flex-shrink-0">
              <LeafIcon size={18} className="text-white" />
            </div>
            <div>
              <h2 className="text-base font-bold text-slate-800 leading-tight">Ecober Driver</h2>
              <p className="text-xs text-slate-400">Driver Portal</p>
            </div>
          </div>
        </div>

        {/* Stats row */}
        <div className="px-4 py-3 border-b border-slate-100">
          <div className="grid grid-cols-3 gap-2">
            {[
              { label: 'Total', value: tripCounts.totalRides,     color: 'bg-purple-50 text-purple-700' },
              { label: 'Month', value: tripCounts.ridesThisMonth, color: 'bg-blue-50 text-blue-700' },
              { label: 'Today', value: tripCounts.ridesToday,     color: 'bg-emerald-50 text-emerald-700' },
            ].map(s => (
              <div key={s.label} className={`${s.color} rounded-xl p-2.5 text-center`}>
                <p className="text-lg font-bold leading-tight">{s.value}</p>
                <p className="text-xs opacity-80">{s.label}</p>
              </div>
            ))}
          </div>
        </div>

        {/* Navigation */}
        <nav className="px-4 py-4 space-y-0.5">
          {tabs.map(({ name, key, Icon }) => {
            const active = selectedTab === key;
            return (
              <button
                key={key}
                onClick={() => setSelectedTab(key)}
                className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all duration-150 text-left
                  ${active
                    ? 'bg-blue-500 text-white shadow-sm'
                    : 'text-slate-500 hover:bg-slate-50 hover:text-blue-600'
                  }`}
              >
                <Icon size={16} className={active ? 'text-white' : 'text-slate-400'} />
                {name}
                {active && <span className="ml-auto w-1.5 h-1.5 bg-white rounded-full animate-pulse" />}
              </button>
            );
          })}
        </nav>

        {/* ── Eco Status (visible on every page) ── */}
        {driverProfile.ecoBadge && (
          <div className="px-4 py-3 border-t border-slate-100">
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2 px-1">
              Eco Status
            </p>
            <EcoBadgePill badgeName={driverProfile.ecoBadge} />
          </div>
        )}

        {/* Online indicator */}
        <div className="px-4 py-2">
          <div className="flex items-center gap-2 bg-emerald-50 rounded-xl px-3 py-2.5">
            <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse flex-shrink-0" />
            <span className="text-xs font-medium text-emerald-700">Online & Available</span>
          </div>
        </div>

        <div className="flex-1" />

        {/* Green Streak pill */}
        <div className="px-4 pb-2">
          <div
            className="flex items-center gap-2 px-3 py-2 rounded-xl border"
            style={{ background: 'linear-gradient(135deg, #fef3c7, #d1fae5)', borderColor: '#86efac' }}
          >
            <span className="text-sm">🔥</span>
            <span className="text-xs font-semibold text-emerald-900">Green Streak: 5 days</span>
          </div>
        </div>

        {/* Driver pill */}
        <div
          onClick={() => setSelectedTab('profile')}
          className="px-4 pb-3 pt-2 border-t border-slate-100 cursor-pointer"
        >
          <div className="flex items-center gap-3 bg-slate-50 hover:bg-blue-50 rounded-xl px-3 py-3 transition-all">
            <div className="w-8 h-8 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-full flex items-center justify-center flex-shrink-0">
              <span className="text-white text-xs font-semibold">{initial}</span>
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-sm font-semibold text-slate-700 truncate">{displayName}</p>
              <p className="text-xs text-blue-600">View Profile →</p>
            </div>
          </div>
        </div>

        <div className="px-4 py-3">
          <div className="rounded-lg border border-gray-300 bg-gradient-to-r from-gray-100 to-gray-50 px-4 py-3 shadow-sm">
            <p className="text-xs font-medium text-gray-700 text-center">Performance with purpose</p>
          </div>
        </div>
        <p className="text-xs text-gray-500 text-center pb-4 mt-3">&copy; 2025 Ecober</p>
      </aside>

      {/* ── Main ── */}
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <header className="bg-white border-b border-emerald-100 px-8 py-4 flex-shrink-0">
          <div className="flex justify-between items-center gap-4">
            <div className="flex-1 rounded-3xl border border-emerald-100 bg-slate-50 px-5 py-4 shadow-sm">
              <div className="flex flex-col gap-2">
                <div className="flex items-center gap-2 text-sm font-semibold text-emerald-900">
                  <LeafIcon size={16} className="text-emerald-600" />
                  <span>{currentTab?.name ?? 'Dashboard'}</span>
                </div>
                <p className="text-xs text-slate-500">Welcome back, {displayName}</p>
              </div>
            </div>
            <div
              onClick={() => setSelectedTab('profile')}
              className="flex items-center gap-3 cursor-pointer rounded-2xl border border-slate-200 bg-white px-4 py-2 transition-all hover:shadow-sm"
            >
              <div className="w-10 h-10 rounded-full bg-emerald-600 flex items-center justify-center text-white text-sm font-semibold">
                {initial}
              </div>
              <div className="hidden sm:block">
                <p className="text-sm font-semibold text-slate-900 leading-tight">{displayName}</p>
                <p className="text-xs text-slate-500">Driver</p>
              </div>
            </div>
          </div>
        </header>

        <main className="flex-1 overflow-y-auto">{renderContent()}</main>
      </div>
    </div>
  );
}

export default DriverDashboardPage;
