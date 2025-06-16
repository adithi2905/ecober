import React, { useEffect, useState } from 'react';

function DriverTripHistory() {
  const [trips, setTrips] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDriverTrips = async () => {
      try {
        const token = localStorage.getItem('token');

        const response = await fetch('http://localhost:8080/driver/driverTripsHistory', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (response.ok) {
          const data = await response.json();
          setTrips(data);
        } else {
          console.error('Failed to fetch driver trip history');
        }
      } catch (err) {
        console.error('Error:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchDriverTrips();
  }, []);

  if (loading) {
    return <div className="text-center py-10">Loading your past trips...</div>;
  }

  return (
    <div className="min-h-screen bg-gray-100 flex flex-col items-center py-10">
      <h1 className="text-3xl font-bold mb-6 text-[#800000]">Driver Trip History</h1>
      <div className="w-full max-w-2xl space-y-4">
        {trips.length === 0 ? (
          <div className="bg-white p-6 rounded-lg shadow text-center">
            No trips found.
          </div>
        ) : (
          trips.map((trip) => (
            <div key={trip.tripId} className="bg-white p-4 rounded-xl shadow-md border-l-4 border-[#800000]">
              <p><strong>Date:</strong> {trip.endTime?.split('T')[0] || 'N/A'}</p>
              <p><strong>Passenger:</strong> {trip.user?.riderName || 'Unknown'}</p>
              <p><strong>From:</strong> {trip.pickupLocation}</p>
              <p><strong>To:</strong> {trip.dropoffLocation}</p>
              <p><strong>Status:</strong> {trip.status}</p>
              <p><strong>CO₂ Saved:</strong> {trip.co2Saved?.toFixed(2) || 0} kg</p>
            </div>
          ))
        )}
      </div>
    </div>
  );
}

export default DriverTripHistory;
