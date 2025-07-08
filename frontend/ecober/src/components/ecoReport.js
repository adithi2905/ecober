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
} from 'recharts';

const COLORS = ['#22c55e', '#3b82f6', '#a855f7'];
const MONTHLY_TARGET = 30; // You can make this dynamic later

function EcoReport() {
  const [monthlyData, setMonthlyData] = useState([]);
  const [rideTypeData, setRideTypeData] = useState([]);
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
        const response = await fetch('http://localhost:8080/driver/me/eco-report', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          const errorText = await response.text();
          throw new Error(errorText || 'Failed to fetch eco report');
        }

        const data = await response.json();
        setMonthlyData(data.monthlyCo2Savings || []);
        setRideTypeData(data.rideTypeDistribution || []);
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
    return <div className="text-center py-10 animate-pulse text-gray-500">Loading Eco Report...</div>;

  if (error)
    return <div className="text-center py-10 text-red-600 font-medium">{error}</div>;

  const currentMonthSaved = getCurrentMonthCO2();
  const goalProgress = Math.min((currentMonthSaved / MONTHLY_TARGET) * 100, 100);

  return (
    <div className="p-6 space-y-6">
      <h2 className="text-3xl font-bold text-center text-green-700">🌿 Eco Report</h2>

      {/* Green Goal Progress */}
      <div className="bg-green-50 p-5 rounded-xl shadow-md text-center">
        <h3 className="text-xl font-semibold text-green-800">Your Green Goal</h3>
        <p className="mt-2 text-gray-700">
          🎯 Target: <span className="font-bold">{MONTHLY_TARGET} kg</span> | ✅ Saved:{" "}
          <span className="font-bold text-green-600">{currentMonthSaved.toFixed(2)} kg</span>
        </p>
        <div className="w-full bg-gray-200 h-4 rounded mt-4 overflow-hidden">
          <div
            className="h-4 bg-green-500 transition-all duration-700 ease-in-out"
            style={{ width: `${goalProgress}%` }}
          />
        </div>
        <p className="mt-2 text-sm text-gray-600">
          {goalProgress.toFixed(0)}% of your goal
        </p>
        {goalProgress >= 100 && (
          <p className="text-green-700 font-medium mt-1">🌱 You smashed your goal this month!</p>
        )}
      </div>

      {/* Monthly CO₂ Savings Bar Chart */}
      <div className="bg-white p-5 rounded-xl shadow-md">
        <h3 className="text-xl font-semibold mb-4 text-green-800">📊 Monthly CO₂ Savings</h3>
        <ResponsiveContainer width="100%" height={300}>
          <BarChart data={monthlyData}>
            <XAxis dataKey="month" />
            <YAxis />
            <Tooltip />
            <Bar dataKey="co2" fill="#22c55e" radius={[6, 6, 0, 0]} />
          </BarChart>
        </ResponsiveContainer>
      </div>

      {/* Ride Type Pie Chart */}
      <div className="bg-white p-5 rounded-xl shadow-md">
        <h3 className="text-xl font-semibold mb-4 text-green-800">🚘 Ride Type Distribution</h3>
        <ResponsiveContainer width="100%" height={300}>
          <PieChart>
            <Pie
              data={rideTypeData}
              cx="50%"
              cy="50%"
              labelLine={false}
              label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
              outerRadius={100}
              fill="#10b981"
              dataKey="value"
            >
              {rideTypeData.map((entry, index) => (
                <Cell
                  key={`cell-${index}`}
                  fill={COLORS[index % COLORS.length]}
                />
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
