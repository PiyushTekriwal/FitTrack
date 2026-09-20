import React, { useState, useEffect } from 'react';
import toast from 'react-hot-toast';
import { MdAutoAwesome, MdFitnessCenter, MdRestaurant, MdCalendarToday, MdFavorite, MdElectricBolt, MdSpeed, MdShield } from 'react-icons/md';
import { generateRecommendation, getLatestRecommendation } from '../services/recommendationService';
import { createPlan } from '../services/planService';
import { getUserFitnessProfile } from '../services/userService';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorMessage from '../components/common/ErrorMessage';
import EmptyState from '../components/common/EmptyState';
import './Recommendations.css';

const Recommendations = () => {
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [error, setError] = useState(null);
  const [recommendation, setRecommendation] = useState(null);
  const [fitnessProfile, setFitnessProfile] = useState(null);
  
  const fetchData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [recRes, profileRes] = await Promise.allSettled([
        getLatestRecommendation(),
        getUserFitnessProfile()
      ]);

      if (recRes.status === 'fulfilled' && recRes.value.data && recRes.value.data.summary) {
        setRecommendation(recRes.value.data);
      } else {
        setRecommendation(null);
      }

      if (profileRes.status === 'fulfilled' && profileRes.value.data) {
        setFitnessProfile(profileRes.value.data);
      }
    } catch (err) {
      console.error("Failed to load recommendation:", err);
      setError('Failed to load recommendation.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleGenerate = async () => {
    setGenerating(true);
    setError(null);
    try {
      const res = await generateRecommendation('COMPREHENSIVE');
      setRecommendation(res.data);
      toast.success('AI Neural Engine synthesized new recommendations!');
    } catch (err) {
      toast.error('Failed to generate recommendation. Please try again.');
    } finally {
      setGenerating(false);
    }
  };

  const handleCreatePlan = async () => {
    if (!recommendation) return;
    try {
      const planData = {
        name: `AI Neural Plan - ${new Date().toLocaleDateString()}`,
        description: recommendation.summary,
        startDate: new Date().toISOString(),
        endDate: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString(),
        items: []
      };
      await createPlan(planData);
      toast.success('Active fitness plan synced from AI Engine!');
    } catch (err) {
      toast.error('Failed to create fitness plan');
    }
  };

  // Calculate dynamic macronutrient metrics based on fitness profile or default goals
  const weight = fitnessProfile?.weightKg || 70;
  const isGain = fitnessProfile?.fitnessGoal === 'MUSCLE_GAIN';
  const isLoss = fitnessProfile?.fitnessGoal === 'WEIGHT_LOSS';
  const proteinTarget = Math.round(weight * (isGain ? 2.2 : isLoss ? 2.0 : 1.6));
  const tdeeEstimate = Math.round(weight * 24 * (isGain ? 1.4 : isLoss ? 1.15 : 1.3));
  const carbsTarget = Math.round((tdeeEstimate * 0.45) / 4);
  const fatsTarget = Math.round((tdeeEstimate * 0.25) / 9);

  if (loading) return <LoadingSpinner message="Querying AI Neural Cache..." />;

  return (
    <div className="recommendations-page animate-slide-up">
      <div className="page-header d-flex justify-content-between align-items-center mb-4" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '16px' }}>
        <div>
          <div className="ai-status-badge mb-2" style={{ marginBottom: '10px' }}>
            <span className="ai-pulse-dot"></span>
            <span>AI ENGINE ONLINE • CACHE OPTIMIZED</span>
          </div>
          <h2>Neural Recommendations</h2>
          <p style={{ color: 'var(--text-secondary)' }}>Personalized fitness heuristics and metabolic intelligence tailored to your body.</p>
        </div>
        <button 
          className="btn-primary generate-btn" 
          onClick={handleGenerate} 
          disabled={generating}
        >
          <MdAutoAwesome style={{ fontSize: '1.2rem', color: '#00f2fe' }} /> 
          {generating ? 'Synthesizing Biometrics...' : 'Re-synthesize Advice'}
        </button>
      </div>

      {generating && (
        <div className="generating-overlay">
          <LoadingSpinner message="Neural engine analyzing caloric expenditure, heart rate distribution, and nutritional macro targets..." />
        </div>
      )}

      {error ? (
        <ErrorMessage message={error} onRetry={fetchData} />
      ) : recommendation ? (
        <div className="recommendation-content">

          {/* AI Biometric & Fuel Radar Bar */}
          <div className="ai-biometrics-bar">
            <div className="biometric-pill tdee">
              <div className="biometric-header">
                <span>Metabolic Radar</span>
                <MdElectricBolt style={{ color: '#00f5a0' }} />
              </div>
              <div className="biometric-value">{tdeeEstimate} <span style={{ fontSize: '0.9rem', color: 'var(--text-secondary)', fontWeight: 500 }}>kcal/day</span></div>
              <div className="biometric-meter">
                <div className="biometric-fill tdee" style={{ width: '75%' }}></div>
              </div>
            </div>

            <div className="biometric-pill protein">
              <div className="biometric-header">
                <span>Protein Fuel Target</span>
                <MdShield style={{ color: '#00f2fe' }} />
              </div>
              <div className="biometric-value">{proteinTarget}g <span style={{ fontSize: '0.9rem', color: 'var(--text-secondary)', fontWeight: 500 }}>/day</span></div>
              <div className="biometric-meter">
                <div className="biometric-fill protein" style={{ width: '85%' }}></div>
              </div>
            </div>

            <div className="biometric-pill carbs">
              <div className="biometric-header">
                <span>Carb Energy Allocation</span>
                <MdSpeed style={{ color: '#f59e0b' }} />
              </div>
              <div className="biometric-value">{carbsTarget}g <span style={{ fontSize: '0.9rem', color: 'var(--text-secondary)', fontWeight: 500 }}>/day</span></div>
              <div className="biometric-meter">
                <div className="biometric-fill carbs" style={{ width: '65%' }}></div>
              </div>
            </div>

            <div className="biometric-pill fats">
              <div className="biometric-header">
                <span>Essential Lipids</span>
                <MdFavorite style={{ color: '#f43f5e' }} />
              </div>
              <div className="biometric-value">{fatsTarget}g <span style={{ fontSize: '0.9rem', color: 'var(--text-secondary)', fontWeight: 500 }}>/day</span></div>
              <div className="biometric-meter">
                <div className="biometric-fill fats" style={{ width: '55%' }}></div>
              </div>
            </div>
          </div>

          {/* AI Coach Summary Card */}
          <div className="glass-card mb-4 summary-card">
            <h3><MdAutoAwesome className="icon-teal" /> AI Executive Synthesis</h3>
            <p className="summary-text">
              {recommendation.summary}
            </p>
            {recommendation.recoveryAdvice && (
              <div style={{ marginTop: '18px', padding: '16px', background: 'rgba(0, 242, 254, 0.05)', borderRadius: '12px', border: '1px solid rgba(0, 242, 254, 0.2)' }}>
                <strong style={{ color: '#00f2fe', display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <MdFavorite /> Recovery & Neural Rest Optimization:
                </strong>
                <p style={{ marginTop: '8px', fontSize: '0.95rem', color: '#cbd5e1', lineHeight: '1.6' }}>{recommendation.recoveryAdvice}</p>
              </div>
            )}
            <div style={{ marginTop: '22px' }}>
              <button className="btn-primary" onClick={handleCreatePlan}>
                <MdElectricBolt /> Deploy To Active Training Plan
              </button>
            </div>
          </div>

          {/* Workouts and Nutrition Grid */}
          <div className="recommendation-grid" style={{ marginBottom: '24px' }}>
            <div className="glass-card">
              <h3 style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '18px' }}>
                <MdFitnessCenter style={{ color: '#00f2fe' }} /> Targeted Neural Protocols
              </h3>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                {Array.isArray(recommendation.workoutRecommendations) && recommendation.workoutRecommendations.length > 0 ? (
                  recommendation.workoutRecommendations.map((w, idx) => (
                    <div key={idx} className="rec-item-card">
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '6px' }}>
                        <span style={{ fontWeight: '700', fontSize: '1rem', color: '#f8fafc' }}>{w.exercise}</span>
                        <span className="rec-tag cyan">{w.duration || `${w.sets} sets x ${w.reps} reps`}</span>
                      </div>
                      <p style={{ fontSize: '0.88rem', color: '#94a3b8', margin: 0 }}>{w.notes}</p>
                    </div>
                  ))
                ) : (
                  <p style={{ color: '#94a3b8' }}>{String(recommendation.workoutRecommendations || 'No workout protocols logged')}</p>
                )}
              </div>
            </div>

            <div className="glass-card">
              <h3 style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '18px' }}>
                <MdRestaurant style={{ color: '#f43f5e' }} /> Precision Nutrition Fuel
              </h3>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                {Array.isArray(recommendation.dietRecommendations) && recommendation.dietRecommendations.length > 0 ? (
                  recommendation.dietRecommendations.map((d, idx) => (
                    <div key={idx} className="rec-item-card">
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '6px' }}>
                        <span style={{ fontWeight: '700', fontSize: '1rem', color: '#f8fafc' }}>{d.meal}</span>
                        <span className="rec-tag pink">{d.calories} kcal ({d.protein})</span>
                      </div>
                      <p style={{ fontSize: '0.92rem', color: '#e2e8f0', marginBottom: '4px' }}>{d.description}</p>
                      <p style={{ fontSize: '0.84rem', color: '#94a3b8', margin: 0 }}>{d.notes}</p>
                    </div>
                  ))
                ) : (
                  <p style={{ color: '#94a3b8' }}>{String(recommendation.dietRecommendations || 'No nutrition targets logged')}</p>
                )}
              </div>
            </div>
          </div>

          {/* 7-Day Split */}
          {Array.isArray(recommendation.weeklyPlan) && recommendation.weeklyPlan.length > 0 && (
            <div className="glass-card">
              <h3 style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '18px' }}>
                <MdCalendarToday style={{ color: '#c084fc' }} /> 7-Day Neural Training & Nutrition Periodization
              </h3>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '14px' }}>
                {recommendation.weeklyPlan.map((day, idx) => (
                  <div key={idx} className={`split-card ${day.rest ? 'rest' : ''}`}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                      <span style={{ fontWeight: '800', fontFamily: 'Outfit', color: day.rest ? '#00f5a0' : '#ffffff' }}>{day.day}</span>
                      {day.rest ? (
                        <span className="rec-tag emerald">REST</span>
                      ) : (
                        <span className="rec-tag cyan">WORKOUT</span>
                      )}
                    </div>
                    <div style={{ fontSize: '0.88rem', fontWeight: '600', color: '#f8fafc', marginBottom: '6px' }}>{day.workout}</div>
                    <div style={{ fontSize: '0.82rem', color: '#94a3b8', lineHeight: '1.4' }}>{day.diet}</div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      ) : (
        <EmptyState 
          icon={MdAutoAwesome}
          title="No Neural Recommendations Available"
          description="Initiate the AI synthesis engine to evaluate your biometrics and output hyper-personalized workout and nutrition blueprints."
          action={<button className="btn-primary" onClick={handleGenerate}><MdAutoAwesome /> Synthesize Now</button>}
        />
      )}
    </div>
  );
};

export default Recommendations;

