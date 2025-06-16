import { useLocation } from "react-router-dom";

function BookingConfirmation() {
  const location = useLocation();
  const { driver, pickup, destination } = location.state || {};

  if (!driver) {
    return (
      <div className="text-center p-8 text-red-600">
        Booking details not available. Please book a ride first.
      </div>
    );
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="bg-white p-8 rounded-2xl shadow-2xl border w-full max-w-md">
        <h2 className="text-2xl font-bold text-[#800000] text-center mb-4">Ride Booked Successfully!</h2>
        <div className="space-y-2 text-gray-800">
          <p><strong>Driver Name:</strong> {driver.driverName}</p>
          <p><strong>Driver Rating:</strong> ⭐ {driver.trustScore ?? 'N/A'}</p>
          <p><strong>Pickup Location:</strong> {pickup}</p>
          <p><strong>Dropoff Location:</strong> {destination}</p>
        </div>
      </div>
    </div>
  );
}

export default BookingConfirmation;
