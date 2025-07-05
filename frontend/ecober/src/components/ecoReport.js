import React, { useEffect, useState } from 'react';
import {
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
  const [totalCo2, setTotalCo2] = useState(0);
  const [carbonScore, setCarbonScore] = useState(0);
  const [ecoBadge, setEcoBadge] = useState('');
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
        setTotalCo2(data.totalCO2 || 0);
        setCarbonScore(data.carbonScore || 0);
        setEcoBadge(data.carbonRating || '');
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

  if (loading) return <div className="text-center py-10">Loading Eco Report...</div>;
  if (error) return <div className="text-center py-10 text-red-600">{error}</div>;

  const currentMonthSaved = getCurrentMonthCO2();
  const goalProgress = Math.min((currentMonthSaved / MONTHLY_TARGET) * 100, 100);

  return (
    <div className="p-6 space-y-6">
      <h2 className="text-2xl font-bold text-center text-green-700">Eco Report</h2>

      <div className="text-center text-lg font-medium space-y-1">
        <div>Total CO₂ Saved: <span className="text-green-600 font-bold">{totalCo2.toFixed(2)} kg</span></div>
        <div>Carbon Score: <span className="text-blue-600 font-bold">{carbonScore.toFixed(1)}</span></div>
        <div>Eco Badge: <span className="text-purple-600 font-bold">{ecoBadge}</span></div>
      </div>

      {/* Green Goal Progress */}
      <div className="bg-gray-100 p-4 rounded-lg shadow text-center">
        <h3 className="text-lg font-semibold text-green-800">Your Green Goal</h3>
        <p>Target: {MONTHLY_TARGET} kg | Saved: {currentMonthSaved.toFixed(2)} kg</p>
        <div className="w-full bg-gray-300 h-4 rounded mt-2">
          <div
            className="h-4 bg-green-500 rounded"
            style={{ width: `${goalProgress}%` }}
          />
        </div>
        <p className="mt-1 text-sm text-gray-600">{goalProgress.toFixed(0)}% of your goal</p>
      </div>

      {/* Monthly CO₂ Savings Bar Chart */}
      <div className="bg-white p-4 rounded-lg shadow">
        <h3 className="text-lg font-semibold mb-2">Monthly CO₂ Savings</h3>
        <BarChart width={500} height={300} data={monthlyData}>
          <XAxis dataKey="month" />
          <YAxis />
          <Tooltip />
          <Bar dataKey="co2" fill="#22c55e" />
        </BarChart>
      </div>

      {/* Ride Type Pie Chart */}
      <div className="bg-white p-4 rounded-lg shadow">
        <h3 className="text-lg font-semibold mb-2">Ride Type Distribution</h3>
        <PieChart width={400} height={300}>
          <Pie
            data={rideTypeData}
            cx="50%"
            cy="50%"
            labelLine={false}
            label={({ name, percent }) => `${name} (${(percent * 100).toFixed(0)}%)`}
            outerRadius={100}
            fill="#8884d8"
            dataKey="value"
          >
            {rideTypeData.map((entry, index) => (
              <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
            ))}
          </Pie>
          <Tooltip />
          <Legend />
        </PieChart>
      </div>
    </div>
  );
}

export default EcoReport;
