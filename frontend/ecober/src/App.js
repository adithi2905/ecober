import './App.css';
import './index.css'; 
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import RiderBooking from './components/riderBooking';
import BookingConfirmation from './components/bookingConfirmation';
import DashboardLayout from './dashboards/dashboardLayout';
import TripHistory from './components/tripsHistory';
import Profile from './components/Profile';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* All dashboard content goes inside this layout */}
        <Route element={<DashboardLayout />}>
          <Route path="/rideBooking" element={<RiderBooking />} />
          <Route path="/bookingConfirmation" element={<BookingConfirmation />} />
          <Route path="/tripsHistory" element={<TripHistory />} />
            <Route path="/profile" element={<Profile />} />
        
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
