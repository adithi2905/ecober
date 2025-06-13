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

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault(); 

    try {
      const response = await fetch("http://localhost:8080/driver/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          name,
          email,
          password,
          location,
          vehicleType,
          isVerified,
          vehicleNo
        })
      });

      if (response.ok) {
        alert("Registration successful!");
        navigate("/driverLogin");
      } else {
        alert("Registration failed.");
      }
    } catch (error) {
      console.error("Error during registration:", error);
      alert("Something went wrong.");
    }
  };

  return (
    <div>
      <form onSubmit={handleSubmit}>
        <input type="text" name="name" value={name} onChange={(e) => setName(e.target.value)} required placeholder="Name" />
        <input type="email" name="email" value={email} onChange={(e) => setEmail(e.target.value)} required placeholder="Email" />
        <input type="password" name="password" value={password} onChange={(e) => setPassword(e.target.value)} required placeholder="Password" />
        <input type="text" name="location" value={location} onChange={(e) => setLocation(e.target.value)} required placeholder="Location" />
        <input type="text" name="vehicleType" value={vehicleType} onChange={(e) => setVehicleType(e.target.value)} required placeholder="Vehicle Type" />
        
        <label>
          Verified:
          <input
            type="checkbox"
            name="isVerified"
            checked={isVerified}
            onChange={(e) => setIsVerified(e.target.checked)}
          />
        </label>

        <input type="text" name="vehicleNo" value={vehicleNo} onChange={(e) => setVehicleNo(e.target.value)} required placeholder="Vehicle Number" />

        <button type="submit">Register</button>
      </form>
    </div>
  );
}

export default DriverRegistration;
