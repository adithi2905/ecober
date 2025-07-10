import React, { useEffect, useState } from "react";

function DriverTripHistory() {
  const [trips, setTrips] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const formatDate = (dateStr) => {
    if (!dateStr) return "N/A";
    const date = new Date(dateStr);
    return date.toLocaleString();
  };

  useEffect(() => {
    const fetchDriverTrips = async () => {
      try {
        const token = localStorage.getItem("token");
        const response = await fetch(
          "http://localhost:8080/driver/me/past-trips",
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );

        if (response.ok) {
          const data = await response.json();
          setTrips(data);
        } else {
          const errorText = await response.text();
          setError("Failed to fetch trip history: " + errorText);
        }
      } catch (err) {
        console.error("Error:", err);
        setError("Network error occurred");
      } finally {
        setLoading(false);
      }
    };

    fetchDriverTrips();
  }, []);

  if (loading) {
    return (
      <div className="flex justify-center items-center h-full text-slate-500 animate-pulse">
        Loading your past trips...
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-center py-10 text-red-600 font-medium">
        {error}
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 to-slate-100 py-8 px-4">
      <h1 className="text-3xl font-bold text-slate-800 mb-6 text-center">
        🏁 Driver Trip History
      </h1>

      <div className="max-w-3xl mx-auto space-y-4">
        {trips.length === 0 ? (
          <div className="bg-white p-6 rounded-2xl shadow text-center">
            <p className="text-slate-500">No trips found in your history.</p>
          </div>
        ) : (
          trips.map((trip) => (
            <div
              key={trip.tripId}
              className="bg-white p-5 rounded-2xl shadow-md border-l-4 border-emerald-500 hover:shadow-lg transition transform hover:scale-[1.02]"
            >
              <div className="flex justify-between items-center mb-2">
                <h3 className="text-lg font-semibold text-slate-800">
                  Trip ID:{" "}
                  <span className="text-slate-600">{trip.tripId}</span>
                </h3>
                <span
                  className={`text-xs px-2 py-1 rounded-full ${
                    trip.status === "COMPLETED"
                      ? "bg-emerald-100 text-emerald-700"
                      : "bg-blue-100 text-blue-700"
                  }`}
                >
                  {trip.status}
                </span>
              </div>
              <p className="text-slate-700">
                <strong>Date:</strong> {formatDate(trip.endTime || trip.startTime)}
              </p>
              <p className="text-slate-700">
                <strong>Passenger:</strong> {trip.user?.username || "Unknown"}
              </p>
              <p className="text-slate-700">
                <strong>From:</strong>{" "}
                {trip.route?.source?.address || "Unknown"}
              </p>
              <p className="text-slate-700">
                <strong>To:</strong>{" "}
                {trip.route?.destination?.address || "Unknown"}
              </p>
              {trip.ecoScore && (
                <p className="text-slate-700">
                  <strong>Eco Score:</strong>{" "}
                  <span className="font-medium text-green-700">
                    {trip.ecoScore}
                  </span>
                </p>
              )}
              {trip.route?.carbonCost && (
                <p className="text-slate-700">
                  <strong>Carbon Cost:</strong>{" "}
                  <span className="font-medium text-amber-700">
                    {trip.route.carbonCost} kg CO₂
                  </span>
                </p>
              )}
              {trip.feedback && (
                <p className="text-slate-700">
                  <strong>Feedback:</strong> "{trip.feedback}"
                </p>
              )}
            </div>
          ))
        )}
      </div>
    </div>
  );
}

export default DriverTripHistory;
