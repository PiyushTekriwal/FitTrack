import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import Layout from './components/Layout/Layout';
import Dashboard from './pages/Dashboard';
import Profile from './pages/Profile';
import Activities from './pages/Activities';
import Recommendations from './pages/Recommendations';
import FitnessPlan from './pages/FitnessPlan';
import Admin from './pages/Admin';
import './App.css';

function App() {
  return (
    <>
      <Toaster position="top-right" toastOptions={{
        className: 'premium-toast',
        style: {
          background: 'rgba(25, 30, 60, 0.9)',
          color: '#fff',
          backdropFilter: 'blur(10px)',
          border: '1px solid rgba(255, 255, 255, 0.1)',
        }
      }} />
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<Dashboard />} />
          <Route path="profile" element={<Profile />} />
          <Route path="activities" element={<Activities />} />
          <Route path="recommendations" element={<Recommendations />} />
          <Route path="plans" element={<FitnessPlan />} />
          <Route path="admin" element={<Admin />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Route>
      </Routes>
    </>
  );
}

export default App;
