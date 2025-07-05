import React, { useEffect, useState } from 'react';

function TripHistory() {
  const [trips, setTrips] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchTrips = async () => {
      const token = localStorage.getItem("token");
      try {
        const res = await fetch("http://localhost:8080/user/tripsHistory", {
          headers: {
            Authorization: `Bearer ${token}`
          },
        });

        if (res.ok) {
          const data = await res.json();
          setTrips(data || []);
        } else {
          console.error("Failed to fetch trips");
        }
      } catch (error) {
        console.error("Error fetching trips:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchTrips();
  }, []);

  if (loading) {
    return (
      <div className="p-8 text-center text-slate-600">
        Loading trip history...
      </div>
    );
  }

  return (
    <div className="p-8">
      <h1 className="text-3xl font-bold mb-6 text-emerald-600">Your Past Rides</h1>
      {trips.length === 0 ? (
        <div className="bg-white p-6 rounded-lg shadow text-center">
          No past rides found.
        </div>
      ) : (
        <div className="space-y-4">
          {trips.map((ride) => (
            <div key={ride.tripId} className="bg-white p-4 rounded-xl shadow-md border-l-4 border-emerald-500">
              <p><strong>Date:</strong> {ride.endTime?.split("T")[0] || "N/A"}</p>
              <p><strong>Driver:</strong> {ride.driver?.driverName || "Unknown"}</p>
              <p><strong>Vehicle:</strong> {ride.driver?.vehicleType || "N/A"}</p>
              <p><strong>From:</strong> {ride.pickupLocation}</p>
              <p><strong>To:</strong> {ride.dropoffLocation}</p>
              <p><strong>Status:</strong> {ride.status}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default TripHistory;
