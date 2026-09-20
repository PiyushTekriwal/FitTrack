import React from 'react';
import { NavLink } from 'react-router-dom';
import { 
  MdDashboard, 
  MdPerson, 
  MdDirectionsRun, 
  MdAutoAwesome,
  MdEventNote,
  MdAdminPanelSettings,
  MdLogout
} from 'react-icons/md';
import { hasRole } from '../../config/auth';
import './Sidebar.css';

const Sidebar = () => {
  const isAdmin = hasRole('ADMIN');

  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <h2>Fit<span className="gradient-text">AI</span></h2>
      </div>
      
      <nav className="sidebar-nav">
        <NavLink to="/" className={({isActive}) => isActive ? "nav-link active" : "nav-link"}>
          <MdDashboard /> <span>Dashboard</span>
        </NavLink>
        <NavLink to="/profile" className={({isActive}) => isActive ? "nav-link active" : "nav-link"}>
          <MdPerson /> <span>Profile</span>
        </NavLink>
        <NavLink to="/activities" className={({isActive}) => isActive ? "nav-link active" : "nav-link"}>
          <MdDirectionsRun /> <span>Activities</span>
        </NavLink>
        <NavLink to="/recommendations" className={({isActive}) => isActive ? "nav-link active" : "nav-link"}>
          <MdAutoAwesome /> <span>AI Recommend</span>
        </NavLink>
        <NavLink to="/plans" className={({isActive}) => isActive ? "nav-link active" : "nav-link"}>
          <MdEventNote /> <span>Fitness Plans</span>
        </NavLink>
        
        {isAdmin && (
          <NavLink to="/admin" className={({isActive}) => isActive ? "nav-link active" : "nav-link"}>
            <MdAdminPanelSettings /> <span>Admin</span>
          </NavLink>
        )}
      </nav>

      <div className="sidebar-footer">
        <button className="logout-btn" onClick={() => window.location.reload()}>
          <MdLogout /> <span>Reset Session</span>
        </button>
      </div>
    </aside>
  );
};

export default Sidebar;
