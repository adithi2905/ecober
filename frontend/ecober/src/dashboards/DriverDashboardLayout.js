import React, { useEffect, useState } from 'react';
import AvailableRides from '../components/DriverAvailabletrips';
import DriverCurrentTrip from '../components/DriverCurrentTrip';
import DriverProfile from '../components/DriverProfile';
import EcoReport from '../components/ecoReport';
import DriverTripHistory from '../components/DriverTripHistory';

const tabs = [
  { name: 'Available Rides', key: 'available', color: 'from-blue-500 to-blue-600', bgColor: 'bg-blue-500' },
  { name: 'Current Trip', key: 'current', color: 'from-emerald-500 to-emerald-600', bgColor: 'bg-emerald-500' },
  { name: 'Carbon Impact', key: 'eco', color: 'from-teal-500 to-emerald-600', bgColor: 'bg-teal-500' },
  { name: 'Trip History', key: 'history', color: 'from-amber-500 to-amber-600', bgColor: 'bg-amber-500' }
];

function DriverDashboardPage() {
  const [selectedTab, setSelectedTab] = useState('available');
  const [tripCounts, setTripCounts] = useState({
    totalRides: 0,
    ridesThisMonth: 0,
    ridesToday: 0,
  });
  const [loadingStats, setLoadingStats] = useState(true);

  useEffect(() => {
    const fetchTripCounts = async () => {
      try {
        const token = localStorage.getItem("token");
        const response = await fetch("http://localhost:8080/driver/me/trip-count", {
          headers: { Authorization: `Bearer ${token}` },
        });

        if (response.ok) {
          const data = await response.json();
          setTripCounts({
            totalRides: data.totalRides || 0,
            ridesThisMonth: data.ridesThisMonth || 0,
            ridesToday: data.ridesToday || 0,
          });
        } else {
          console.error("Failed to fetch trip counts.");
        }
      } catch (error) {
        console.error("Error fetching trip counts:", error);
      } finally {
        setLoadingStats(false);
      }
    };

    fetchTripCounts();
  }, []);

  const renderContent = () => {
    switch (selectedTab) {
      case 'available':
        return <AvailableRides />;
      case 'current':
        return <DriverCurrentTrip />;
      case 'eco':
        return <EcoReport />;
      case 'history':
        return <DriverTripHistory />;
      case 'profile':
        return <DriverProfile />;
      default:
        return <AvailableRides />;
    }
  };

  const activeTab = tabs.find(t => t.key === selectedTab);

  return (
    <div className="flex h-screen bg-gradient-to-br from-slate-50 to-slate-100">
      {/* Sidebar */}
      <aside className="w-80 bg-white shadow-xl border-r border-slate-200 flex flex-col">
        {/* Logo */}
        <div className="p-8 border-b border-slate-100">
          <div className="flex items-center space-x-4">
            <div className="w-12 h-12 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-2xl flex items-center justify-center shadow-lg">
              <span className="text-white text-xl font-bold">D</span>
            </div>
            <div>
              <h2 className="text-2xl font-bold text-slate-800">Ecober Driver</h2>
              <p className="text-sm text-slate-500">Professional Dashboard</p>
            </div>
          </div>
        </div>

        {/* Driver Stats */}
        <div className="p-6 border-b border-slate-100">
          {loadingStats ? (
            <p className="text-center text-slate-400 animate-pulse">Loading stats...</p>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              {/* Total Rides */}
              <div className="bg-gradient-to-br from-purple-50 to-purple-100 p-4 rounded-xl">
                <div className="flex items-center space-x-2 mb-2">
                  <div className="w-3 h-3 bg-purple-500 rounded-full"></div>
                  <span className="text-xs font-medium text-purple-700">Total Rides</span>
                </div>
                <p className="text-2xl font-bold text-purple-700">{tripCounts.totalRides}</p>
              </div>

              {/* Rides This Month */}
              <div className="bg-gradient-to-br from-blue-50 to-indigo-50 p-4 rounded-xl">
                <div className="flex items-center space-x-2 mb-2">
                  <div className="w-3 h-3 bg-blue-500 rounded-full"></div>
                  <span className="text-xs font-medium text-blue-700">This Month</span>
                </div>
                <p className="text-2xl font-bold text-blue-700">{tripCounts.ridesThisMonth}</p>
              </div>

              {/* Rides Today */}
              <div className="bg-gradient-to-br from-emerald-50 to-teal-50 p-4 rounded-xl">
                <div className="flex items-center space-x-2 mb-2">
                  <div className="w-3 h-3 bg-emerald-500 rounded-full"></div>
                  <span className="text-xs font-medium text-emerald-700">Today</span>
                </div>
                <p className="text-2xl font-bold text-emerald-800">{tripCounts.ridesToday}</p>
              </div>
            </div>
          )}
        </div>

        {/* Navigation */}
        <nav className="flex-1 px-6 py-8 space-y-3">
          {tabs.map((tab) => {
            const isActive = selectedTab === tab.key;
            return (
              <button
                key={tab.key}
                onClick={() => setSelectedTab(tab.key)}
                className={`group w-full flex items-center space-x-4 px-5 py-4 rounded-xl transition-all duration-300 text-left font-medium
                  ${isActive
                    ? `bg-gradient-to-r ${tab.color} text-white shadow-lg transform scale-105`
                    : 'text-slate-600 hover:bg-slate-100 hover:text-slate-800 hover:transform hover:scale-102'
                  }
                `}
              >
                <div className={`w-3 h-3 rounded-full ${isActive ? 'bg-white' : `${tab.bgColor} opacity-60 group-hover:opacity-100`}`}></div>
                <span>{tab.name}</span>
                {isActive && (
                  <div className="ml-auto w-2 h-2 bg-white rounded-full animate-pulse" />
                )}
              </button>
            );
          })}
        </nav>

        {/* Online Status */}
        <div className="px-6 py-4">
          <div className="flex items-center justify-between p-4 bg-gradient-to-r from-emerald-50 to-teal-50 rounded-xl">
            <div className="flex items-center space-x-3">
              <div className="w-4 h-4 bg-emerald-500 rounded-full animate-pulse"></div>
              <span className="text-sm font-medium text-emerald-700">Online & Available</span>
            </div>
            <div className="w-6 h-6 bg-emerald-500 rounded-lg"></div>
          </div>
        </div>

        {/* Footer */}
        <div className="p-6 text-center border-t border-slate-100">
          <p className="text-xs text-slate-400">&copy; 2025 Ecober Driver. All rights reserved.</p>
        </div>
      </aside>

      {/* Main Content */}
      <div className="flex-1 flex flex-col min-w-0">
        {/* Header */}
        <header className="bg-white shadow-lg border-b border-slate-200 px-8 py-6">
          <div className="flex justify-between items-center">
            <div>
              <h1 className="text-3xl font-bold text-slate-800">{activeTab?.name}</h1>
              <p className="text-sm text-slate-500 mt-2">
                {selectedTab === 'available' && 'Find your next ride opportunity'}
                {selectedTab === 'current' && 'Manage your ongoing trip'}
                {selectedTab === 'eco' && 'Track your environmental impact'}
                {selectedTab === 'history' && 'Review your completed trips'}
                {selectedTab === 'profile' && 'View or edit your driver profile'}
              </p>
            </div>

            {/* Driver Avatar */}
            <div
              onClick={() => setSelectedTab('profile')}
              className="flex items-center space-x-3 bg-gradient-to-r from-slate-50 to-slate-100 rounded-xl px-4 py-2 hover:from-slate-100 hover:to-slate-200 transition-all duration-200 cursor-pointer"
            >
              <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-full flex items-center justify-center">
                <span className="text-white text-sm font-semibold">D</span>
              </div>
              <div className="hidden sm:block">
                <p className="text-sm font-semibold text-slate-700">Driver</p>
                <p className="text-xs text-slate-500">Pro Driver</p>
              </div>
            </div>
          </div>
        </header>

        {/* Page Content */}
        <main className="flex-1 overflow-y-auto bg-gradient-to-br from-slate-50 to-slate-100 p-8">
          <div className="animate-fade-in">{renderContent()}</div>
        </main>
      </div>
    </div>
  );
}

export default DriverDashboardPage;
