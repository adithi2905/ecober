import React, { useEffect, useState } from 'react';
import {
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  PieChart,
  Pie,
  Cell,
  Legend,
  CartesianGrid,
  LabelList,
} from 'recharts';

const COLORS = ['#22c55e', '#3b82f6', '#f97316', '#a855f7'];

function EcoReport() {
  const [monthlyData, setMonthlyData] = useState([]);
  const [rideTypeData, setRideTypeData] = useState([]);
  const [poolingData, setPoolingData] = useState([]);
  const [target, setTarget] = useState(30); // Default target
  const [newTarget, setNewTarget] = useState('');
  const [editingTarget, setEditingTarget] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const getCurrentMonthCO2 = () => {
    const currentMonth = new Date().toLocaleString('default', { month: 'short' }).toUpperCase();
    const entry = monthlyData.find((m) => m.month === currentMonth);
    return entry ? entry.co2 : 0;
  };

  useEffect(() => {
    const fetchEcoReport = async () => {
      try {
        const token = localStorage.getItem('token');

        // Fetch eco-report data
        const ecoResponse = await fetch('http://localhost:8080/driver/me/eco-report', {
          headers: { Authorization: `Bearer ${token}` },
        });

        if (!ecoResponse.ok) {
          const errorText = await ecoResponse.text();
          throw new Error(errorText || 'Failed to fetch eco report');
        }

        const ecoData = await ecoResponse.json();
        setMonthlyData(ecoData.monthlyCo2Savings || []);
        setRideTypeData(ecoData.rideTypeDistribution || []);

        // Fetch past trips for pooling data
        const tripsResponse = await fetch('http://localhost:8080/driver/me/past-trips', {
          headers: { Authorization: `Bearer ${token}` },
        });

        if (!tripsResponse.ok) {
          const errorText = await tripsResponse.text();
          throw new Error(errorText || 'Failed to fetch past trips');
        }

        const trips = await tripsResponse.json();
        const poolingCount = trips.filter((trip) => trip.is_pooling === 1).length;
        const soloCount = trips.length - poolingCount;

        setPoolingData([
          { name: 'Pooling', value: poolingCount },
          { name: 'Solo Rides', value: soloCount },
        ]);
      } catch (err) {
        console.error('Error fetching eco report:', err);
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchEcoReport();
  }, []);

  if (loading)
    return (
      <div className="text-center py-10 animate-pulse text-gray-500">
        Loading Eco Report...
      </div>
    );

  if (error)
    return (
      <div className="text-center py-10 text-red-600 font-medium">
        {error}
      </div>
    );

  const currentMonthSaved = getCurrentMonthCO2();
  const goalProgress = Math.min((currentMonthSaved / target) * 100, 100);

  // Fun Fact Generator
  const funFacts = [
    `🌱 You saved enough CO₂ to charge ${(currentMonthSaved * 1000).toFixed(0)} smartphones 📱`,
    `🌎 Your savings are equivalent to planting ${(currentMonthSaved / 21).toFixed(1)} trees 🌳`,
    `🚲 You could ride ${(currentMonthSaved * 5).toFixed(0)} km on an e-bike with that savings!`,
  ];
  const randomFact = funFacts[Math.floor(Math.random() * funFacts.length)];

  const handleSetTarget = () => {
    const newVal = parseFloat(newTarget);
    if (!isNaN(newVal) && newVal > 0) {
      setTarget(newVal);
      setEditingTarget(false);
      setNewTarget('');
    } else {
      alert('Please enter a valid positive number');
    }
  };

  return (
    <div className="p-6 space-y-8 bg-gradient-to-br from-green-50 to-green-100 min-h-screen">
      <h2 className="text-4xl font-extrabold text-center text-emerald-700 drop-shadow">
        🌿 Eco Report
      </h2>

      {/* Green Goal Progress */}
      <div className="bg-emerald-50 p-6 rounded-2xl shadow-lg text-center">
        <div className="flex justify-between items-center mb-4">
          <h3 className="text-2xl font-semibold text-emerald-800">Your Green Goal</h3>
          <button
            onClick={() => setEditingTarget(true)}
            className="px-3 py-1 rounded bg-green-600 text-white hover:bg-green-700 transition"
          >
            Set Target
          </button>
        </div>
        {editingTarget && (
          <div className="flex justify-center gap-2 mb-4">
            <input
              type="number"
              placeholder="Enter new target (kg)"
              value={newTarget}
              onChange={(e) => setNewTarget(e.target.value)}
              className="border rounded px-2 py-1 w-40 text-center"
            />
            <button
              onClick={handleSetTarget}
              className="px-3 py-1 rounded bg-emerald-500 text-white hover:bg-emerald-600 transition"
            >
              Save
            </button>
          </div>
        )}
        <p className="mt-2 text-gray-700">
          🎯 Target: <span className="font-bold">{target} kg</span> | ✅ Saved:{" "}
          <span className="font-bold text-green-600">{currentMonthSaved.toFixed(2)} kg</span>
        </p>
        <div className="relative w-full bg-gray-200 h-5 rounded-full mt-4 overflow-hidden">
          <div
            className="h-full bg-gradient-to-r from-green-400 to-green-600 rounded-full transition-all duration-700 ease-in-out"
            style={{ width: `${goalProgress}%` }}
          />
          <span className="absolute right-2 top-0 text-xs text-gray-700 font-medium">
            {goalProgress.toFixed(0)}%
          </span>
        </div>
        {goalProgress >= 100 && (
          <p className="text-green-700 font-medium mt-3">🌟 You smashed your goal this month!</p>
        )}
        <div className="mt-4 px-3 py-2 bg-green-100 text-emerald-800 rounded-full inline-block font-medium shadow-sm">
          💡 {randomFact}
        </div>
      </div>

      {/* Monthly CO₂ Savings Bar Chart */}
      <div className="bg-white p-6 rounded-2xl shadow-lg">
        <h3 className="text-xl font-semibold mb-4 text-emerald-800">📊 Monthly CO₂ Savings</h3>
        <ResponsiveContainer width="100%" height={300}>
          <BarChart data={monthlyData} barSize={40}>
            <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
            <XAxis dataKey="month" tick={{ fontSize: 14, fill: '#4b5563' }} />
            <YAxis tick={{ fontSize: 14, fill: '#4b5563' }} />
            <Tooltip
              contentStyle={{
                backgroundColor: '#f0fdf4',
                border: '1px solid #bbf7d0',
                color: '#16a34a',
                fontSize: 14,
              }}
            />
            <Bar dataKey="co2" radius={[10, 10, 0, 0]} fill="url(#colorUv)">
              <LabelList dataKey="co2" position="top" style={{ fill: '#16a34a', fontWeight: '600' }} />
            </Bar>
            <defs>
              <linearGradient id="colorUv" x1="0" y1="0" x2="0" y2="1">
                <stop offset="0%" stopColor="#4ade80" stopOpacity={0.8} />
                <stop offset="100%" stopColor="#16a34a" stopOpacity={0.8} />
              </linearGradient>
            </defs>
          </BarChart>
        </ResponsiveContainer>
      </div>

      {/* Ride Type Pie Chart */}
      <div className="bg-white p-6 rounded-2xl shadow-lg">
        <h3 className="text-xl font-semibold mb-4 text-emerald-800">🚘 Ride Type Distribution</h3>
        <ResponsiveContainer width="100%" height={300}>
          <PieChart>
            <defs>
              {rideTypeData.map((_, index) => (
                <radialGradient id={`grad-${index}`} key={index}>
                  <stop offset="30%" stopColor={COLORS[index % COLORS.length]} stopOpacity="0.8" />
                  <stop offset="100%" stopColor={COLORS[index % COLORS.length]} stopOpacity="1" />
                </radialGradient>
              ))}
            </defs>
            <Pie
              data={rideTypeData}
              cx="50%"
              cy="50%"
              labelLine={false}
              label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
              outerRadius={100}
              dataKey="value"
            >
              {rideTypeData.map((_, index) => (
                <Cell key={`cell-${index}`} fill={`url(#grad-${index})`} />
              ))}
            </Pie>
            <Tooltip />
            <Legend />
          </PieChart>
        </ResponsiveContainer>
      </div>

      {/* Pooling vs Solo Rides Pie Chart */}
      <div className="bg-white p-6 rounded-2xl shadow-lg">
        <h3 className="text-xl font-semibold mb-4 text-emerald-800">🤝 Pooling vs Solo Rides</h3>
        <ResponsiveContainer width="100%" height={300}>
          <PieChart>
            <defs>
              {poolingData.map((_, index) => (
                <radialGradient id={`pool-grad-${index}`} key={index}>
                  <stop offset="30%" stopColor={COLORS[index % COLORS.length]} stopOpacity="0.8" />
                  <stop offset="100%" stopColor={COLORS[index % COLORS.length]} stopOpacity="1" />
                </radialGradient>
              ))}
            </defs>
            <Pie
              data={poolingData}
              cx="50%"
              cy="50%"
              labelLine={false}
              label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
              outerRadius={100}
              dataKey="value"
            >
              {poolingData.map((_, index) => (
                <Cell key={`cell-${index}`} fill={`url(#pool-grad-${index})`} />
              ))}
            </Pie>
            <Tooltip />
            <Legend />
          </PieChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}

export default EcoReport;
