import React, { useState, useEffect } from 'react';
import toast from 'react-hot-toast';
import { MdDelete, MdDirectionsRun } from 'react-icons/md';
import { getActivities, createActivity, deleteActivity, getActivityStats } from '../services/activityService';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorMessage from '../components/common/ErrorMessage';
import EmptyState from '../components/common/EmptyState';
import './Activities.css';

const Activities = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [activities, setActivities] = useState([]);
  const [stats, setStats] = useState(null);
  
  const [showForm, setShowForm] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  
  const [formData, setFormData] = useState({
    activityType: 'CARDIO',
    exerciseName: '',
    durationMinutes: '',
    caloriesBurned: '',
    intensity: 'MEDIUM',
    startTime: new Date().toISOString().slice(0, 16)
  });

  const fetchData = async () => {
    setLoading(true);
    try {
      const [activitiesRes, statsRes] = await Promise.all([
        getActivities(0, 50),
        getActivityStats()
      ]);
      setActivities(activitiesRes.data.content || activitiesRes.data || []);
      setStats(statsRes.data);
    } catch (err) {
      setError("Failed to load activities.");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      const payload = {
        ...formData,
        startTime: new Date(formData.startTime).toISOString()
      };
      await createActivity(payload);
      toast.success('Activity added successfully');
      setShowForm(false);
      fetchData();
    } catch (err) {
      toast.error('Failed to add activity');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this activity?')) {
      try {
        await deleteActivity(id);
        toast.success('Activity deleted');
        fetchData();
      } catch (err) {
        toast.error('Failed to delete activity');
      }
    }
  };

  if (loading && !activities.length) return <LoadingSpinner message="Loading activities..." />;
  if (error) return <ErrorMessage message={error} onRetry={fetchData} />;

  return (
    <div className="activities-page animate-slide-up">
      <div className="page-header d-flex justify-content-between align-items-center">
        <div>
          <h2>Your Activities</h2>
          <p>Track and manage your workouts.</p>
        </div>
        <button className="btn-primary" onClick={() => setShowForm(!showForm)}>
          {showForm ? 'Cancel' : 'Add Activity'}
        </button>
      </div>

      {stats && (
        <div className="stats-row mb-4">
          <div className="glass-card stat-mini">
            <span className="label">Total Workouts</span>
            <span className="value">{stats.totalWorkouts || 0}</span>
          </div>
          <div className="glass-card stat-mini">
            <span className="label">Total Time</span>
            <span className="value">{stats.totalDurationMinutes || 0} min</span>
          </div>
          <div className="glass-card stat-mini">
            <span className="label">Calories</span>
            <span className="value">{stats.totalCaloriesBurned || 0} kcal</span>
          </div>
        </div>
      )}

      {showForm && (
        <div className="glass-card mb-4 form-card animate-fade-in">
          <h3>Add New Activity</h3>
          <form onSubmit={handleSubmit} className="activity-form">
            <div className="form-row">
              <div className="form-group half">
                <label>Activity Type</label>
                <select name="activityType" value={formData.activityType} onChange={handleChange}>
                  <option value="CARDIO">Cardio</option>
                  <option value="STRENGTH">Strength</option>
                  <option value="FLEXIBILITY">Flexibility</option>
                  <option value="SPORTS">Sports</option>
                </select>
              </div>
              <div className="form-group half">
                <label>Exercise Name</label>
                <input type="text" name="exerciseName" value={formData.exerciseName} onChange={handleChange} required placeholder="e.g. Running" />
              </div>
            </div>
            <div className="form-row">
              <div className="form-group third">
                <label>Duration (mins)</label>
                <input type="number" name="durationMinutes" value={formData.durationMinutes} onChange={handleChange} required min="1" />
              </div>
              <div className="form-group third">
                <label>Calories Burned</label>
                <input type="number" name="caloriesBurned" value={formData.caloriesBurned} onChange={handleChange} required min="1" />
              </div>
              <div className="form-group third">
                <label>Intensity</label>
                <select name="intensity" value={formData.intensity} onChange={handleChange}>
                  <option value="LOW">Low</option>
                  <option value="MEDIUM">Medium</option>
                  <option value="HIGH">High</option>
                </select>
              </div>
            </div>
            <div className="form-group">
              <label>Date & Time</label>
              <input type="datetime-local" name="startTime" value={formData.startTime} onChange={handleChange} required />
            </div>
            <button type="submit" className="btn-primary" disabled={submitting}>
              {submitting ? 'Saving...' : 'Save Activity'}
            </button>
          </form>
        </div>
      )}

      <div className="glass-card">
        <h3>Activity History</h3>
        {activities.length > 0 ? (
          <div className="table-responsive">
            <table className="modern-table">
              <thead>
                <tr>
                  <th>Date</th>
                  <th>Type</th>
                  <th>Exercise</th>
                  <th>Duration</th>
                  <th>Calories</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {activities.map(act => (
                  <tr key={act.id}>
                    <td>{new Date(act.startTime).toLocaleString()}</td>
                    <td><span className={`badge badge-${act.activityType.toLowerCase()}`}>{act.activityType}</span></td>
                    <td>{act.exerciseName}</td>
                    <td>{act.durationMinutes} min</td>
                    <td>{act.caloriesBurned} kcal</td>
                    <td>
                      <button className="icon-btn danger" onClick={() => handleDelete(act.id)}>
                        <MdDelete />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <EmptyState 
            icon={MdDirectionsRun}
            title="No activities recorded"
            description="You haven't recorded any workouts yet. Click 'Add Activity' to get started."
          />
        )}
      </div>
    </div>
  );
};

export default Activities;
