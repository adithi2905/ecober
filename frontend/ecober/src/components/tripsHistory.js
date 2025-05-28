import React from 'react';

const pastRides = [
  {
    id: 1,
    driver: "Kiran Desai",
    rating: 4.6,
    pickup: "Koramangala",
    drop: "Electronic City",
    date: "2025-05-26",
  },
  {
    id: 2,
    driver: "Ravi Shankar",
    rating: 4.9,
    pickup: "HSR Layout",
    drop: "Whitefield",
    date: "2025-05-20",
  },
];

function tripHistory() {
  return (
    <div className="min-h-screen bg-gray-100 flex flex-col items-center py-10">
      <h1 className="text-3xl font-bold mb-6">Your Past Rides</h1>
      <div className="w-full max-w-2xl space-y-4">
        {pastRides.map((ride) => (
          <div key={ride.id} className="bg-white p-4 rounded-xl shadow-md border-l-4 border-maroon-700">
            <p><strong>Date:</strong> {ride.date}</p>
            <p><strong>Driver:</strong> {ride.driver}</p>
            <p><strong>Rating:</strong> ⭐ {ride.rating}</p>
            <p><strong>From:</strong> {ride.pickup}</p>
            <p><strong>To:</strong> {ride.drop}</p>
          </div>
        ))}
      </div>
    </div>
  );
}

export default tripHistory;
