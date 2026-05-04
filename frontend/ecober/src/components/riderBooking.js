import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

const VEHICLE_TYPES = ['Sedan', 'SUV', 'Van', 'Electric'];

function RiderBooking() {
  const [pickup, setPickup]           = useState('');
  const [destination, setDestination] = useState('');
  const [vehicleType, setVehicleType] = useState('Sedan');
  const [willingToPool, setWillingToPool] = useState(true);
  const [loading, setLoading]         = useState(false);
  const [error, setError]             = useState('');
  const [co2Saved, setCo2Saved]       = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) return;
    fetch('http://localhost:8080/user/profile', {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(r => r.ok ? r.json() : null)
      .then(d => { if (d) setCo2Saved(d.totalCO2Saved ?? 0); })
      .catch(() => {});
  }, []);

  const handleSubmit = async e => {
    e.preventDefault();
    const trimPickup = pickup.trim();
    const trimDest   = destination.trim();
    if (!trimPickup || !trimDest) {
      setError('Please enter valid pickup and destination locations.');
      return;
    }
    setError('');
    setLoading(true);
    try {
      const token = localStorage.getItem('token');
      const res = await fetch('http://localhost:8080/ride/requestRide', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
        body: JSON.stringify({
          pickupLocation:       trimPickup,
          dropoffLocation:      trimDest,
          preferredVehicleType: vehicleType,
          willingToPool,
        }),
      });
      if (res.ok) {
        const data = await res.json();
        navigate('/bookingconfirmation', {
          state: {
            driver:      null,
            pickup:      trimPickup,
            destination: trimDest,
            status:      data.message || 'Ride booked successfully.',
          },
        });
      } else {
        const text = await res.text();
        setError(text || 'Failed to book ride. Please try again.');
      }
    } catch {
      setError('Something went wrong. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-8 max-w-xl">
      {/* Green info banner */}
      <div className="bg-green-50 border border-green-200 rounded-2xl px-5 py-3.5 mb-6 flex items-start gap-3">
        <span className="text-lg flex-shrink-0 mt-0.5">🌿</span>
        <p className="text-sm text-emerald-700 leading-snug">
          Each ride saves avg. <strong>2.1 kg CO₂</strong> vs solo car
          {co2Saved != null && co2Saved > 0 && (
            <> — you've saved <strong>{co2Saved.toFixed(1)} kg</strong> so far</>
          )}
        </p>
      </div>

      {/* Booking card */}
      <div className="bg-white rounded-2xl border border-slate-100 shadow-sm">
        <div className="p-6">
          <form onSubmit={handleSubmit} className="space-y-5">

            {/* FROM */}
            <div className="bg-gray-50 rounded-xl px-4 py-3.5 flex items-center gap-3">
              <div className="w-3 h-3 rounded-full bg-blue-500 ring-4 ring-blue-50 flex-shrink-0" />
              <div className="flex-1 min-w-0">
                <p className="text-xs text-slate-400 uppercase tracking-wide font-medium mb-1">From</p>
                <input
                  type="text"
                  value={pickup}
                  onChange={e => { setPickup(e.target.value); setError(''); }}
                  placeholder="Pickup location"
                  className="w-full bg-transparent text-sm font-medium text-slate-700 placeholder-slate-300 outline-none"
                />
              </div>
            </div>

            {/* Connector */}
            <div className="flex items-center px-4 -mt-2 -mb-2">
              <div className="ml-1 w-px h-4 bg-slate-200" />
            </div>

            {/* TO */}
            <div className="bg-gray-50 rounded-xl px-4 py-3.5 flex items-center gap-3">
              <div className="w-3 h-3 rounded-full bg-red-400 ring-4 ring-red-50 flex-shrink-0" />
              <div className="flex-1 min-w-0">
                <p className="text-xs text-slate-400 uppercase tracking-wide font-medium mb-1">To</p>
                <input
                  type="text"
                  value={destination}
                  onChange={e => { setDestination(e.target.value); setError(''); }}
                  placeholder="Destination"
                  className="w-full bg-transparent text-sm font-medium text-slate-700 placeholder-slate-300 outline-none"
                />
              </div>
            </div>

            {/* Ride type */}
            <div>
              <p className="text-xs text-slate-400 uppercase tracking-wide font-medium mb-2.5">Ride Type</p>
              <div className="grid grid-cols-2 gap-3">
                <button
                  type="button"
                  onClick={() => setWillingToPool(true)}
                  className={`rounded-xl p-4 text-left border-2 transition-all ${
                    willingToPool
                      ? 'bg-green-50 border-green-400'
                      : 'bg-white border-slate-200 hover:border-slate-300'
                  }`}
                >
                  <p className={`text-sm font-bold ${willingToPool ? 'text-emerald-700' : 'text-slate-600'}`}>Pool Ride</p>
                  <p className={`text-xs mt-0.5 ${willingToPool ? 'text-emerald-600' : 'text-slate-400'}`}>-40% CO₂</p>
                </button>
                <button
                  type="button"
                  onClick={() => setWillingToPool(false)}
                  className={`rounded-xl p-4 text-left border-2 transition-all ${
                    willingToPool
                      ? 'bg-white border-slate-200 hover:border-slate-300'
                      : 'bg-slate-100 border-slate-400'
                  }`}
                >
                  <p className={`text-sm font-bold ${willingToPool ? 'text-slate-500' : 'text-slate-700'}`}>Solo Ride</p>
                  <p className="text-xs mt-0.5 text-slate-400">Standard</p>
                </button>
              </div>
            </div>

            {/* Vehicle type */}
            <div>
              <p className="text-xs text-slate-400 uppercase tracking-wide font-medium mb-2.5">Vehicle Type</p>
              <div className="grid grid-cols-4 gap-2">
                {VEHICLE_TYPES.map(v => (
                  <button
                    key={v}
                    type="button"
                    onClick={() => setVehicleType(v)}
                    className={`py-2 rounded-xl text-xs font-medium transition-all border ${
                      vehicleType === v
                        ? 'bg-emerald-600 text-white border-emerald-600'
                        : 'bg-white text-slate-500 border-slate-200 hover:border-emerald-300'
                    }`}
                  >
                    {v}
                  </button>
                ))}
              </div>
            </div>

            {/* Inline error */}
            {error && (
              <p className="text-sm text-amber-700 bg-amber-50 border border-amber-200 rounded-xl px-4 py-3">
                {error}
              </p>
            )}

            {/* CTA */}
            <button
              type="submit"
              disabled={loading}
              className="w-full py-3.5 bg-emerald-600 hover:bg-emerald-700 disabled:opacity-60 text-white rounded-xl font-semibold text-sm transition-all shadow-sm"
            >
              {loading ? 'Finding your ride…' : 'Find Eco Ride'}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}

export default RiderBooking;
