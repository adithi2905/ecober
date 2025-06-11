import { useState } from "react";
import { useNavigate } from 'react-router-dom';

function Login()
{
    const [username,setUserName]=useState("");
    const [password,setPassword]=useState("");
    const navigate=useNavigate();

    const handleSubmit=async (e) =>
    {
        try{
        e.preventDefault();
        const response= await fetch("http://localhost:8080/user/auth/login",{
        method: "POST",
        headers: {
                    "Content-Type": "application/json"
                },
        body: JSON.stringify({username: username,
            password:password
        }),
        });

        if (response.ok) {
                const data = await response.json(); 
                localStorage.setItem("token", data.token);
                alert("Login successful!");
                navigate("/rideBooking")
            } else {
                alert("Login failed.");
            }
        } catch (error) {
            console.error("Error during login:", error);
            alert("Something went wrong.");
        }
    }

    return (<div><form onSubmit={handleSubmit}>
        <input type="text" name="username" value={username} onChange={(e)=>setUserName(e.target.value)} required/>
        <input type="password" name="password" value={password} onChange={(e)=>setPassword(e.target.value)} required/>
        <button type="submit">Submit</button>
    </form></div>)
}
export default Login;