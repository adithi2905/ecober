import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

function Logout() {
  const navigate = useNavigate();

  useEffect(() => {
    const logout = async () => {
      const token = localStorage.getItem("token");

      await fetch("http://localhost:8080/user/logout", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        }
      });

      localStorage.removeItem("token");
      navigate("/"); // Redirect to login or homepage
    };

    logout();
  }, [navigate]);

  return <div>Logging out...</div>;
}

export default Logout;
