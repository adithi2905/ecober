import React, { useEffect, useState } from 'react';
import { Co2Equiv } from './EcoUtils';

const VEHICLE_ICONS     = { Sedan: '🚗', SUV: '🚙', Van: '🚐', Electric: '⚡' };
const DAILY_CO2_GOAL    = 8;
const AVG_SAVED_PER_RIDE = 0.8;

function EcoPotentialCard({ ridesToday = 0 }) {
  const co2Today = ridesToday * AVG_SAVED_PER_RIDE;
  const progress = Math.min((co2Today / DAILY_CO2_GOAL) * 100, 100);

  return (
    <div className="bg-white border border-emerald-100 rounded-2xl p-5 mb-5 shadow-sm">
      <div className="flex items-center justify-between mb-3 gap-4">
        <div>
          <h3 className="text-sm font-semibold text-slate-900">Today's Eco Potential</h3>
          <p className="text-xs text-slate-500 mt-1">
            {ridesToday} ride{ridesToday !== 1 ? 's' : ''} · {co2Today.toFixed(1)} kg CO₂ saved
          </p>
        </div>
        <div className="text-right">
          <p className="text-lg font-semibold text-emerald-800">{progress.toFixed(0)}%</p>
          <p className="text-xs text-slate-500">of {DAILY_CO2_GOAL} kg goal</p>
        </div>
      </div>
      <Co2Equiv kg={co2Today} className="mt-3 text-slate-600" />
      <div className="relative h-3 bg-slate-100 rounded-full overflow-hidden mt-4">
        <div
          style={{
            position: 'absolute',
            inset: 0,
            backgroundImage: 'repeating-linear-gradient(90deg, rgba(16,185,129,0.12) 0, rgba(16,185,129,0.12) 5px, transparent 5px, transparent 12px)',
            backgroundSize: '28px 100%',
            animation: 'dashScroll 1.4s linear infinite',
          }}
        />
        <div
          style={{
            position: 'absolute',
            inset: '0 auto 0 0',
            width: `${progress}%`,
            background: 'linear-gradient(90deg, #6ee7b7, #10b981)',
            borderRadius: '9999px',
            transition: 'width 0.7s ease',
          }}
        />
      </div>
    </div>
  );
}

