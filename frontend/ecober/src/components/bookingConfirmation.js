import React from 'react';
import { useLocation, useNavigate } from "react-router-dom";

function BookingConfirmation() {
  const location = useLocation();
  const navigate = useNavigate();
  const { driver, pickup, destination, status } = location.state || {};

  const handleGoBack = () => {
    navigate('/rideBooking');
  };

  return (
    <div className="p-8 min-h-screen flex items-center justify-center bg-gradient-to-br from-slate-50 to-slate-100">
      <div className="bg-white p-8 rounded-2xl shadow-2xl w-full max-w-lg">
        <h2 className="text-3xl font-bold text-emerald-600 text-center mb-6">
          {status || "Ride Booked Successfully!"}
        </h2>
        {driver ? (
          <div className="space-y-3 text-slate-700">
            <p><strong>Driver Name:</strong> {driver.driverName}</p>
            <p><strong>Driver Rating:</strong> ⭐ {driver.trustScore ?? 'N/A'}</p>
            <p><strong>Pickup Location:</strong> {pickup}</p>
            <p><strong>Dropoff Location:</strong> {destination}</p>
          </div>
        ) : (
          <p className="text-center text-red-600">Booking details not available. Please book a ride first.</p>
        )}
        <button
          onClick={handleGoBack}
          className="mt-6 w-full py-3 bg-emerald-500 hover:bg-emerald-600 text-white rounded-lg font-semibold transition"
        >
          Book Another Ride
        </button>
      </div>
    </div>
  );
}

export default BookingConfirmation;
