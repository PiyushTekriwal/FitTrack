import React, { useState, useEffect } from 'react';
import { MdGroup, MdDirectionsRun, MdEventNote } from 'react-icons/md';
import { getAdminStats } from '../services/adminService';
import { hasRole } from '../config/auth';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorMessage from '../components/common/ErrorMessage';
import EmptyState from '../components/common/EmptyState';
import './Admin.css';

const Admin = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [stats, setStats] = useState(null);

  // Check roles
  const isAdmin = hasRole('ADMIN');

  const fetchStats = async () => {
    if (!isAdmin) return;
    
    setLoading(true);
    try {
      const res = await getAdminStats();
      setStats(res.data);
    } catch (err) {
      setError('Failed to load admin statistics.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStats();
  }, [isAdmin]);

  if (!isAdmin) {
    return (
      <div className="admin-page animate-fade-in">
        <EmptyState 
          title="Access Denied"
          description="You do not have permission to view the admin panel."
        />
      </div>
    );
  }

  if (loading) return <LoadingSpinner message="Loading admin statistics..." />;
  if (error) return <ErrorMessage message={error} onRetry={fetchStats} />;

  return (
    <div className="admin-page animate-slide-up">
      <div className="page-header">
        <h2>Admin Dashboard</h2>
        <p>Platform wide statistics and monitoring.</p>
      </div>

      <div className="admin-stats-grid">
        <div className="glass-card admin-stat-card">
          <div className="admin-stat-icon user-icon">
            <MdGroup />
          </div>
          <div className="admin-stat-content">
            <h3>Total Users</h3>
            <div className="admin-stat-value">{stats?.totalUsers || 0}</div>
          </div>
        </div>

        <div className="glass-card admin-stat-card">
          <div className="admin-stat-icon activity-icon">
            <MdDirectionsRun />
          </div>
          <div className="admin-stat-content">
            <h3>Total Activities</h3>
            <div className="admin-stat-value">{stats?.totalActivities || 0}</div>
          </div>
        </div>

        <div className="glass-card admin-stat-card">
          <div className="admin-stat-icon plan-icon">
            <MdEventNote />
          </div>
          <div className="admin-stat-content">
            <h3>Total Plans</h3>
            <div className="admin-stat-value">{stats?.totalPlans || 0}</div>
          </div>
        </div>
      </div>
      
      <div className="glass-card mt-4">
        <h3>System Status</h3>
        <div className="system-status">
          <div className="status-item">
            <span className="status-dot healthy"></span> API Gateway: <strong>Online</strong>
          </div>
          <div className="status-item">
            <span className="status-dot healthy"></span> Auth Service: <strong>Online</strong>
          </div>
          <div className="status-item">
            <span className="status-dot healthy"></span> AI Service: <strong>Online</strong>
          </div>
          <div className="status-item">
            <span className="status-dot healthy"></span> Database: <strong>Online</strong>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Admin;

