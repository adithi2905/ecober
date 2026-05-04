import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

const VEHICLE_ICONS = {
  Sedan:    '🚗',
  SUV:      '🚙',
  Van:      '🚐',
  Electric: '⚡',
};

const STATUS_CONFIG = {
  ACCEPTED:    { label: 'Driver Assigned',  dot: 'bg-amber-400',   badge: 'bg-amber-50 text-amber-700 border-amber-200' },
  IN_PROGRESS: { label: 'Ride in Progress', dot: 'bg-emerald-400', badge: 'bg-emerald-50 text-emerald-700 border-emerald-200' },
  COMPLETED:   { label: 'Completed',        dot: 'bg-slate-400',   badge: 'bg-slate-50 text-slate-600 border-slate-200' },
};

function CurrentRide() {
  const [ride, setRide]       = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError]     = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('token');
    fetch('http://localhost:8080/user/trip/current', {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(r => {
        if (r.ok)          return r.json();
        if (r.status === 404) return null;
        throw new Error('Unable to fetch current ride.');
      })
      .then(setRide)
      .catch(e => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <div className="p-8">
        <div className="bg-white rounded-2xl h-56 animate-pulse max-w-xl" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-8">
        <div className="bg-red-50 border border-red-100 rounded-2xl p-8 text-center max-w-xl">
          <p className="text-sm text-red-600 mb-4">{error}</p>
          <button
            onClick={() => navigate('/rideBooking')}
            className="px-5 py-2.5 bg-emerald-500 hover:bg-emerald-600 text-white rounded-xl text-sm font-semibold transition-all"
          >
            Book a Ride
          </button>
        </div>
      </div>
    );
  }

  if (!ride) {
    return (
      <div className="p-8">
        <div className="bg-white border border-slate-100 rounded-2xl p-12 text-center max-w-xl">
          <div className="w-14 h-14 bg-emerald-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <span className="text-2xl">🚗</span>
          </div>
          <h3 className="text-base font-semibold text-slate-700 mb-1">No active ride</h3>
          <p className="text-sm text-slate-400 mb-6">You don't have any ongoing rides right now.</p>
          <button
            onClick={() => navigate('/rideBooking')}
            className="px-6 py-2.5 bg-emerald-500 hover:bg-emerald-600 text-white rounded-xl text-sm font-semibold transition-all shadow-sm"
          >
            Book a Ride
          </button>
        </div>
      </div>
    );
  }

  const cfg = STATUS_CONFIG[ride.status] ?? {
    label: ride.status,
    dot:   'bg-slate-400',
    badge: 'bg-slate-50 text-slate-600 border-slate-200',
  };

  return (
    <div className="p-8">
      <div className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden max-w-xl">
        {/* Status strip */}
        <div className={`flex items-center space-x-2 px-5 py-3 border-b ${cfg.badge}`}>
          <span className={`w-2 h-2 rounded-full ${cfg.dot} animate-pulse flex-shrink-0`} />
          <span className="text-sm font-semibold">{cfg.label}</span>
          <span className="text-xs ml-auto opacity-60">Trip #{ride.tripId}</span>
        </div>

        {/* Route */}
        <div className="px-5 py-5 flex items-stretch gap-4">
          <div className="flex flex-col items-center py-0.5 flex-shrink-0">
            <div className="w-3 h-3 rounded-full bg-emerald-500 ring-4 ring-emerald-50" />
            <div className="w-px flex-1 bg-slate-200 my-1.5" />
            <div className="w-3 h-3 rounded-full bg-red-400 ring-4 ring-red-50" />
          </div>
          <div className="flex-1 flex flex-col justify-between gap-2 min-w-0">
            <div>
              <p className="text-xs text-slate-400 uppercase tracking-wide font-medium">Pickup</p>
              <p className="text-sm font-semibold text-slate-700 mt-0.5">
                {ride.route?.source?.address || ride.pickupLocation || 'N/A'}
              </p>
            </div>
            <div>
              <p className="text-xs text-slate-400 uppercase tracking-wide font-medium">Drop-off</p>
              <p className="text-sm font-semibold text-slate-700 mt-0.5">
                {ride.route?.destination?.address || ride.dropoffLocation || 'N/A'}
              </p>
            </div>
          </div>
          {ride.fare != null && (
            <div className="text-right flex-shrink-0">
              <p className="text-xs text-slate-400 mb-1">Est. Fare</p>
              <p className="text-xl font-bold text-emerald-600">${ride.fare.toFixed(2)}</p>
            </div>
          )}
        </div>

        {/* Driver card */}
        <div className="px-5 py-4 bg-slate-50 border-t border-slate-100 flex items-center space-x-4">
          <div className="w-11 h-11 bg-gradient-to-br from-slate-200 to-slate-300 rounded-full flex items-center justify-center text-xl flex-shrink-0">
            {VEHICLE_ICONS[ride.driver?.vehicleType] ?? '🚗'}
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-sm font-semibold text-slate-700 truncate">
              {ride.driver?.driverName || 'Unknown Driver'}
            </p>
            <p className="text-xs text-slate-400">{ride.driver?.vehicleType || 'Vehicle'}</p>
          </div>
          <div className="w-9 h-9 bg-emerald-500 rounded-full flex items-center justify-center text-white text-sm font-bold flex-shrink-0">
            {ride.driver?.driverName?.[0]?.toUpperCase() ?? '?'}
          </div>
        </div>
      </div>
    </div>
  );
}

export default CurrentRide;
