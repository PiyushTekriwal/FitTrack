import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { currentUser } from '../../config/auth';
import { getUserProfile } from '../../services/userService';
import './Navbar.css';

const Navbar = () => {
  const location = useLocation();
  const [userName, setUserName] = useState(currentUser.name || 'User');
  
  useEffect(() => {
    // Load actual user profile name from DB
    getUserProfile()
      .then(res => {
        if (res.data) {
          const first = res.data.firstName || '';
          const last = res.data.lastName || '';
          const full = `${first} ${last}`.trim();
          if (full) setUserName(full);
        }
      })
      .catch(err => console.error("Navbar profile fetch error:", err));

    // Listen to profile updates live
    const handleUpdate = (e) => {
      if (e.detail) {
        const full = `${e.detail.firstName || ''} ${e.detail.lastName || ''}`.trim();
        if (full) setUserName(full);
      }
    };
    window.addEventListener('userProfileUpdated', handleUpdate);
    return () => window.removeEventListener('userProfileUpdated', handleUpdate);
  }, []);

  const getPageTitle = () => {
    const path = location.pathname;
    if (path === '/') return 'Dashboard';
    if (path.startsWith('/profile')) return 'Profile';
    if (path.startsWith('/activities')) return 'Activities';
    if (path.startsWith('/recommendations')) return 'AI Recommendations';
    if (path.startsWith('/plans')) return 'Fitness Plans';
    if (path.startsWith('/admin')) return 'Admin Panel';
    return 'Dashboard';
  };

  const initial = userName.charAt(0).toUpperCase() || 'U';

  return (
    <header className="navbar">
      <div className="navbar-title">
        <h1>{getPageTitle()}</h1>
      </div>
      
      <div className="navbar-user">
        <div className="user-info">
          <span className="user-name">{userName}</span>
        </div>
        <div className="user-avatar">
          {initial}
        </div>
      </div>
    </header>
  );
};

export default Navbar;
