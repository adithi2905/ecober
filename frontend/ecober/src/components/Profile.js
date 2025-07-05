import React from 'react';
import { useNavigate } from 'react-router-dom';

function Profile() {
  const rider = {
    name: "Adithi R",
    email: "adithi@ecober.io",
    totalRides: 12,
    totalCO2Saved: 24.7, // in kg
  };

  const navigate = useNavigate();
  const averageCO2 = (rider.totalCO2Saved / rider.totalRides).toFixed(2);
  const ecoFriendly = averageCO2 >= 2.0;

  const handleLogout = (e) => {
    e.preventDefault();
    navigate("/logout");
  };

  return (
    <div className="p-8 min-h-screen bg-gradient-to-br from-slate-50 to-slate-100 flex justify-center items-center">
      <div className="bg-white rounded-2xl shadow-xl p-8 w-full max-w-lg">
        <h2 className="text-3xl font-bold text-emerald-600 text-center mb-6">Rider Profile</h2>
        <div className="space-y-3 text-slate-700">
          <p><strong>Name:</strong> {rider.name}</p>
          <p><strong>Email:</strong> {rider.email}</p>
          <p><strong>Total Rides:</strong> {rider.totalRides}</p>
          <p><strong>Total CO₂ Saved:</strong> {rider.totalCO2Saved} kg</p>
          <p><strong>Average per Ride:</strong> {averageCO2} kg</p>
          <p>
            <strong>Status:</strong>{" "}
            {ecoFriendly ? (
              <span className="text-emerald-600 font-semibold">✅ Eco-Friendly Rider 🌱</span>
            ) : (
              <span className="text-yellow-600 font-semibold">⚠️ Needs Improvement</span>
            )}
          </p>
        </div>
        <button
          onClick={handleLogout}
          className="mt-6 w-full py-3 bg-emerald-500 hover:bg-emerald-600 text-white rounded-lg font-semibold transition"
        >
          Logout
        </button>
      </div>
    </div>
  );
}

export default Profile;
