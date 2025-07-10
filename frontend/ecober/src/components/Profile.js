import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

function Profile() {
  const [rider, setRider] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const response = await fetch("http://localhost:8080/user/profile", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          throw new Error("Failed to fetch profile");
        }

        const data = await response.json();
        setRider(data);
      } catch (error) {
        console.error("Error fetching profile:", error);
        alert("🚫 Unable to load profile. Please login again.");
        navigate("/login");
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, [navigate, token]);

  const handleLogout = (e) => {
    e.preventDefault();
    localStorage.removeItem("token"); // Clear token
    navigate("/login");
  };

  if (loading) {
    return (
      <div className="p-8 flex justify-center items-center min-h-screen text-slate-500 animate-pulse">
        Loading profile...
      </div>
    );
  }

  if (!rider) {
    return (
      <div className="p-8 flex justify-center items-center min-h-screen text-red-600">
        Failed to load profile.
      </div>
    );
  }

  return (
    <div className="p-8 min-h-screen bg-gradient-to-br from-slate-50 to-slate-100 flex justify-center items-center">
      <div className="bg-white rounded-2xl shadow-xl p-8 w-full max-w-lg">
        <h2 className="text-3xl font-bold text-emerald-600 text-center mb-6">
          {rider.ecoBadge}
        </h2>

        <div className="space-y-3 text-slate-700">
          <p><strong>Name:</strong> {rider.username}</p>
          <p><strong>Total Rides:</strong> {rider.tripCount}</p>
          <p><strong>Total CO₂ Saved:</strong> {rider.totalCO2Saved.toFixed(2)} kg</p>
          <p><strong>Average per Ride:</strong> {rider.averageCO2Saved.toFixed(2)} kg</p>
         </div>

        <button
          onClick={handleLogout}
          className="mt-6 w-full py-3 bg-emerald-500 hover:bg-emerald-600 text-white rounded-lg font-semibold transition"
        >
          Logout
        </button>
      </div>
    </div>
  );
}

export default Profile;
