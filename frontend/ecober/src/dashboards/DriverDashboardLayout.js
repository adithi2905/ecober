import React, { useState } from 'react';
import AvailableRides from '../components/DriverAvailabletrips';
import DriverCurrentTrip from '../components/DriverCurrentTrip';
import DriverProfile from '../components/DriverProfile'; 
import EcoReport from '../components/ecoReport';
import GreenGoals from '../components/greenGoals';
import DriverTripHistory from '../components/DriverTripHistory';

const tabs = [
  { name: 'Available Rides', key: 'available' },
  { name: 'Current Trip', key: 'current' },
  { name: 'Profile', key: 'profile' },
  { name: 'Carbon Impact', key: 'eco' },
  { name: 'Trip Count', key: 'goals' },
  { name: 'Trip History', key: 'history' }
];

function DriverDashboardPage() {
  const [selectedTab, setSelectedTab] = useState('available');

  const renderContent = () => {
    switch (selectedTab) {
      case 'available':
        return <AvailableRides />;
      case 'current':
        return <DriverCurrentTrip />;
      case 'profile':
        return <DriverProfile />; 
      case 'eco':
        return <EcoReport />;
      case 'goals':
        return <GreenGoals />;
      case 'history':
        return <DriverTripHistory />;
      default:
        return <AvailableRides />;
    }
  };

  return (
    <div className="flex h-screen bg-gray-100">
      {/* Sidebar */}
      <aside className="w-64 bg-white shadow-lg flex flex-col justify-between">
        <div>
          <h2 className="text-2xl font-bold p-6 text-maroon-700">Ecober Driver</h2>
          <nav className="space-y-2 px-6">
            {tabs.map((tab) => (
              <button
                key={tab.key}
                onClick={() => setSelectedTab(tab.key)}
                className={`w-full text-left px-4 py-2 rounded-lg transition ${
                  selectedTab === tab.key
                    ? 'bg-maroon-700 text-white'
                    : 'text-gray-700 hover:bg-gray-200'
                }`}
              >
                {tab.name}
              </button>
            ))}
          </nav>
        </div>
        <div className="p-6 text-sm text-gray-500">&copy; 2025 Ecober</div>
      </aside>

      {/* Main content */}
      <div className="flex-1 flex flex-col overflow-y-auto">
        <header className="bg-white shadow px-6 py-4 flex justify-between items-center">
          <h1 className="text-xl font-semibold text-gray-700">
            {tabs.find((t) => t.key === selectedTab)?.name}
          </h1>
          <div className="flex items-center gap-2">
            <span className="text-gray-600">Hello, Driver</span>
            <div className="w-8 h-8 rounded-full bg-gray-300" />
          </div>
        </header>

        <main className="flex-1 overflow-y-auto p-6">
          {renderContent()}
        </main>
      </div>
    </div>
  );
}

export default DriverDashboardPage;
