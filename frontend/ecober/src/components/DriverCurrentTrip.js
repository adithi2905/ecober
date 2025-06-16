import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

function DriverCurrentTrip() {
  const [trip, setTrip] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchTrip = async () => {
      const token = localStorage.getItem("token");
      const res = await fetch("http://localhost:8080/driver/me/current-trip", {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (res.ok) {
        const data = await res.json();
        setTrip(data);
      }
      setLoading(false);
    };
    fetchTrip();
  }, []);

  const handleStartTrip = async () => {
    const token = localStorage.getItem("token");
    const res = await fetch(`http://localhost:8080/driver/start-trip/${trip.tripId}`, {
      method: "POST",
      headers: { Authorization: `Bearer ${token}` },
    });
    if (res.ok) {
      alert("Trip started!");
      window.location.reload();
    } else {
      alert("Failed to start trip.");
    }
  };

  const handleEndTrip = async () => {
    const token = localStorage.getItem("token");
    const res = await fetch(`http://localhost:8080/driver/end-trip/${trip.tripId}`, {
      method: "POST",
      headers: { Authorization: `Bearer ${token}` },
    });
    if (res.ok) {
      alert("Trip ended.");
      setTrip(null);
    } else {
      alert("Failed to end trip.");
    }
  };

  if (loading) return <div>Loading...</div>;

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="bg-white p-6 rounded shadow-md text-center">
        {trip ? (
          <>
            <h2 className="text-xl font-bold text-[#800000] mb-4">Trip Status: {trip.status}</h2>
            <p><strong>Rider:</strong> {trip.riderName}</p>
            <p><strong>From:</strong> {trip.pickupLocation}</p>
            <p><strong>To:</strong> {trip.dropoffLocation}</p>
            {trip.status === "ACCEPTED" && (
              <button onClick={handleStartTrip} className="mt-4 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700">
                Start Trip
              </button>
            )}
            {trip.status === "IN_PROGRESS" && (
              <button onClick={handleEndTrip} className="mt-4 px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700">
                End Trip
              </button>
            )}
          </>
        ) : (
          <div>
            <h2 className="text-xl font-bold">No Ongoing Trip</h2>
            <button onClick={() => navigate("/availableRides")} className="mt-4 px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700">
              View Available Rides
            </button>
          </div>
        )}
      </div>
    </div>
  );
}

export default DriverCurrentTrip;
