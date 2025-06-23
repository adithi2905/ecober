import './App.css';
import './index.css'; 
import { BrowserRouter, Routes, Route } from 'react-router-dom';

import RiderBooking from './components/RiderBooking';
import BookingConfirmation from './components/bookingConfirmation';
import DashboardLayout from './dashboards/dashboardLayout';
import TripHistory from './components/tripsHistory';
import Profile from './components/Profile';
import EcoReport from './components/ecoReport';
import CurrentRide from './components/CurrentRide';

import Registration from './components/Registration';
import Login from './components/Login';
import Logout from './components/Logout';

import DriverRegistration from './components/DriverRegistration';
import DriverLogin from './components/DriverLogin'; 
import AvailableRides from './components/DriverAvailabletrips';
import DriverCurrentTrip from './components/DriverCurrentTrip';
import DriverDashboardLayout from './dashboards/DriverDashboardLayout';
import DriverTripHistory from './components/DriverTripHistory';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public Auth Routes */}
        <Route path="/login" element={<Login />} />
        <Route path="/logout" element={<Logout />} />
        <Route path="/registration" element={<Registration />} />
        <Route path="/driverRegister" element={<DriverRegistration />} />
        <Route path="/driverLogin" element={<DriverLogin />} />

        {/* Rider Dashboard */}
        <Route element={<DashboardLayout />}>
          <Route path="/rideBooking" element={<RiderBooking />} />
          <Route path="/bookingConfirmation" element={<BookingConfirmation />} />
          <Route path="/tripsHistory" element={<TripHistory />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/ecoReport" element={<EcoReport />} />
          <Route path="/currentRide" element={<CurrentRide />} />
        </Route>

        {/* Driver Dashboard */}
        <Route element={<DriverDashboardLayout />}>
          <Route path="/availableRides" element={<AvailableRides />} />
          <Route path="/driverCurrentTrip" element={<DriverCurrentTrip />} />
          <Route path="/driverTripHistory" element={<DriverTripHistory/>}/>
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
