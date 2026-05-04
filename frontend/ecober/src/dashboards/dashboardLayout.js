import React, { useEffect, useState } from 'react';
import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom';
import { LeafIcon, MapPinIcon, NavigationIcon, HistoryIcon, UserIcon } from '../components/Icons';
import { EcoBadgePill } from '../components/EcoBadge';

const navLinks = [
  { name: 'Book Ride',    path: '/rideBooking',  Icon: MapPinIcon },
  { name: 'Current Ride', path: '/currentRide',  Icon: NavigationIcon },
  { name: 'Past Rides',   path: '/tripsHistory', Icon: HistoryIcon },
  { name: 'Eco Report',   path: '/ecoReport',    Icon: LeafIcon },
];

function DashboardLayout() {
  const location  = useLocation();
  const navigate  = useNavigate();
  const [profile, setProfile] = useState({ username: '', ecoBadge: '', totalCO2Saved: 0 });

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) return;
    fetch('http://localhost:8080/user/profile', {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(r => r.ok ? r.json() : null)
      .then(d => {
        if (d) setProfile({
          username:     d.username     || '',
          ecoBadge:     d.ecoBadge     || '',
          totalCO2Saved: d.totalCO2Saved ?? 0,
        });
      })
      .catch(() => {});
  }, []);

  const initial     = profile.username ? profile.username[0].toUpperCase() : '?';
  const displayName = profile.username || 'Rider';
  const currentPage =
    navLinks.find(l => l.path === location.pathname)?.name
    ?? (location.pathname === '/profile' ? 'Profile' : 'Dashboard');

  return (
    <div className="flex h-screen bg-slate-50">
      {/* ── Sidebar ── */}
      <aside className="w-64 bg-white border-r border-slate-100 flex flex-col flex-shrink-0">
        {/* Logo */}
        <div className="px-6 py-5 border-b border-slate-100">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 bg-gradient-to-br from-emerald-500 to-teal-600 rounded-xl flex items-center justify-center shadow-md flex-shrink-0">
              <LeafIcon size={18} className="text-white" />
            </div>
            <div>
              <h2 className="text-base font-bold text-slate-800 leading-tight">Ecober</h2>
              <p className="text-xs text-slate-400">Eco-friendly rides</p>
            </div>
          </div>
        </div>

        {/* Navigation */}
        <nav className="px-4 py-5 space-y-0.5">
          {navLinks.map(({ name, path, Icon }) => {
            const active = location.pathname === path;
            return (
              <Link
                key={path}
                to={path}
                className={`flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all duration-150
                  ${active
                    ? 'bg-emerald-500 text-white shadow-sm'
                    : 'text-slate-500 hover:bg-slate-50 hover:text-emerald-600'
                  }`}
              >
                <Icon size={16} className={active ? 'text-white' : 'text-slate-400'} />
                {name}
              </Link>
            );
          })}
        </nav>

        {/* ── Eco Status (visible on every page) ── */}
        {profile.ecoBadge && (
          <div className="px-4 py-3 border-t border-slate-100">
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2 px-1">
              Eco Status
            </p>
            <EcoBadgePill
              badgeName={profile.ecoBadge}
              co2Saved={profile.totalCO2Saved}
            />
          </div>
        )}

        {/* Spacer */}
        <div className="flex-1" />

        {/* User pill */}
        <div
          onClick={() => navigate('/profile')}
          className="px-4 pb-3 pt-2 border-t border-slate-100 cursor-pointer"
        >
          <div className="flex items-center gap-3 bg-slate-50 hover:bg-emerald-50 rounded-xl px-3 py-3 transition-all">
            <div className="w-8 h-8 bg-gradient-to-br from-emerald-500 to-teal-600 rounded-full flex items-center justify-center flex-shrink-0">
              <span className="text-white text-xs font-semibold">{initial}</span>
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-sm font-semibold text-slate-700 truncate">{displayName}</p>
              <p className="text-xs text-emerald-600">View Profile →</p>
            </div>
          </div>
        </div>

        <p className="text-xs text-slate-300 text-center pb-4">&copy; 2025 Ecober</p>
      </aside>

      {/* ── Main ── */}
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        {/* Header */}
        <header className="bg-white border-b border-slate-100 px-8 py-4 flex-shrink-0">
          <div className="flex justify-between items-center">
            <div>
              <h1 className="text-lg font-bold text-slate-800">{currentPage}</h1>
              <p className="text-xs text-slate-400 mt-0.5 flex items-center gap-1">
                <LeafIcon size={12} className="text-emerald-500" />
                Welcome back, {displayName}
              </p>
            </div>
            <div
              onClick={() => navigate('/profile')}
              className="flex items-center gap-2.5 cursor-pointer bg-slate-50 hover:bg-slate-100 rounded-xl px-3 py-2 transition-all"
            >
              <div className="w-8 h-8 bg-gradient-to-br from-emerald-500 to-teal-600 rounded-full flex items-center justify-center flex-shrink-0">
                <span className="text-white text-xs font-semibold">{initial}</span>
              </div>
              <div className="hidden sm:block">
                <p className="text-sm font-semibold text-slate-700 leading-tight">{displayName}</p>
                <p className="text-xs text-slate-400">Rider</p>
              </div>
            </div>
          </div>
        </header>

        <main className="flex-1 overflow-y-auto">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

export default DashboardLayout;
