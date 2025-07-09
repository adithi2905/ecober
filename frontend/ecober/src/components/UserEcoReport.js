import React, { useEffect, useState } from "react";

function UserEcoReport() {
  const [ecoReport, setEcoReport] = useState(null);
  const [loading, setLoading] = useState(true);
  const token = localStorage.getItem("token");

  useEffect(() => {
    const fetchEcoReport = async () => {
      try {
        const response = await fetch("http://localhost:8080/user/eco/ecoReport", {
          headers: { Authorization: `Bearer ${token}` },
        });

        if (!response.ok) {
          throw new Error("Failed to fetch Eco Report.");
        }

        const data = await response.json();
        setEcoReport(data);
      } catch (error) {
        console.error("Error fetching Eco Report:", error);
        alert("❌ Could not load Eco Report.");
      } finally {
        setLoading(false);
      }
    };

    fetchEcoReport();
  }, [token]);

  if (loading) {
    return (
      <div className="flex justify-center items-center h-full text-slate-500 animate-pulse">
        Loading Eco Report...
      </div>
    );
  }

  if (!ecoReport) {
    return (
      <div className="text-center text-red-500">
        ❌ Failed to load Eco Report.
      </div>
    );
  }

  return (
    <div className="p-6 space-y-6">
      <h2 className="text-3xl font-bold text-slate-800 text-center">
        🌱 Your Eco Report
      </h2>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div className="bg-white shadow rounded-2xl p-5 text-center">
          <p className="text-2xl font-bold text-emerald-600">{ecoReport.totalTrips}</p>
          <p className="text-slate-600">Total Trips</p>
        </div>
        <div className="bg-white shadow rounded-2xl p-5 text-center">
          <p className="text-2xl font-bold text-emerald-600">{ecoReport.totalEmissions.toFixed(2)} kg</p>
          <p className="text-slate-600">Total CO₂ Emissions</p>
        </div>
        <div className="bg-white shadow rounded-2xl p-5 text-center">
          <p className="text-2xl font-bold text-emerald-600">{ecoReport.averageEmissionPerTrip.toFixed(2)} kg</p>
          <p className="text-slate-600">Avg CO₂ per Trip</p>
        </div>
      </div>

      <div className="bg-gradient-to-r from-emerald-400 to-emerald-600 text-white rounded-2xl p-6 shadow flex flex-col items-center">
        <p className="text-xl">Your Badge:</p>
        <p className="text-3xl font-bold mt-2">{ecoReport.ecoBadge}</p>
      </div>
    </div>
  );
}

export default UserEcoReport;
