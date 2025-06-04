import './App.css';
import './index.css'; 
import { BrowserRouter, Routes,Route } from 'react-router-dom';
import RiderBooking from './components/riderBooking';

function App() {
  return (
    <BrowserRouter>
    <Routes>
      <Route path='/rideBooking' element={<RiderBooking/>}></Route>
    </Routes>
    </BrowserRouter>
  );
}

export default App;
