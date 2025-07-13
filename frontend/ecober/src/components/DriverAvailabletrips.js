import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

function DriverAvailableTrips() {
  const [rides, setRides] = useState([]);
  const [loading, setLoading] = useState(true);
  const [accepting, setAccepting] = useState(false);
  const token = localStorage.getItem("token");
  const navigate = useNavigate();

  useEffect(() => {
    const fetchRides = async () => {
      try {
        const response = await fetch("http://localhost:8080/driver/fetchRides", {
          headers: { Authorization: `Bearer ${token}` },
        });

        if (!response.ok) throw new Error("Failed to fetch rides");

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
      const response = await fetch(
        `http://localhost:8080/driver/acceptRide/${rideRequestId}`,
        {
          method: "POST",
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      if (!response.ok) {
        const errorData = await response.json();
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
      navigate("/availableRides");
    } catch (error) {
      console.error("Error accepting ride:", error);
      alert("🚫 Error accepting ride.");
    } finally {
      setAccepting(false);
    }
  };

  if (loading)
    return (
      <div className="flex justify-center items-center h-full text-slate-500 animate-pulse">
        Loading rides...
      </div>
    );

  return (
    <div className="p-6 space-y-4">
      <h2 className="text-3xl font-bold text-slate-800 mb-4 text-center">
        Available Rides
      </h2>
      {rides.length === 0 ? (
        <p className="text-center text-slate-500">
          No ride requests available at the moment.
        </p>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {rides.map((ride) => (
            <div
              key={ride.rideRequestId}
              className="p-5 bg-white rounded-2xl shadow hover:shadow-md transition transform hover:scale-105 border border-slate-100"
            >
              <h3 className="text-xl font-semibold text-slate-700 mb-2">
                Ride ID: {ride.rideRequestId}
              </h3>
              <p className="text-slate-600">
                <strong>Pickup:</strong> {ride.pickupLocation}
              </p>
              <p className="text-slate-600">
                <strong>Dropoff:</strong> {ride.dropoffLocation}
              </p>
              <p className="text-slate-600">
                <strong>Vehicle Type:</strong> {ride.preferredVehicleType}
              </p>
              <p className="text-slate-600">
                <strong>Willing to Pool:</strong>{" "}
                {ride.willingToPool ? "Yes" : "No"}
              </p>

              <button
                onClick={() => handleAcceptRide(ride.rideRequestId)}
                className="mt-4 w-full py-2 rounded-xl text-white bg-gradient-to-r from-emerald-500 to-emerald-600 hover:from-emerald-600 hover:to-emerald-700 shadow"
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

export default DriverAvailableTrips;
