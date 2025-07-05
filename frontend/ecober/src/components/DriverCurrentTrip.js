import React, { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";

function DriverCurrentTrip() {
  const [trip, setTrip] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    const passedTrip = location.state?.trip;
    if (passedTrip) {
      setTrip(passedTrip);
      setLoading(false);
    } else {
      const fetchTrip = async () => {
        const token = localStorage.getItem("token");
        const res = await fetch("http://localhost:8080/driver/me/current-trip", {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (res.ok) {
          const data = await res.json();
          setTrip(data);
        } else {
          const msg = await res.text();
          setError("Could not load trip: " + msg);
        }
        setLoading(false);
      };
      fetchTrip();
    }
  }, [location.state]);

  const handleStartTrip = async () => {
    const token = localStorage.getItem("token");
    const res = await fetch(`http://localhost:8080/driver/start-trip/${trip.tripId}`, {
      method: "POST",
      headers: { Authorization: `Bearer ${token}` },
    });

    if (res.ok) {
      alert("🚗 Trip started!");
      const updatedTrip = { ...trip, status: "IN_PROGRESS" };
      setTrip(updatedTrip);
    } else {
      alert("❌ Failed to start trip.");
    }
  };

  const handleEndTrip = async () => {
    const token = localStorage.getItem("token");
    const res = await fetch(`http://localhost:8080/driver/end-trip/${trip.tripId}`, {
      method: "POST",
      headers: { Authorization: `Bearer ${token}` },
    });

    if (res.ok) {
      alert("✅ Trip ended successfully.");
      setTrip(null);
      navigate("/driver/availableRides");
    } else {
      alert("❌ Failed to end trip.");
    }
  };

  if (loading)
    return (
      <div className="flex justify-center items-center h-full text-slate-500 animate-pulse">
        Loading current trip...
      </div>
    );

  if (error)
    return (
      <div className="flex justify-center items-center h-full text-red-600">
        {error}
      </div>
    );

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-slate-50 to-slate-100">
      <div className="bg-white p-8 rounded-2xl shadow-lg w-full max-w-lg transition transform hover:scale-[1.02]">
        {trip ? (
          <>
            <h2 className="text-2xl font-bold text-slate-800 mb-4">
              🚖 Trip Status:{" "}
              <span
                className={`${
                  trip.status === "ACCEPTED"
                    ? "text-blue-600"
                    : "text-emerald-600"
                }`}
              >
                {trip.status}
              </span>
            </h2>
            <div className="space-y-2 text-slate-700">
              <p>
                <strong>Rider:</strong> {trip.user?.username || "N/A"}
              </p>
              <p>
                <strong>From:</strong> {trip.route?.source.address || "N/A"}
              </p>
              <p>
                <strong>To:</strong> {trip.route?.destination.address || "N/A"}
              </p>
            </div>

            {trip.status === "ACCEPTED" && (
              <button
                onClick={handleStartTrip}
                className="mt-6 w-full py-3 rounded-xl text-white bg-gradient-to-r from-blue-500 to-blue-600 hover:from-blue-600 hover:to-blue-700 shadow-lg transition"
              >
                Start Trip
              </button>
            )}

            {trip.status === "IN_PROGRESS" && (
              <button
                onClick={handleEndTrip}
                className="mt-6 w-full py-3 rounded-xl text-white bg-gradient-to-r from-red-500 to-red-600 hover:from-red-600 hover:to-red-700 shadow-lg transition"
              >
                End Trip
              </button>
            )}
          </>
        ) : (
          <div className="text-center">
            <h2 className="text-xl font-bold text-slate-700 mb-4">
              No Ongoing Trip
            </h2>
            <button
              onClick={() => navigate("/driver/availableRides")}
              className="px-4 py-2 rounded-xl text-white bg-gradient-to-r from-emerald-500 to-emerald-600 hover:from-emerald-600 hover:to-emerald-700 shadow transition"
            >
              View Available Rides
            </button>
          </div>
        )}
      </div>
    </div>
  );
}

export default DriverCurrentTrip;
