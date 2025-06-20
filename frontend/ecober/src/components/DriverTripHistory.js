import React, { useEffect, useState } from 'react';

function DriverTripHistory() {
  const [trips, setTrips] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const formatDate = (dateStr) => {
    if (!dateStr) return 'N/A';
    const date = new Date(dateStr);
    return date.toLocaleString(); 
  };

  useEffect(() => {
    const fetchDriverTrips = async () => {
      try {
        const token = localStorage.getItem('token');
        const response = await fetch('http://localhost:8080/driver/me/past-trips', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (response.ok) {
          const data = await response.json();
          setTrips(data);
        } else {
          const errorText = await response.text();
          setError('Failed to fetch driver trip history: ' + errorText);
        }
      } catch (err) {
        console.error('Error:', err);
        setError('Network error occurred');
      } finally {
        setLoading(false);
      }
    };

    fetchDriverTrips();
  }, []);

  if (loading) {
    return <div className="text-center py-10">Loading your past trips...</div>;
  }

  if (error) {
    return <div className="text-center py-10 text-red-600">{error}</div>;
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
            <div
              key={trip.tripId}
              className="bg-white p-4 rounded-xl shadow-md border-l-4 border-[#800000]"
            >
              <p><strong>Trip ID:</strong> {trip.tripId}</p>
              <p><strong>Date:</strong> {formatDate(trip.endTime || trip.startTime)}</p>
              <p><strong>Passenger:</strong> {trip.user?.username || 'Unknown'}</p>
              <p><strong>From:</strong> {trip.route?.source?.address || 'Unknown'}</p>
              <p><strong>To:</strong> {trip.route?.destination?.address || 'Unknown'}</p>
              <p><strong>Status:</strong> {trip.status}</p>
              <p><strong>Estimated CO₂ Emission:</strong> {trip.estimatedEmission?.toFixed(2) || 0} kg</p>
              <p><strong>Actual CO₂ Emission:</strong> {trip.actualEmission?.toFixed(2) || 0} kg</p>
              {trip.ecoScore && <p><strong>Eco Score:</strong> {trip.ecoScore}</p>}
              {trip.feedback && <p><strong>Feedback:</strong> {trip.feedback}</p>}
            </div>
          ))
        )}
      </div>
    </div>
  );
}

export default DriverTripHistory;
