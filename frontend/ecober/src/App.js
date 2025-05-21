import React from "react";
import {BrowserRouter,Routes,Route} from 'react-router-dom';
import './App.css';
import ChatBot from './components/Chatbot';

function App() {
  return (
    <BrowserRouter>
    <Routes>
      <Route path="/chatbot" element={<ChatBot/>}></Route>
    </Routes>
    </BrowserRouter>
  );
}

export default App;