function DriverAvailableTrips({ ridesToday = 0 }) {
  const [rides, setRides]         = useState([]);
  const [loading, setLoading]     = useState(true);
  const [accepting, setAccepting] = useState(null);
  const [feedback, setFeedback]   = useState(null);
  const token = localStorage.getItem('token');

  useEffect(() => {
    fetch('http://localhost:8080/driver/fetchRides', {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(r => r.ok ? r.json() : [])
      .then(setRides)
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [token]);

  const handleAcceptRide = async rideRequestId => {
    if (accepting) return;
    setAccepting(rideRequestId);
    setFeedback(null);
    try {
      const res = await fetch(`http://localhost:8080/driver/acceptRide/${rideRequestId}`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${token}` },
      });
      if (!res.ok) {
        const data = await res.json().catch(() => ({}));
        const msg = data.message || '';
        if (msg.includes('active trip'))
          setFeedback({ type: 'error', text: 'You already have an active trip. Complete it first.' });
        else if (msg.includes('already accepted'))
          setFeedback({ type: 'error', text: 'This ride was already taken by another driver.' });
        else
          setFeedback({ type: 'error', text: 'Could not accept ride. Please try again.' });
        return;
      }
      setRides([]);
      setFeedback({ type: 'success', text: 'Ride accepted! Head to the Current Trip tab.' });
    } catch {
      setFeedback({ type: 'error', text: 'Network error. Please try again.' });
    } finally {
      setAccepting(null);
    }
  };

  if (loading) {
    return (
      <div className="p-8 space-y-3">
        {[1, 2, 3].map(i => <div key={i} className="bg-white rounded-2xl h-32 animate-pulse" />)}
      </div>
    );
  }

  return (
    <div className="p-8">
      <div className="flex items-baseline justify-between mb-5">
        <h2 className="text-xl font-bold text-slate-800">Available Rides</h2>
        {rides.length > 0 && (
          <span className="text-sm text-slate-400">
            {rides.length} request{rides.length !== 1 ? 's' : ''}
          </span>
        )}
      </div>

      <EcoPotentialCard ridesToday={ridesToday} />

      {feedback && (
        <div className={`mb-4 px-4 py-3 rounded-xl text-sm font-medium border ${
          feedback.type === 'success'
            ? 'bg-emerald-50 text-emerald-700 border-emerald-200'
            : 'bg-red-50 text-red-600 border-red-200'
        }`}>
          {feedback.text}
        </div>
      )}

      {rides.length === 0 && !feedback ? (
        <div className="bg-slate-50 border border-emerald-100 rounded-2xl p-12 text-center">
          <h3 className="text-base font-semibold text-slate-900 mb-2">
            You’re online and ready to create impact
          </h3>
          <p className="text-sm text-slate-500">Each accepted ride saves an average of 2.1 kg CO₂.</p>
        </div>
      ) : (
        <div className="space-y-3">
          {rides.map(ride => (
            <RideCard
              key={ride.rideRequestId}
              ride={ride}
              accepting={accepting === ride.rideRequestId}
              onAccept={() => handleAcceptRide(ride.rideRequestId)}
            />
          ))}
        </div>
      )}
    </div>
  );
}

function RideCard({ ride, accepting, onAccept }) {
  return (
    <div className="bg-white rounded-xl border border-gray-300 hover:border-emerald-400 hover:shadow-lg transition-all overflow-hidden shadow-md">
      <div className="px-5 py-4 flex items-stretch gap-4">
        <div className="flex flex-col items-center py-0.5 flex-shrink-0">
          <div className="w-2.5 h-2.5 rounded-full bg-emerald-600 ring-4 ring-emerald-100" />
          <div className="w-px flex-1 bg-gray-300 my-1.5" />
          <div className="w-2.5 h-2.5 rounded-full bg-gray-400 ring-4 ring-gray-100" />
        </div>

        <div className="flex-1 flex flex-col justify-between gap-2 min-w-0">
          <div>
            <p className="text-xs text-gray-500 uppercase tracking-wide font-medium">Pickup</p>
            <p className="text-sm font-medium text-gray-900 mt-0.5 truncate">{ride.pickupLocation}</p>
          </div>
          <div>
            <p className="text-xs text-gray-500 uppercase tracking-wide font-medium">Drop-off</p>
            <p className="text-sm font-medium text-gray-900 mt-0.5 truncate">{ride.dropoffLocation}</p>
          </div>
        </div>

        <div className="flex flex-col items-end justify-between flex-shrink-0">
          <div className="text-right space-y-1">
            <div className="flex items-center justify-end space-x-1.5">
              <span className="text-sm">{VEHICLE_ICONS[ride.preferredVehicleType] ?? '🚗'}</span>
              <span className="text-xs text-gray-700 font-medium">{ride.preferredVehicleType || 'Any'}</span>
            </div>
            {ride.willingToPool && (
              <span className="inline-block text-xs bg-emerald-50 text-emerald-700 border border-emerald-300 px-2 py-0.5 rounded-full font-medium shadow-sm">
                Pooling OK
              </span>
            )}
          </div>
          <button
            onClick={onAccept}
            disabled={accepting}
            className="px-5 py-2 bg-gradient-to-br from-emerald-600 to-emerald-700 hover:from-emerald-700 hover:to-emerald-800 disabled:opacity-60 text-white text-sm font-semibold rounded-lg transition-all shadow-md hover:shadow-lg"
          >
            {accepting ? 'Accepting…' : 'Accept'}
          </button>
        </div>
      </div>
    </div>
  );
}

export default DriverAvailableTrips;
