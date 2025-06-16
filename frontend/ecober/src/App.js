import './App.css';
import './index.css'; 
import { BrowserRouter, Routes, Route } from 'react-router-dom';

import RiderBooking from './components/riderBooking';
import BookingConfirmation from './components/bookingConfirmation';
import DashboardLayout from './dashboards/dashboardLayout';
import TripHistory from './components/tripsHistory';
import Profile from './components/Profile';
import EcoReport from './components/ecoReport';
import GreenGoals from './components/greenGoals';
import Registration from './components/Registration';
import Login from './components/Login';
import Logout from './components/Logout';
import DriverRegistration from './components/DriverRegistration';
import DriverLogin from './components/DriverLogin'; 
import AvailableRides from './components/DriverAvailabletrips';
import CurrentRide from './components/CurrentRide';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/driverRegister" element={<DriverRegistration />} />
        <Route path="/driverLogin" element={<DriverLogin />} />
        <Route path="/login" element={<Login />} />
        <Route path="/logout" element={<Logout />} />
        <Route path="/registration" element={<Registration />} />
        <Route path="/availableRides" element={<AvailableRides/>}></Route>
        
        <Route element={<DashboardLayout />}>
          <Route path="/rideBooking" element={<RiderBooking />} />
          <Route path="/bookingConfirmation" element={<BookingConfirmation />} />
          <Route path="/tripsHistory" element={<TripHistory />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/ecoReport" element={<EcoReport />} />
          <Route path="/goals" element={<GreenGoals />} />
          <Route path="/currentRide" element={<CurrentRide/>}/>
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
