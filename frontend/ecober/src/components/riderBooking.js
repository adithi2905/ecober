import React, { useState } from 'react';
import { useNavigate } from "react-router-dom";

function RiderBooking() {
  const [pickup, setPickup] = useState('');
  const [destination, setDestination] = useState('');
  const [preferredVehicleType, setPreferredVehicleType] = useState('Sedan');
  const [willingToPool, setWillingToPool] = useState(false);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    const token = localStorage.getItem("token");

    const trimmedPickup = pickup.trim();
    const trimmedDestination = destination.trim();

    if (!trimmedPickup || !trimmedDestination) {
      alert("Please enter valid pickup and destination locations.");
      return;
    }

    setLoading(true);

    try {
      const response = await fetch(`${process.env.REACT_APP_API_BASE || "http://localhost:8080"}/ride/requestRide`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({
          pickupLocation: trimmedPickup,
          dropoffLocation: trimmedDestination,
          preferredVehicleType,
          willingToPool,
        })
      });

      if (response.ok) {
        const data = await response.json();
        navigate("/bookingconfirmation", {
          state: {
            driver: null,
            pickup: trimmedPickup,
            destination: trimmedDestination,
            status: data.message || "Ride booked successfully.",
          },
        });
      } else {
        const errorText = await response.text();
        alert(errorText || "Failed to book ride");
      }
    } catch (error) {
      console.error("Error booking ride:", error);
      alert("Something went wrong. " + error.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-8 bg-gradient-to-br from-slate-50 to-slate-100 min-h-screen flex items-center justify-center">
      <div className="bg-white p-8 rounded-2xl shadow-xl w-full max-w-lg">
        <h2 className="text-3xl font-bold mb-6 text-slate-800 text-center">Book Your Ride</h2>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block mb-1 text-slate-700">Pickup Location</label>
            <input
              type="text"
              value={pickup}
              onChange={(e) => setPickup(e.target.value)}
              className="w-full border border-slate-300 p-3 rounded-lg focus:outline-none focus:ring focus:ring-emerald-500"
              placeholder="Enter pickup location"
              required
            />
          </div>
          <div>
            <label className="block mb-1 text-slate-700">Destination Location</label>
            <input
              type="text"
              value={destination}
              onChange={(e) => setDestination(e.target.value)}
              className="w-full border border-slate-300 p-3 rounded-lg focus:outline-none focus:ring focus:ring-emerald-500"
              placeholder="Enter destination"
              required
            />
          </div>
          <div>
            <label className="block mb-1 text-slate-700">Preferred Vehicle Type</label>
            <select
              value={preferredVehicleType}
              onChange={(e) => setPreferredVehicleType(e.target.value)}
              className="w-full border border-slate-300 p-3 rounded-lg focus:outline-none"
            >
              <option value="Sedan">Sedan</option>
              <option value="SUV">SUV</option>
              <option value="Van">Van</option>
              <option value="Electric">Electric</option>
            </select>
          </div>
          <div className="flex items-center">
            <input
              type="checkbox"
              checked={willingToPool}
              onChange={(e) => setWillingToPool(e.target.checked)}
              className="mr-2 rounded border-slate-300"
            />
            <label className="text-slate-700">Willing to Pool</label>
          </div>
          <button
            type="submit"
            disabled={loading}
            className={`w-full py-3 rounded-lg transition duration-200 font-semibold ${
              loading
                ? 'bg-slate-400 cursor-not-allowed'
                : 'bg-emerald-500 hover:bg-emerald-600 text-white'
            }`}
          >
            {loading ? "Booking..." : "Book Ride"}
          </button>
        </form>
      </div>
    </div>
  );
}

export default RiderBooking;
