import React from 'react';
import { Link, Outlet, useLocation } from 'react-router-dom';

const navLinks = [
{ name: 'Book Ride', path: '/rideBooking' },
{ name: 'Past Rides', path: '/tripsHistory' },
{ name: 'Profile', path: '/profile' },
   { name: 'Eco History', path: '/ecoReport' },
    { name: 'Green Goals', path: '/goals' },
];

function DashboardLayout() {
  const location = useLocation();

  return (
    <div className="flex h-screen bg-gray-100">
      {/* Sidebar */}
      <aside className="w-64 bg-white shadow-lg flex flex-col justify-between">
        <div>
          <h2 className="text-2xl font-bold p-6 text-maroon-700">Ecober</h2>
          <nav className="space-y-2 px-6">
            {navLinks.map((link) => (
              <Link
                key={link.path}
                to={link.path}
                className={`block px-4 py-2 rounded-lg transition ${
                  location.pathname === link.path
                    ? 'bg-maroon-700 text-white'
                    : 'text-gray-700 hover:bg-gray-200'
                }`}
              >
                {link.name}
              </Link>
            ))}
          </nav>
        </div>
        <div className="p-6 text-sm text-gray-500">&copy; 2025 Ecober</div>
      </aside>

      {/* Main content */}
      <div className="flex-1 flex flex-col">
        {/* Topbar */}
        <header className="bg-white shadow px-6 py-4 flex justify-between items-center">
          <h1 className="text-xl font-semibold text-gray-700">Dashboard</h1>
          <div className="flex items-center gap-2">
            <span className="text-gray-600">Hello, Adithi</span>
            <div className="w-8 h-8 rounded-full bg-gray-300" />
          </div>
        </header>

        {/* Page content */}
        <main className="flex-1 overflow-y-auto p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

export default DashboardLayout;
