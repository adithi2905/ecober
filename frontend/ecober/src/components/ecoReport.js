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

function EcoReport() {
  const [totalCo2, setTotalCo2] = useState(0);
  const [monthlyData, setMonthlyData] = useState([]);
  const [rideTypeData, setRideTypeData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

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
        setTotalCo2(data.totalCo2Saved || 0);
        setMonthlyData(data.monthlyCo2Savings || []);
        setRideTypeData(data.riderDistribution || []);
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

  return (
    <div className="p-6 space-y-6">
      <h2 className="text-2xl font-bold text-center text-green-700">Eco Report</h2>

      <div className="text-center text-lg font-medium">
        Total CO₂ Saved: <span className="text-green-600 font-bold">{totalCo2.toFixed(2)} kg</span>
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
