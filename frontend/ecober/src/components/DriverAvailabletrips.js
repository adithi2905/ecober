import React, { useEffect, useState } from "react";
 import { useNavigate } from "react-router-dom"; 

function DriverAvailabletrips() {
  const [rides, setRides] = useState([]);
  const [loading, setLoading] = useState(true);
  const [accepting, setAccepting] = useState(false);
  const token = localStorage.getItem("token");
   const navigate = useNavigate(); 

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
    if (accepting) return;
    setAccepting(true);

    try {
      const response = await fetch(`http://localhost:8080/driver/acceptRide/${rideRequestId}`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      const contentType = response.headers.get("content-type");
      let errorData = {};

      if (!response.ok) {
        if (contentType && contentType.includes("application/json")) {
          errorData = await response.json();
        }

        if (errorData.message?.includes("Driver already has an active trip")) {
          alert("❌ You already have an active trip. Complete it first.");
        } else if (errorData.message?.includes("Ride already accepted")) {
          alert("⚠️ This ride has already been taken by another driver.");
        } else {
          alert("🚫 Failed to accept ride. Try again.");
        }
        return;
      }

      alert("✅ Ride accepted successfully!");
      setRides([]);
      navigate("/driver/currentTrip"); 
    } catch (error) {
      console.error("Error accepting ride:", error);
      alert("🚫 Error accepting ride.");
    } finally {
      setAccepting(false);
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
              <p><strong>Ride ID:</strong> {ride.rideRequestId}</p>

              <button
                onClick={() => handleAcceptRide(ride.rideRequestId)}
                className="mt-3 px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700 disabled:opacity-50"
                disabled={accepting}
              >
                {accepting ? "Processing..." : "Accept Ride"}
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default DriverAvailabletrips;
