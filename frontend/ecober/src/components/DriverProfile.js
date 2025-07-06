import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const DriverProfile = () => {
  const [driver, setDriver] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate=useNavigate();

  const handleLogout=async()=>

  {
    const token = localStorage.getItem("token");

      await fetch("http://localhost:8080/driver/logout", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        }
      });

      localStorage.removeItem("token");
      navigate("/");
      return <div>Logging out...</div>;
  }

  useEffect(() => {
    const fetchDriverProfile = async () => {
      try {
        const token = localStorage.getItem("token");
        const response = await fetch("http://localhost:8080/driver/me/getProfile", {
          headers: { Authorization: `Bearer ${token}` },
        });

        if (response.ok) {
          const data = await response.json();
          setDriver(data);
        } else {
          const errorText = await response.text();
          setError("Failed to load profile: " + errorText);
        }
      } catch (error) {
        console.error("Error fetching driver profile:", error);
        setError("Network error occurred");
      } finally {
        setLoading(false);
      }
    };

    fetchDriverProfile();
  }, []);

  if (loading)
    return (
      <div className="flex justify-center items-center h-full text-slate-500 animate-pulse">
        Loading profile...
      </div>
    );
  if (error)
    return <div className="p-6 text-red-600 text-center">{error}</div>;
  if (!driver)
    return (
      <div className="p-6 text-center text-slate-500">No profile data found.</div>
    );


  return (
    <div className="max-w-4xl mx-auto bg-white rounded-2xl shadow p-6 mt-6">
      <h2 className="text-3xl font-bold text-slate-800 mb-6 text-center">
        Driver Profile
      </h2>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="space-y-2">
          <p className="text-slate-700">
            <strong>Name:</strong> {driver.driverName || "N/A"}
          </p>
          <p className="text-slate-700">
            <strong>Vehicle No:</strong> {driver.vehicleNo || "N/A"}
          </p>
          <p className="text-slate-700">
            <strong>Vehicle Type:</strong> {driver.vehicleType || "N/A"}
          </p>
          <p className="text-slate-700">
            <strong>Fuel Efficiency:</strong> {driver.fuelEfficiency || "N/A"}
          </p>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div className="bg-gradient-to-br from-blue-50 to-blue-100 p-4 rounded-xl shadow">
            <h4 className="text-sm text-slate-600">Total Trips</h4>
            <p className="text-2xl font-bold text-blue-700">
              {driver.totalTrips || 0}
            </p>
          </div>
          <div className="bg-gradient-to-br from-yellow-50 to-yellow-100 p-4 rounded-xl shadow">
            <h4 className="text-sm text-slate-600">Average Rating</h4>
            <p className="text-2xl font-bold text-yellow-500">
              {driver.rating || 0} ★
            </p>
          </div>
          <div className="bg-gradient-to-br from-green-50 to-green-100 p-4 rounded-xl shadow">
            <h4 className="text-sm text-slate-600">Acceptance Rate</h4>
            <p className="text-2xl font-bold text-green-700">
              {driver.acceptanceRate || 0}%
            </p>
          </div>
          <div className="bg-gradient-to-br from-emerald-50 to-emerald-100 p-4 rounded-xl shadow">
            <h4 className="text-sm text-slate-600">CO₂ Saved</h4>
            <p className="text-2xl font-bold text-emerald-700">
              {driver.co2Saved || 0} kg
            </p>
          </div>
        </div>
      </div>
      <button
  onClick={handleLogout}
  className="mt-8 w-full md:w-40 mx-auto block py-3 px-6 rounded-xl bg-gradient-to-r from-red-500 to-red-600 text-white text-lg font-medium shadow hover:from-red-600 hover:to-red-700 transition-all duration-300 ease-in-out"
>
Logout</button>
    </div>
  );
};

export default DriverProfile;
