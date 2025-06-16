import React, { useEffect, useState } from 'react';
import axios from 'axios';

const DriverProfile = () => {
  const [driver, setDriver] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDriverProfile = async () => {
      try {
        const token = localStorage.getItem('token'); 
        const response = await axios.get('/driver/getProfile', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setDriver(response.data);
      } catch (error) {
        console.error('Error fetching driver profile:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchDriverProfile();
  }, []);

  if (loading) return <div className="p-6">Loading profile...</div>;
  if (!driver) return <div className="p-6 text-red-500">Failed to load profile.</div>;

  return (
    <div className="max-w-4xl mx-auto bg-white shadow-lg rounded-xl p-6 mt-6">
      <h2 className="text-2xl font-bold text-maroon-700 mb-4">Driver Profile</h2>

      <div className="mb-6">
        <h3 className="text-xl font-semibold text-gray-700 mb-2">Personal Information</h3>
        <div className="space-y-1 text-gray-600">
          <p><strong>Name:</strong> {driver.name}</p>
          <p><strong>Email:</strong> {driver.email}</p>
          <p><strong>Phone:</strong> {driver.phone || 'N/A'}</p>
        </div>
      </div>

      <div className="mb-6">
        <h3 className="text-xl font-semibold text-gray-700 mb-2">Vehicle Information</h3>
        <div className="space-y-1 text-gray-600">
          <p><strong>Type:</strong> {driver.vehicleType}</p>
          <p><strong>Model:</strong> {driver.vehicleModel}</p>
          <p><strong>License Plate:</strong> {driver.licensePlate}</p>
        </div>
      </div>

      <div className="mb-6 grid grid-cols-2 gap-6">
        <div className="bg-gray-100 p-4 rounded-lg shadow-inner">
          <h4 className="font-medium text-gray-700">Total Trips</h4>
          <p className="text-2xl font-bold text-maroon-700">{driver.totalTrips}</p>
        </div>
        <div className="bg-gray-100 p-4 rounded-lg shadow-inner">
          <h4 className="font-medium text-gray-700">Average Rating</h4>
          <p className="text-2xl font-bold text-yellow-500">{driver.rating} ★</p>
        </div>
        <div className="bg-gray-100 p-4 rounded-lg shadow-inner">
          <h4 className="font-medium text-gray-700">Acceptance Rate</h4>
          <p className="text-2xl font-bold text-green-600">{driver.acceptanceRate}%</p>
        </div>
        <div className="bg-gray-100 p-4 rounded-lg shadow-inner">
          <h4 className="font-medium text-gray-700">CO₂ Saved</h4>
          <p className="text-2xl font-bold text-emerald-700">{driver.co2Saved} kg</p>
        </div>
      </div>
    </div>
  );
};

export default DriverProfile;
