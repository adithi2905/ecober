import React, { useEffect, useState } from 'react';
import { Co2Equiv, EcoRing } from './EcoUtils';

const STATUS_STYLES = {
  COMPLETED:   'bg-emerald-50 text-emerald-700 border border-emerald-200',
  CANCELLED:   'bg-red-50 text-red-600 border border-red-200',
  IN_PROGRESS: 'bg-blue-50 text-blue-700 border border-blue-200',
};

function formatDate(str) {
  if (!str) return 'N/A';
  return new Date(str).toLocaleDateString('en-US', { weekday: 'short', month: 'short', day: 'numeric' });
}

function formatTime(str) {
  if (!str) return '';
  return new Date(str).toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
}

function DriverTripHistory() {
  const [trips, setTrips]   = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError]   = useState(null);

  useEffect(() => {
    const token = localStorage.getItem('token');
    fetch('http://localhost:8080/driver/me/past-trips', {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(r => r.ok ? r.json() : Promise.reject())
      .then(setTrips)
      .catch(() => setError('Failed to load trip history.'))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <div className="p-8 space-y-3">
        {[1, 2, 3].map(i => <div key={i} className="bg-white rounded-2xl h-28 animate-pulse" />)}
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-8 flex items-center justify-center">
        <p className="text-sm text-red-500">{error}</p>
      </div>
    );
  }

  const totalSaved = trips.reduce((sum, trip) => sum + (trip.route?.carbonCost || 0), 0);

  return (
    <div className="p-8">
      <div className="flex items-baseline justify-between mb-6">
        <div>
          <h2 className="text-xl font-bold text-slate-800">Trip History</h2>
          <p className="text-sm text-slate-500 mt-1">{trips.length} trip{trips.length !== 1 ? 's' : ''} • {totalSaved.toFixed(1)} kg CO₂ kept out of the atmosphere</p>
        </div>
      </div>

      {trips.length === 0 ? (
        <div className="bg-white rounded-2xl border border-slate-100 p-12 text-center">
          <div className="w-14 h-14 bg-slate-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <span className="text-2xl">🏁</span>
          </div>
          <h3 className="text-sm font-semibold text-slate-600 mb-1">No trips yet</h3>
          <p className="text-xs text-slate-400">Your completed trips will appear here.</p>
        </div>
      ) : (
        <div className="space-y-4">
          {trips.map((trip, index) => (
            <TripCard key={trip.tripId} trip={trip} index={index} />
          ))}
        </div>
      )}
    </div>
  );
}

function TripCard({ trip, index }) {
  const date = trip.endTime || trip.startTime;
  const savedKg = trip.route?.carbonCost || 0;
  return (
    <div className="bg-white rounded-lg border border-gray-300 hover:border-emerald-400 hover:shadow-lg transition-all overflow-hidden shadow-md">
      {/* Header */}
      <div className="flex items-center justify-between px-5 py-3 bg-gray-50 border-b border-gray-200">
        <div className="flex items-center gap-3">
          <span className="text-xs text-gray-400 font-mono">#{String(index + 1).padStart(2, '0')}</span>
          <span className="text-xs uppercase tracking-wide text-gray-700 font-semibold">Trip summary</span>
        </div>
        <div className="text-right">
          <div className={`inline-flex items-center gap-2 text-xs font-semibold px-2.5 py-1 rounded-full ${STATUS_STYLES[trip.status] ?? 'bg-gray-100 text-gray-700'}`}>
            {trip.status}
          </div>
          <div className="text-xs text-gray-600 mt-1">{formatDate(date)}{date ? ` • ${formatTime(date)}` : ''}</div>
        </div>
      </div>

      {/* Body */}
      <div className="px-5 py-4 grid grid-cols-[1.5fr_1fr] gap-4">
        <div className="flex flex-col justify-between gap-4 min-w-0">
          <div>
            <p className="text-xs text-gray-500 uppercase tracking-wide font-medium">From</p>
            <p className="text-sm font-medium text-gray-900 mt-0.5 truncate">
              {trip.route?.source?.address || 'N/A'}
            </p>
          </div>
          <div>
            <p className="text-xs text-gray-500 uppercase tracking-wide font-medium">To</p>
            <p className="text-sm font-medium text-gray-900 mt-0.5 truncate">
              {trip.route?.destination?.address || 'N/A'}
            </p>
          </div>
        </div>

        <div className="flex flex-col justify-between items-end text-right">
          <div>
            <p className="text-sm font-bold text-emerald-700">{savedKg.toFixed(1)} kg</p>
            <p className="text-xs text-gray-600">saved vs solo</p>
          </div>
          <EcoRing score={trip.ecoScore ?? 0} />
        </div>
      </div>

      <div className="px-5 py-3 bg-gray-50 border-t border-gray-200 text-xs text-gray-700 shadow-inner">
        <strong className="text-gray-900">Equivalent:</strong> <Co2Equiv kg={savedKg} />
      </div>
    </div>
  );
}

export default DriverTripHistory;
