import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { MdFitnessCenter, MdLocalFireDepartment, MdTimeline, MdAutoAwesome } from 'react-icons/md';
import { getUserProfile } from '../services/userService';
import { getActivityStats, getRecentActivities } from '../services/activityService';
import { getLatestRecommendation } from '../services/recommendationService';
import { getCurrentPlan } from '../services/planService';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorMessage from '../components/common/ErrorMessage';
import EmptyState from '../components/common/EmptyState';
import './Dashboard.css';

const Dashboard = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [data, setData] = useState({
    profile: null,
    stats: null,
    recentActivities: [],
    recommendation: null,
    plan: null
  });

  const fetchData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [profileRes, statsRes, activitiesRes, recRes, planRes] = await Promise.allSettled([
        getUserProfile(),
        getActivityStats(),
        getRecentActivities(7),
        getLatestRecommendation(),
        getCurrentPlan()
      ]);

      setData({
        profile: profileRes.status === 'fulfilled' ? profileRes.value.data : null,
        stats: statsRes.status === 'fulfilled' ? statsRes.value.data : null,
        recentActivities: activitiesRes.status === 'fulfilled' ? activitiesRes.value.data : [],
        recommendation: recRes.status === 'fulfilled' ? recRes.value.data : null,
        plan: planRes.status === 'fulfilled' ? planRes.value.data : null
      });
    } catch (err) {
      console.error(err);
      setError('Failed to load dashboard data. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  if (loading) return <LoadingSpinner message="Loading your dashboard..." />;
  if (error) return <ErrorMessage message={error} onRetry={fetchData} />;

  const { stats, recentActivities, recommendation, plan } = data;

  return (
    <div className="dashboard animate-slide-up">
      <div className="dashboard-header">
        <h2>Welcome back, <span className="gradient-text">{data.profile?.firstName || 'User'}</span></h2>
        <p>Here's your fitness summary for today.</p>
      </div>

      <div className="stats-grid">
        <div className="stat-card glass-card">
          <div className="stat-icon purple"><MdFitnessCenter /></div>
          <div className="stat-info">
            <h4>Total Workouts</h4>
            <h2>{stats?.totalWorkouts || 0}</h2>
          </div>
        </div>
        <div className="stat-card glass-card">
          <div className="stat-icon coral"><MdLocalFireDepartment /></div>
          <div className="stat-info">
            <h4>Calories Burned</h4>
            <h2>{stats?.totalCaloriesBurned || 0} kcal</h2>
          </div>
        </div>
        <div className="stat-card glass-card">
          <div className="stat-icon teal"><MdTimeline /></div>
          <div className="stat-info">
            <h4>Total Duration</h4>
            <h2>{stats?.totalDurationMinutes || 0} min</h2>
          </div>
        </div>
      </div>

      <div className="dashboard-grid">
        <div className="dashboard-column">
          <div className="section-card glass-card">
            <div className="section-header">
              <h3>Recent Activities</h3>
              <Link to="/activities" className="view-all">View All</Link>
            </div>
            
            {recentActivities.length > 0 ? (
              <div className="activity-list">
                {recentActivities.slice(0, 5).map(activity => (
                  <div key={activity.id} className="activity-item">
                    <div className="activity-icon">
                      <MdFitnessCenter />
                    </div>
                    <div className="activity-details">
                      <h4>{activity.exerciseName}</h4>
                      <p>{new Date(activity.startTime).toLocaleDateString()} • {activity.durationMinutes} min</p>
                    </div>
                    <div className="activity-calories">
                      {activity.caloriesBurned} kcal
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <EmptyState 
                title="No activities yet"
                description="Start recording your workouts to see them here."
                action={<Link to="/activities" className="btn-primary">Add Activity</Link>}
              />
            )}
          </div>
        </div>

        <div className="dashboard-column">
          <div className="section-card glass-card mb-4">
            <div className="section-header">
              <h3>Current Plan</h3>
              <Link to="/plans" className="view-all">View Plan</Link>
            </div>
            
            {plan ? (
              <div className="plan-preview">
                <h4>{plan.name || 'Active Fitness Plan'}</h4>
                <div className="progress-bar-container mt-3">
                  <div className="progress-bar" style={{ width: `${plan.completionPercentage || 0}%` }}></div>
                </div>
                <p className="progress-text">{plan.completionPercentage || 0}% Completed</p>
              </div>
            ) : (
              <EmptyState 
                icon={MdTimeline}
                title="No active plan"
                description="Create a personalized plan to reach your goals."
                action={<Link to="/plans" className="btn-primary">Create Plan</Link>}
              />
            )}
          </div>

          <div className="section-card glass-card">
            <div className="section-header">
              <h3>AI Recommendation</h3>
              <Link to="/recommendations" className="view-all">View Details</Link>
            </div>
            
            {recommendation ? (
              <div className="recommendation-preview">
                <p className="rec-summary">{recommendation.summary?.substring(0, 100)}...</p>
                <Link to="/recommendations" className="btn-primary btn-block mt-3" style={{display: 'block', textAlign: 'center'}}>
                  View Full Recommendation
                </Link>
              </div>
            ) : (
              <EmptyState 
                icon={MdAutoAwesome}
                title="No recommendations"
                description="Generate your first AI-powered fitness recommendation."
                action={<Link to="/recommendations" className="btn-primary">Generate Now</Link>}
              />
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
