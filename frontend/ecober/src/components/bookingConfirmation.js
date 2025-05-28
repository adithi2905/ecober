function BookingConfirmation() {
  const driver = {
    name: "Aarav Kumar",
    rating: 4.9,
    pickup: "MG Road, Bengaluru",
    drop: "Indiranagar, Bengaluru",
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="bg-white p-8 rounded-2xl shadow-2xl border w-full max-w-md">
        <h2 className="text-2xl font-bold text-[#800000] -600 text-center mb-4">Ride Booked Successfully!</h2>
        <div className="space-y-2 text-gray-800">
          <p><strong>Driver Name:</strong> {driver.name}</p>
          <p><strong>Driver Rating:</strong> ⭐ {driver.rating}</p>
          <p><strong>Pickup Location:</strong> {driver.pickup}</p>
          <p><strong>Dropoff Location:</strong> {driver.drop}</p>
        </div>
      </div>
    </div>
  );
}

export default BookingConfirmation;
