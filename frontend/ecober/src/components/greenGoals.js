import React, { useState } from 'react';

function GreenGoals() {
  const [goal, setGoal] = useState(30); // goal in kg
  const [saved, setSaved] = useState(24.7); // from backend or stats

  const progress = Math.min(((saved / goal) * 100).toFixed(1), 100);

  return (
    <div className="max-w-xl mx-auto p-6 bg-white rounded-xl shadow space-y-4">
      <h2 className="text-2xl font-bold text-center text-green-700">Your Green Goals</h2>
      <p className="text-gray-700 text-center">Track your monthly CO₂ savings target</p>

      <div className="space-y-2">
        <p><strong>Target:</strong> {goal} kg</p>
        <p><strong>Saved:</strong> {saved} kg</p>

        <div className="w-full bg-gray-200 rounded-full h-5">
          <div
            className="bg-green-500 h-5 rounded-full text-white text-sm flex items-center justify-center"
            style={{ width: `${progress}%` }}
          >
            {progress}%
          </div>
        </div>
      </div>

      <div className="pt-4">
        <label className="block text-sm font-medium text-gray-700">Update Your Goal (kg):</label>
        <input
          type="number"
          value={goal}
          onChange={(e) => setGoal(e.target.value)}
          className="border p-2 mt-1 w-full rounded"
        />
      </div>
    </div>
  );
}

export default GreenGoals;
