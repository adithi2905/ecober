import { BarChart, Bar, XAxis, YAxis, Tooltip, PieChart, Pie, Cell, Legend } from 'recharts';

const monthlyData = [
  { month: 'Jan', co2: 2.5 },
  { month: 'Feb', co2: 3.2 },
  { month: 'Mar', co2: 4.1 },
  { month: 'Apr', co2: 5.0 },
  { month: 'May', co2: 6.2 },
];

const rideTypeData = [
  { name: 'Pooled', value: 60 },
  { name: 'Solo', value: 30 },
  { name: 'Electric Vehicle', value: 10 },
];

const COLORS = ['#22c55e', '#3b82f6', '#a855f7'];

function EcoReport() {
  return (
    <div className="p-6 space-y-6">
      <h2 className="text-2xl font-bold text-center text-green-700">Eco Report</h2>

      <div className="text-center text-lg font-medium">
        Total CO₂ Saved: <span className="text-green-600 font-bold">24.7 kg</span>
      </div>

      {/* Monthly Savings Bar Chart */}
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
