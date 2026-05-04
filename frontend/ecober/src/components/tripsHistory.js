import React, { useEffect, useState } from 'react';

const STATUS_STYLES = {
  COMPLETED:   'bg-emerald-50 text-emerald-700 border border-emerald-200',
  CANCELLED:   'bg-red-50 text-red-600 border border-red-200',
  IN_PROGRESS: 'bg-blue-50 text-blue-700 border border-blue-200',
  ACCEPTED:    'bg-amber-50 text-amber-700 border border-amber-200',
};

const VEHICLE_ICONS = {
  Sedan:    '🚗',
  SUV:      '🚙',
  Van:      '🚐',
  Electric: '⚡',
};

function formatDate(str) {
  if (!str) return 'N/A';
  return new Date(str).toLocaleDateString('en-US', {
    weekday: 'short', month: 'short', day: 'numeric',
  });
}

function formatTime(str) {
  if (!str) return '';
  return new Date(str).toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
}

function TripHistory() {
  const [trips, setTrips]   = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('token');
    fetch('http://localhost:8080/user/tripsHistory', {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(r => r.ok ? r.json() : [])
      .then(d => setTrips(d || []))
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <div className="p-8 space-y-3">
        {[1, 2, 3].map(i => (
          <div key={i} className="bg-white rounded-2xl h-28 animate-pulse" />
        ))}
      </div>
    );
  }

  return (
    <div className="p-8">
      <div className="flex items-baseline justify-between mb-6">
        <h2 className="text-xl font-bold text-slate-800">Past Rides</h2>
        {trips.length > 0 && (
          <span className="text-sm text-slate-400">
            {trips.length} ride{trips.length !== 1 ? 's' : ''}
          </span>
        )}
      </div>

      {trips.length === 0 ? (
        <div className="bg-white rounded-2xl border border-slate-100 p-12 text-center">
          <div className="w-14 h-14 bg-slate-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <span className="text-2xl">🚗</span>
          </div>
          <h3 className="text-sm font-semibold text-slate-600 mb-1">No past rides yet</h3>
          <p className="text-xs text-slate-400">Your completed rides will appear here.</p>
        </div>
      ) : (
        <div className="space-y-3">
          {trips.map((ride, index) => (
            <TripCard key={ride.tripId} ride={ride} index={index} />
          ))}
        </div>
      )}
    </div>
  );
}

function TripCard({ ride, index }) {
  return (
    <div className="bg-white rounded-2xl border border-slate-100 hover:border-emerald-200 hover:shadow-md transition-all duration-200 overflow-hidden">
      {/* Card header */}
      <div className="flex items-center justify-between px-5 py-2.5 bg-slate-50 border-b border-slate-100">
        <div className="flex items-center space-x-2.5">
          <span className="text-xs text-slate-300 font-mono">
            #{String(index + 1).padStart(2, '0')}
          </span>
          <span className={`text-xs font-semibold px-2.5 py-0.5 rounded-full ${STATUS_STYLES[ride.status] ?? 'bg-slate-100 text-slate-600'}`}>
            {ride.status}
          </span>
        </div>
        <div className="text-right">
          <span className="text-xs font-medium text-slate-600">{formatDate(ride.endTime)}</span>
          {ride.endTime && (
            <span className="text-xs text-slate-400 ml-2">{formatTime(ride.endTime)}</span>
          )}
        </div>
      </div>

      {/* Card body */}
      <div className="px-5 py-4 flex items-stretch gap-4">
        {/* Route dots */}
        <div className="flex flex-col items-center py-0.5 flex-shrink-0">
          <div className="w-2.5 h-2.5 rounded-full bg-emerald-500 ring-4 ring-emerald-50" />
          <div className="w-px flex-1 bg-slate-200 my-1.5" />
          <div className="w-2.5 h-2.5 rounded-full bg-red-400 ring-4 ring-red-50" />
        </div>

        {/* Locations */}
        <div className="flex-1 flex flex-col justify-between gap-2 min-w-0">
          <div>
            <p className="text-xs text-slate-400 uppercase tracking-wide font-medium">Pickup</p>
            <p className="text-sm font-medium text-slate-700 mt-0.5 truncate">{ride.pickupLocation || 'N/A'}</p>
          </div>
          <div>
            <p className="text-xs text-slate-400 uppercase tracking-wide font-medium">Drop-off</p>
            <p className="text-sm font-medium text-slate-700 mt-0.5 truncate">{ride.dropoffLocation || 'N/A'}</p>
          </div>
        </div>

        {/* Driver + fare */}
        <div className="text-right flex flex-col justify-between flex-shrink-0">
          <div>
            <div className="flex items-center justify-end space-x-1.5 mb-0.5">
              <span className="text-sm">{VEHICLE_ICONS[ride.driver?.vehicleType] ?? '🚗'}</span>
              <p className="text-sm font-semibold text-slate-700">{ride.driver?.driverName || 'Unknown'}</p>
            </div>
            <p className="text-xs text-slate-400">{ride.driver?.vehicleType || 'N/A'}</p>
          </div>
          {ride.fare != null && (
            <p className="text-sm font-bold text-emerald-600">${ride.fare.toFixed(2)}</p>
          )}
        </div>
      </div>

      {/* Eco footer */}
      {(ride.ecoScore != null || ride.carbonCost != null) && (
        <div className="px-5 py-2.5 border-t border-slate-50 bg-emerald-50/60 flex items-center space-x-5">
          {ride.ecoScore != null && (
            <span className="text-xs text-slate-600">
              🌱 Eco Score&nbsp;
              <strong className="text-emerald-700">{ride.ecoScore}</strong>
            </span>
          )}
          {ride.carbonCost != null && (
            <span className="text-xs text-slate-600">
              💨 CO₂&nbsp;
              <strong className="text-slate-700">{ride.carbonCost.toFixed(2)} kg</strong>
            </span>
          )}
        </div>
      )}
    </div>
  );
}

export default TripHistory;
