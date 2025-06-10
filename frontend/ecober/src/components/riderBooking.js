import React, { useState } from 'react';

function RiderBooking() {
  const [pickup, setPickup] = useState('');
  const [destination, setDestination] = useState('');

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
        riderPickupLocation: pickup,
        riderDropOffLocation: destination,
        preferredVehicleType: "Car",
        willingToPool: false,
      })
    });
    if (response.ok) {
      const data = await response.json();
      alert(`Ride booked successfully! ${JSON.stringify(data)}`);
    } else {
      alert("Failed to book ride. Please check your token or login again.");
    }
  } catch (error) {
    console.error("Error booking ride:", error);
    alert("Something went wrong.");
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
