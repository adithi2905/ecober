import React, { useState } from 'react';
import { useNavigate } from "react-router-dom";

function RiderBooking() {
  const [pickup, setPickup] = useState('');
  const [destination, setDestination] = useState('');
  const [preferredVehicleType, setPreferredVehicleType] = useState('Sedan');
  const [willingToPool, setWillingToPool] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    const token = localStorage.getItem("token");

    try {
      const response = await fetch("http://localhost:8080/ride/requestRide", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({
          pickupLocation: pickup,
          dropoffLocation: destination,
          preferredVehicleType: preferredVehicleType,
          willingToPool: willingToPool,
        })
      });

      if (response.ok) {
        const contentType = response.headers.get("content-type");
        let message = "Ride booked successfully.";

        if (contentType && contentType.includes("application/json")) {
          const data = await response.json();
          message = data.message || message;
        } else if (contentType && contentType.includes("text/plain")) {
          message = await response.text();
        }

        navigate("/bookingconfirmation", {
          state: {
            driver: null,
            pickup: pickup,
            destination: destination,
            status: message,
          },
        });
      } else {
        const errorText = await response.text();
        alert(errorText || "Failed to book ride");
      }
    } catch (error) {
      console.error("Error booking ride:", error);
      alert("Something went wrong. " + error.message);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="bg-white p-8 rounded-2xl shadow-2xl border border-gray-300 transform transition hover:scale-105 hover:shadow-[0px_10px_20px_rgba(0,0,0,0.3)]">
        <h2 className="text-2xl font-bold mb-6 text-center">Book Your Ride</h2>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block mb-1 text-gray-700">Pickup Location:</label>
            <input
              type="text"
              value={pickup}
              onChange={(e) => setPickup(e.target.value)}
              className="w-full border border-gray-300 p-2 rounded focus:outline-none focus:ring focus:ring-maroon-500"
              placeholder="Enter pickup location"
              required
            />
          </div>
          <div>
            <label className="block mb-1 text-gray-700">Destination Location:</label>
            <input
              type="text"
              value={destination}
              onChange={(e) => setDestination(e.target.value)}
              className="w-full border border-gray-300 p-2 rounded focus:outline-none focus:ring focus:ring-maroon-500"
              placeholder="Enter destination"
              required
            />
          </div>
          <div>
            <label className="block mb-1 text-gray-700">Preferred Vehicle Type:</label>
            <select
              value={preferredVehicleType}
              onChange={(e) => setPreferredVehicleType(e.target.value)}
              className="w-full border border-gray-300 p-2 rounded focus:outline-none"
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
              className="mr-2"
            />
            <label className="text-gray-700">Willing to Pool</label>
          </div>
          <button
            type="submit"
            className="w-full bg-[#800000] text-white py-2 px-4 rounded hover:bg-[#a00000] transition"
          >
            Book Ride
          </button>
        </form>
      </div>
    </div>
  );
}

export default RiderBooking;
