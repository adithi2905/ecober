import React,{useEffect,useState} from "react";
import { useNavigate } from "react-router-dom";

function CurrentRide()
{
    const [ride,setRide]=useState(null);
    const [loading,setLoading]=useState(true);
    const navigate=useNavigate();

    useEffect(()=>
    {
        const fetchCurrentRide=async()=>
        {
            const token=localStorage.getItem("token");
            try
            {
                const res=await fetch("http://localhost:8080/user/trip/current",
                    {
                        headers: {
                            "Authorization": `Bearer ${token}`},

                    });
                if(res.ok)
                {
                    const data=await res.json();
                    if(data)
                        setRide(data);
                }
            }
            catch (err) {
        console.error("Error fetching current ride", err);
      }
      setLoading(false);
    };

    fetchCurrentRide();
  }, []);

  const handleBookRide = () => {
    navigate("/rideBooking");
  };
  if (loading) return <div>Loading current ride...</div>;


return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="bg-white p-6 rounded-lg shadow-md text-center">
        {ride ? (
          <>
            <h2 className="text-xl font-bold mb-4 text-[#800000]">Your Ride is In Progress</h2>
            <p><strong>Driver:</strong> {ride.driver.driverName}</p>
            <p><strong>Vehicle:</strong> {ride.driver.vehicleType}</p>
            <p><strong>Pickup:</strong> {ride.user.pickupLocation}</p>
            <p><strong>Dropoff:</strong> {ride.user.dropoffLocation}</p>
            <p><strong>Status:</strong> {ride.status}</p>
          </>
        ) : (
          <>
            <h2 className="text-xl font-bold mb-4">No Ongoing Rides</h2>
            <button
              onClick={handleBookRide}
              className="mt-2 px-4 py-2 bg-[#800000] text-white rounded hover:bg-[#a00000]"
            >
              Book a Ride
            </button>
          </>
        )}
      </div>
    </div>
  );
}

export default CurrentRide;


