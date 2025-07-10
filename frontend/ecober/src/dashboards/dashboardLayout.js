import React from 'react';
import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom';

const navLinks = [
  { name: 'Book Ride', path: '/rideBooking' },
  { name: 'Current Rides', path: '/currentRide' },
  { name: 'Past Rides', path: '/tripsHistory' },
];

function DashboardLayout() {
  const location = useLocation();
  const navigate = useNavigate();

  return (
    <div className="flex h-screen bg-gradient-to-br from-slate-50 to-slate-100">
      {/* Sidebar */}
      <aside className="w-72 bg-white shadow-xl border-r border-slate-200 flex flex-col">
        {/* Logo Section */}
        <div className="p-8 border-b border-slate-100">
          <div className="flex items-center space-x-4">
            <div className="w-12 h-12 bg-gradient-to-br from-emerald-500 to-teal-600 rounded-2xl flex items-center justify-center shadow-lg">
              <span className="text-white text-xl font-bold">E</span>
            </div>
            <div>
              <h2 className="text-2xl font-bold text-slate-800">Ecober</h2>
              <p className="text-sm text-slate-500">Eco-friendly rides</p>
            </div>
          </div>
        </div>

        {/* Navigation */}
        <nav className="flex-1 px-6 py-8 space-y-3">
          {navLinks.map((link) => {
            const isActive = location.pathname === link.path;

            return (
              <Link
                key={link.path}
                to={link.path}
                className={`group flex items-center space-x-4 px-5 py-4 rounded-xl transition-all duration-300 font-medium
                  ${
                    isActive
                      ? 'bg-gradient-to-r from-emerald-500 to-teal-600 text-white shadow-lg transform scale-105'
                      : 'text-slate-600 hover:bg-slate-100 hover:text-slate-800 hover:transform hover:scale-102'
                  }
                `}
              >
                <div
                  className={`w-2 h-2 rounded-full ${
                    isActive
                      ? 'bg-white'
                      : 'bg-slate-400 group-hover:bg-slate-600'
                  }`}
                ></div>
                <span>{link.name}</span>
              </Link>
            );
          })}
        </nav>

        {/* User Section */}
        <div
          onClick={() => navigate('/profile')}
          className="p-6 border-t border-slate-100 cursor-pointer"
        >
          <div className="flex items-center space-x-3 bg-gradient-to-r from-slate-50 to-slate-100 rounded-xl px-4 py-3 hover:from-slate-100 hover:to-slate-200 transition-all duration-200">
            <div className="w-10 h-10 bg-gradient-to-br from-emerald-500 to-teal-600 rounded-full flex items-center justify-center">
              <span className="text-white text-sm font-semibold">A</span>
            </div>
            <div className="flex-1">
              <p className="text-sm font-semibold text-slate-700">Adithi</p>
              <p className="text-xs text-slate-500">Premium Member</p>
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="p-6 text-center">
          <p className="text-xs text-slate-400">
            &copy; 2025 Ecober. All rights reserved.
          </p>
        </div>
      </aside>

      {/* Main Content */}
      <div className="flex-1 flex flex-col min-w-0">
        {/* Header */}
        <header className="bg-white shadow-lg border-b border-slate-200 px-8 py-6">
          <div className="flex justify-between items-center">
            <div>
              <h1 className="text-3xl font-bold text-slate-800">Dashboard</h1>
              <p className="text-sm text-slate-500 mt-2">
                Welcome back! Here's what's happening today.
              </p>
            </div>

            
              {/* User Profile (Clickable) */}
              <div
                onClick={() => navigate('/profile')}
                className="flex items-center space-x-3 bg-gradient-to-r from-slate-50 to-slate-100 rounded-xl px-4 py-2 hover:from-slate-100 hover:to-slate-200 transition-all duration-200 cursor-pointer"
              >
                <div className="w-10 h-10 bg-gradient-to-br from-emerald-500 to-teal-600 rounded-full flex items-center justify-center">
                  <span className="text-white text-sm font-semibold">A</span>
                </div>
                <div className="hidden sm:block">
                  <p className="text-sm font-semibold text-slate-700">Adithi</p>
                  <p className="text-xs text-slate-500">Premium Member</p>
                </div>
              </div>
          </div>
        </header>

        {/* Main Page Content */}
        <main className="flex-1 overflow-y-auto bg-gradient-to-br from-slate-50 to-slate-100 p-8">
          <div className="animate-fade-in">
            <Outlet /> {/* This is where UserEcoReport will render */}
          </div>
        </main>
      </div>
    </div>
  );
}

export default DashboardLayout;
