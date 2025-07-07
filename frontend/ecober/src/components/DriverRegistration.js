import { useState } from "react";
import { useNavigate } from "react-router-dom";

function DriverRegistration() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [location, setLocation] = useState("");
  const [vehicleType, setVehicleType] = useState("");
  const [isVerified, setIsVerified] = useState(false);
  const [vehicleNo, setVehicleNo] = useState("");
  const [vin,setVin]=useState("");

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch("http://localhost:8080/driver/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          name,
          email,
          password,
          location,
          vehicleType,
          isVerified,
          vehicleNo,
          vin
        }),
      });

      if (response.ok) {
        alert("✅ Registration successful!");
        navigate("/driverLogin");
      } else {
        alert("❌ Registration failed.");
      }
    } catch (error) {
      console.error("Error during registration:", error);
      alert("⚠️ Something went wrong.");
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-slate-50 to-slate-200 p-4">
      <div className="w-full max-w-md bg-white rounded-2xl shadow-lg p-8">
        <h2 className="text-3xl font-bold text-center text-slate-800 mb-6">
          Driver Registration
        </h2>
        <form onSubmit={handleSubmit} className="space-y-4">
          {/* Name */}
          <input
            type="text"
            name="name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
            placeholder="Full Name"
            className="w-full px-4 py-3 rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />

          {/* Email */}
          <input
            type="email"
            name="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            placeholder="Email"
            className="w-full px-4 py-3 rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />

          {/* Password */}
          <input
            type="password"
            name="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            placeholder="Password"
            className="w-full px-4 py-3 rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />

          {/* Location */}
          <input
            type="text"
            name="location"
            value={location}
            onChange={(e) => setLocation(e.target.value)}
            required
            placeholder="Location"
            className="w-full px-4 py-3 rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />

          {/* Vehicle Type */}
          <input
            type="text"
            name="vehicleType"
            value={vehicleType}
            onChange={(e) => setVehicleType(e.target.value)}
            required
            placeholder="Vehicle Type"
            className="w-full px-4 py-3 rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />

          {/* Vehicle Number */}
          <input
            type="text"
            name="vehicleNo"
            value={vehicleNo}
            onChange={(e) => setVehicleNo(e.target.value)}
            required
            placeholder="Vehicle Number"
            className="w-full px-4 py-3 rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />

          {/* Verified Checkbox */}
          <div className="flex items-center space-x-2">
            <input
              type="checkbox"
              name="isVerified"
              checked={isVerified}
              onChange={(e) => setIsVerified(e.target.checked)}
              className="h-5 w-5 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
            />
            <label className="text-slate-700 text-sm">Verified Driver?</label>
          </div>

          {/*Vin*/}
          <input
            type="text"
            name="vin"
            value={vin}
            onChange={(e) => setVin(e.target.value)}
            required
            placeholder="Vehicle Identification Number"
            className="w-full px-4 py-3 rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />

          {/* Submit Button */}
          <button
            type="submit"
            className="w-full py-3 bg-gradient-to-r from-blue-500 to-indigo-600 text-white rounded-lg shadow hover:from-blue-600 hover:to-indigo-700 transition"
          >
            Register
          </button>
        </form>

        {/* Back to Login */}
        <p className="text-center text-slate-500 text-sm mt-4">
          Already have an account?{" "}
          <span
            onClick={() => navigate("/driverLogin")}
            className="text-blue-600 font-medium hover:underline cursor-pointer"
          >
            Log in here
          </span>
        </p>
      </div>
    </div>
  );
}

export default DriverRegistration;
