import React, { useEffect, useState } from 'react';

const DriverProfile = () => {
  const [driver, setDriver] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchDriverProfile = async () => {
      try {
        const token = localStorage.getItem('token'); 
        const response = await fetch('http://localhost:8080/driver/me/getProfile', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (response.ok) {
          const data = await response.json();
          setDriver(data);
        } else {
          const errorText = await response.text();
          setError('Failed to load profile: ' + errorText);
        }
      } catch (error) {
        console.error('Error fetching driver profile:', error);
        setError('Network error occurred');
      } finally {
        setLoading(false);
      }
    };

    fetchDriverProfile();
  }, []);

  if (loading) return <div className="p-6">Loading profile...</div>;
  if (error) return <div className="p-6 text-red-500">{error}</div>;
  if (!driver) return <div className="p-6 text-red-500">Failed to load profile.</div>;

  return (
    <div className="max-w-4xl mx-auto bg-white shadow-lg rounded-xl p-6 mt-6">
      <h2 className="text-2xl font-bold text-[#800000] mb-4">Driver Profile</h2>

      <div className="mb-6">
        <h3 className="text-xl font-semibold text-gray-700 mb-2">Personal Information</h3>
        <div className="space-y-1 text-gray-600">
          <p><strong>Name:</strong> {driver.name || 'N/A'}</p>
          <p><strong>Email:</strong> {driver.email || 'N/A'}</p>
          <p><strong>Phone:</strong> {driver.phone || 'N/A'}</p>
          <p><strong>Location:</strong> {driver.location || 'N/A'}</p>
        </div>
      </div>

      <div className="mb-6">
        <h3 className="text-xl font-semibold text-gray-700 mb-2">Vehicle Information</h3>
        <div className="space-y-1 text-gray-600">
          <p><strong>Type:</strong> {driver.vehicleType || 'N/A'}</p>
          <p><strong>Model:</strong> {driver.vehicleModel || 'N/A'}</p>
          <p><strong>License Plate:</strong> {driver.vehicleNo || driver.licensePlate || 'N/A'}</p>
          <p><strong>Verified:</strong> {driver.isVerified ? 'Yes' : 'No'}</p>
        </div>
      </div>

      <div className="mb-6 grid grid-cols-2 gap-6">
        <div className="bg-gray-100 p-4 rounded-lg shadow-inner">
          <h4 className="font-medium text-gray-700">Total Trips</h4>
          <p className="text-2xl font-bold text-[#800000]">{driver.totalTrips || 0}</p>
        </div>
        <div className="bg-gray-100 p-4 rounded-lg shadow-inner">
          <h4 className="font-medium text-gray-700">Average Rating</h4>
          <p className="text-2xl font-bold text-yellow-500">{driver.rating || 0} ★</p>
        </div>
        <div className="bg-gray-100 p-4 rounded-lg shadow-inner">
          <h4 className="font-medium text-gray-700">Acceptance Rate</h4>
          <p className="text-2xl font-bold text-green-600">{driver.acceptanceRate || 0}%</p>
        </div>
        <div className="bg-gray-100 p-4 rounded-lg shadow-inner">
          <h4 className="font-medium text-gray-700">CO₂ Saved</h4>
          <p className="text-2xl font-bold text-emerald-700">{driver.co2Saved || 0} kg</p>
        </div>
      </div>
    </div>
  );
};

export default DriverProfile;