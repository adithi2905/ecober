import React, { useEffect, useState } from "react";

function DriverAvailabletrips() {
  const [rides, setRides] = useState([]);
  const [loading, setLoading] = useState(true);
  const token = localStorage.getItem("token");

  useEffect(() => {
    const fetchRides = async () => {
      try {
        const response = await fetch("http://localhost:8080/driver/fetchRides", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          throw new Error("Failed to fetch rides");
        }

        const data = await response.json();
        setRides(data);
      } catch (error) {
        console.error("Error fetching rides:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchRides();
  }, [token]);

  const handleAcceptRide = async (rideRequestId) => {
    try {
      const response = await fetch(`http://localhost:8080/driver/acceptRide/${rideRequestId}`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        throw new Error("Failed to accept ride");
      }

      alert("Ride accepted successfully!");
      setRides(prev => prev.filter(ride => ride.rideRequestId !== rideRequestId));
    } catch (error) {
      console.error("Error accepting ride:", error);
      alert("Error accepting ride.");
    }
  };

  if (loading) return <div>Loading rides...</div>;

  return (
    <div className="min-h-screen bg-gray-100 p-6">
      <h2 className="text-2xl font-bold mb-4 text-center text-[#800000]">Requested Rides</h2>
      {rides.length === 0 ? (
        <p className="text-center text-gray-600">No ride requests available.</p>
      ) : (
        <div className="space-y-4">
          {rides.map((ride) => (
            <div key={ride.rideRequestId} className="bg-white p-4 rounded-xl shadow-md border">
              <p><strong>Pickup:</strong> {ride.pickupLocation}</p>
              <p><strong>Dropoff:</strong> {ride.dropoffLocation}</p>
              <p><strong>Vehicle Type:</strong> {ride.preferredVehicleType}</p>
              <p><strong>Willing to Pool:</strong> {ride.willingToPool ? "Yes" : "No"}</p>
              <p><strong>Requested Time:</strong> {new Date(ride.requestedTime).toLocaleString()}</p>
              <p><strong>Status:</strong> {ride.status}</p>
              <button
                onClick={() => handleAcceptRide(ride.rideRequestId)}
                className="mt-3 px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700"
              >
                Accept Ride
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default DriverAvailabletrips;
