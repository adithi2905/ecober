import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

function CurrentRide() {
  const [ride, setRide] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchCurrentRide = async () => {
      const token = localStorage.getItem("token");
      try {
        const res = await fetch("http://localhost:8080/user/trip/current", {
          headers: {
            Authorization: `Bearer ${token}`
          },
        });

        if (res.ok) {
          const data = await res.json();
          setRide(data || null);
        } else if (res.status === 404) {
          setRide(null);
        } else {
          const errorText = await res.text();
          setError('Failed to fetch current ride: ' + errorText);
        }
      } catch (err) {
        console.error("Error fetching current ride", err);
        setError('Network error occurred');
      }
      setLoading(false);
    };

    fetchCurrentRide();
  }, []);

  const handleBookRide = () => {
    navigate("/rideBooking");
  };

  if (loading) {
    return (
      <div className="p-8 text-center text-slate-600">
        Loading current ride...
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-8 bg-red-100 rounded-xl shadow-md text-center">
        <h2 className="text-xl font-bold text-red-700 mb-3">Error</h2>
        <p className="text-red-600 mb-4">{error}</p>
        <button
          onClick={handleBookRide}
          className="px-4 py-2 bg-emerald-500 hover:bg-emerald-600 text-white rounded-lg"
        >
          Book a Ride
        </button>
      </div>
    );
  }

  return (
    <div className="p-8">
      {ride ? (
        <div className="bg-white p-6 rounded-xl shadow-lg border-l-4 border-emerald-500">
          <h2 className="text-2xl font-bold mb-3 text-emerald-600">Your Ride in Progress</h2>
          <p><strong>Trip ID:</strong> {ride.tripId}</p>
          <p><strong>Driver:</strong> {ride.driver?.driverName || 'Unknown'}</p>
          <p><strong>Vehicle:</strong> {ride.driver?.vehicleType || 'N/A'}</p>
          <p><strong>Pickup:</strong> {ride.pickupLocation}</p>
          <p><strong>Dropoff:</strong> {ride.dropoffLocation}</p>
          <p><strong>Status:</strong> {ride.status}</p>
          {ride.fare && <p><strong>Estimated Fare:</strong> ${ride.fare.toFixed(2)}</p>}
        </div>
      ) : (
        <div className="bg-white p-6 rounded-xl shadow text-center">
          <h2 className="text-xl font-bold text-slate-700">No Ongoing Rides</h2>
          <p className="text-slate-500 mb-4">You don’t have any active rides at the moment.</p>
          <button
            onClick={handleBookRide}
            className="px-4 py-2 bg-emerald-500 hover:bg-emerald-600 text-white rounded-lg"
          >
            Book a Ride
          </button>
        </div>
      )}
    </div>
  );
}

export default CurrentRide;
