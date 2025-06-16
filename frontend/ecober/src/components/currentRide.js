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
            "Authorization": `Bearer ${token}`
          },
        });
        
        if (res.ok) {
          const data = await res.json();
          if (data) {
            setRide(data);
          }
        } else if (res.status === 404) {
          // No current ride found - this is normal
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

  if (loading) return <div className="min-h-screen flex items-center justify-center">Loading current ride...</div>;

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <div className="bg-white p-6 rounded-lg shadow-md text-center">
          <h2 className="text-xl font-bold mb-4 text-red-600">Error</h2>
          <p className="text-red-600 mb-4">{error}</p>
          <button
            onClick={handleBookRide}
            className="mt-2 px-4 py-2 bg-[#800000] text-white rounded hover:bg-[#a00000]"
          >
            Book a Ride
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="bg-white p-6 rounded-lg shadow-md text-center">
        {ride ? (
          <>
            <h2 className="text-xl font-bold mb-4 text-[#800000]">Your Ride is In Progress</h2>
            <p><strong>Trip ID:</strong> {ride.tripId}</p>
            <p><strong>Driver:</strong> {ride.driver?.driverName || 'Unknown'}</p>
            <p><strong>Vehicle:</strong> {ride.driver?.vehicleType || 'N/A'}</p>
            <p><strong>Pickup:</strong> {ride.pickupLocation || ride.user?.pickupLocation}</p>
            <p><strong>Dropoff:</strong> {ride.dropoffLocation || ride.user?.dropoffLocation}</p>
            <p><strong>Status:</strong> {ride.status}</p>
            {ride.fare && <p><strong>Estimated Fare:</strong> ${ride.fare.toFixed(2)}</p>}
          </>
        ) : (
          <>
            <h2 className="text-xl font-bold mb-4">No Ongoing Rides</h2>
            <p className="text-gray-600 mb-4">You don't have any active rides at the moment.</p>
            <button
              onClick={handleBookRide}
              className="mt-2 px-4 py-2 bg-[#800000] text-white rounded hover:bg-[#a00000]"
            >
              Book a Ride
            </button>
          </>
        )}
      </div>
    </div>
  );
}

export default CurrentRide;