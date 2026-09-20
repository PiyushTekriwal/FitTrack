import React, { useState, useEffect } from 'react';
import toast from 'react-hot-toast';
import { getUserProfile, updateProfile, getFitnessProfile, updateFitnessProfile } from '../services/userService';
import { setStoredUser } from '../config/auth';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorMessage from '../components/common/ErrorMessage';
import './Profile.css';

const Profile = () => {
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [savingFitness, setSavingFitness] = useState(false);
  const [error, setError] = useState(null);
  
  const [userProfile, setUserProfile] = useState({
    firstName: '',
    lastName: '',
    email: ''
  });
  
  const [fitnessProfile, setFitnessProfile] = useState({
    age: '',
    heightCm: '',
    weightKg: '',
    fitnessLevel: 'BEGINNER',
    fitnessGoal: 'WEIGHT_LOSS',
    dietaryPreference: 'NONE',
    activityPreferences: ''
  });

  const fetchData = async () => {
    setLoading(true);
    try {
      const [userRes, fitnessRes] = await Promise.allSettled([
        getUserProfile(),
        getFitnessProfile()
      ]);
      
      if (userRes.status === 'fulfilled' && userRes.value.data) {
        setUserProfile(userRes.value.data);
      }
      
      if (fitnessRes.status === 'fulfilled' && fitnessRes.value.data) {
        const fp = fitnessRes.value.data;
        setFitnessProfile({
          age: fp.age || '',
          heightCm: fp.heightCm || fp.height || '',
          weightKg: fp.weightKg || fp.weight || '',
          fitnessLevel: fp.fitnessLevel || 'BEGINNER',
          fitnessGoal: fp.fitnessGoal || 'WEIGHT_LOSS',
          dietaryPreference: fp.dietaryPreference || 'NONE',
          activityPreferences: Array.isArray(fp.activityPreferences) ? fp.activityPreferences.join(', ') : (fp.activityPreferences || '')
        });
      }
    } catch (err) {
      console.error(err);
      setError("Failed to load profile data.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleUserChange = (e) => {
    setUserProfile({ ...userProfile, [e.target.name]: e.target.value });
  };

  const handleFitnessChange = (e) => {
    setFitnessProfile({ ...fitnessProfile, [e.target.name]: e.target.value });
  };

  const saveUserProfile = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      const res = await updateProfile(userProfile);
      // Persist to session storage so future requests use this email as X-User-Id
      const updated = {
        email: userProfile.email,
        name: `${userProfile.firstName} ${userProfile.lastName}`.trim(),
        roles: ["USER", "ADMIN"]
      };
      setStoredUser(updated);
      window.dispatchEvent(new CustomEvent('userProfileUpdated', { detail: res.data || userProfile }));
      toast.success('Profile & Email updated successfully');
    } catch (err) {
      toast.error('Failed to update basic profile');
    } finally {
      setSaving(false);
    }
  };

  const saveFitnessProfile = async (e) => {
    e.preventDefault();
    setSavingFitness(true);
    try {
      const payload = {
        age: parseInt(fitnessProfile.age, 10),
        heightCm: parseFloat(fitnessProfile.heightCm),
        weightKg: parseFloat(fitnessProfile.weightKg),
        fitnessLevel: fitnessProfile.fitnessLevel,
        fitnessGoal: fitnessProfile.fitnessGoal,
        dietaryPreference: fitnessProfile.dietaryPreference,
        activityPreferences: typeof fitnessProfile.activityPreferences === 'string' ? fitnessProfile.activityPreferences : ''
      };
      await updateFitnessProfile(payload);
      toast.success('Fitness profile updated successfully');
      fetchData();
    } catch (err) {
      console.error(err);
      toast.error('Failed to update fitness profile');
    } finally {
      setSavingFitness(false);
    }
  };

  if (loading) return <LoadingSpinner message="Loading profile..." />;
  if (error) return <ErrorMessage message={error} onRetry={fetchData} />;

  return (
    <div className="profile-page animate-slide-up">
      <div className="page-header">
        <h2>Your Profile</h2>
        <p>Manage your personal information and fitness goals.</p>
      </div>

      <div className="profile-grid">
        <div className="profile-card glass-card">
          <h3>Basic Information</h3>
          <form onSubmit={saveUserProfile}>
            <div className="form-group">
              <label>First Name</label>
              <input type="text" name="firstName" value={userProfile.firstName} onChange={handleUserChange} required />
            </div>
            <div className="form-group">
              <label>Last Name</label>
              <input type="text" name="lastName" value={userProfile.lastName} onChange={handleUserChange} required />
            </div>
            <div className="form-group">
              <label>Your Personal Email</label>
              <input 
                type="email" 
                name="email" 
                value={userProfile.email} 
                onChange={handleUserChange} 
                required 
                placeholder="Enter your personal email" 
              />
              <small style={{ color: '#a0aec0', marginTop: '6px', display: 'block' }}>
                You can change this to your personal email anytime. Each email manages its own separate profile, workouts, and AI plans.
              </small>
            </div>
            <button type="submit" className="btn-primary" disabled={saving}>
              {saving ? 'Saving...' : 'Save Basic Info'}
            </button>
          </form>
        </div>

        <div className="profile-card glass-card">
          <h3>Fitness Profile</h3>
          <form onSubmit={saveFitnessProfile}>
            <div className="form-row">
              <div className="form-group half">
                <label>Age</label>
                <input type="number" name="age" value={fitnessProfile.age} onChange={handleFitnessChange} required min="10" max="120" />
              </div>
              <div className="form-group half">
                <label>Height (cm)</label>
                <input type="number" name="heightCm" value={fitnessProfile.heightCm} onChange={handleFitnessChange} required step="0.1" />
              </div>
            </div>
            <div className="form-group">
              <label>Weight (kg)</label>
              <input type="number" name="weightKg" value={fitnessProfile.weightKg} onChange={handleFitnessChange} required step="0.1" />
            </div>
            <div className="form-group">
              <label>Fitness Level</label>
              <select name="fitnessLevel" value={fitnessProfile.fitnessLevel} onChange={handleFitnessChange}>
                <option value="BEGINNER">Beginner</option>
                <option value="INTERMEDIATE">Intermediate</option>
                <option value="ADVANCED">Advanced</option>
              </select>
            </div>
            <div className="form-group">
              <label>Primary Goal</label>
              <select name="fitnessGoal" value={fitnessProfile.fitnessGoal} onChange={handleFitnessChange}>
                <option value="WEIGHT_LOSS">Weight Loss</option>
                <option value="MUSCLE_GAIN">Muscle Gain</option>
                <option value="ENDURANCE">Endurance</option>
                <option value="GENERAL_FITNESS">General Fitness</option>
              </select>
            </div>
            <div className="form-group">
              <label>Dietary Preference</label>
              <select name="dietaryPreference" value={fitnessProfile.dietaryPreference} onChange={handleFitnessChange}>
                <option value="NONE">None</option>
                <option value="VEGETARIAN">Vegetarian</option>
                <option value="VEGAN">Vegan</option>
                <option value="KETO">Keto</option>
                <option value="PALEO">Paleo</option>
              </select>
            </div>
            <div className="form-group">
              <label>Activity Preferences (comma separated)</label>
              <input type="text" name="activityPreferences" value={fitnessProfile.activityPreferences} onChange={handleFitnessChange} placeholder="e.g. gym, running, yoga" />
            </div>
            <button type="submit" className="btn-primary" disabled={savingFitness}>
              {savingFitness ? 'Saving...' : 'Save Fitness Profile'}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Profile;
