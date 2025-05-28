import React from 'react';

function Profile() {
  const rider = {
    name: "Adithi R",
    email: "adithi@ecober.io",
    totalRides: 12,
    totalCO2Saved: 24.7, // in kg
  };

  const averageCO2 = (rider.totalCO2Saved / rider.totalRides).toFixed(2);
  const ecoFriendly = averageCO2 >= 2.0;

  return (
    <div className="max-w-xl mx-auto bg-white shadow-lg rounded-xl p-6 space-y-4">
      <h2 className="text-2xl font-bold text-maroon-700 text-center">Rider Profile</h2>
      <div className="space-y-2 text-gray-700">
        <p><strong>Name:</strong> {rider.name}</p>
        <p><strong>Email:</strong> {rider.email}</p>
        <p><strong>Total Rides:</strong> {rider.totalRides}</p>
        <p><strong>Total CO₂ Saved:</strong> {rider.totalCO2Saved} kg</p>
        <p><strong>Average per Ride:</strong> {averageCO2} kg</p>
        <p>
          <strong>Status:</strong>{" "}
          {ecoFriendly ? (
            <span className="text-green-600 font-semibold">✅ Eco-Friendly Rider 🌱</span>
          ) : (
            <span className="text-yellow-600 font-semibold">⚠️ Needs Improvement</span>
          )}
        </p>
      </div>
    </div>
  );
}

export default Profile;
