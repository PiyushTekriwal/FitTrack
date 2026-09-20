import React, { useState, useEffect } from 'react';
import toast from 'react-hot-toast';
import { MdCheckCircle, MdRadioButtonUnchecked, MdEventNote } from 'react-icons/md';
import { getCurrentPlan, completePlanItem } from '../services/planService';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorMessage from '../components/common/ErrorMessage';
import EmptyState from '../components/common/EmptyState';
import './FitnessPlan.css';

const FitnessPlan = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [plan, setPlan] = useState(null);

  const fetchPlan = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await getCurrentPlan();
      setPlan(res.data);
    } catch (err) {
      if (err.response?.status !== 404) {
        setError('Failed to load current fitness plan.');
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPlan();
  }, []);

  const toggleItemCompletion = async (itemId, completed) => {
    if (completed) return; // Only allowing mark as complete for now based on API specs
    
    try {
      await completePlanItem(plan.id, itemId);
      
      // Optimistic update
      const updatedItems = plan.items.map(item => 
        item.id === itemId ? { ...item, completed: true } : item
      );
      
      const completedCount = updatedItems.filter(i => i.completed).length;
      const totalCount = updatedItems.length;
      const newPercentage = totalCount > 0 ? Math.round((completedCount / totalCount) * 100) : 0;
      
      setPlan({
        ...plan,
        items: updatedItems,
        completionPercentage: newPercentage
      });
      
      toast.success('Task marked as complete!');
    } catch (err) {
      toast.error('Failed to update task status');
    }
  };

  if (loading) return <LoadingSpinner message="Loading your fitness plan..." />;
  if (error) return <ErrorMessage message={error} onRetry={fetchPlan} />;

  return (
    <div className="plan-page animate-slide-up">
      <div className="page-header">
        <h2>Your Fitness Plan</h2>
        <p>Track your assigned workouts and meals.</p>
      </div>

      {plan ? (
        <div className="plan-container">
          <div className="glass-card plan-overview mb-4">
            <div className="plan-header-info">
              <div>
                <h3>{plan.name}</h3>
                <p className="plan-dates">
                  {new Date(plan.startDate).toLocaleDateString()} - {new Date(plan.endDate).toLocaleDateString()}
                </p>
              </div>
              <div className="plan-status">
                <span className={`status-badge ${plan.status.toLowerCase()}`}>{plan.status}</span>
              </div>
            </div>
            
            <p className="plan-description">{plan.description}</p>
            
            <div className="progress-section mt-4">
              <div className="d-flex justify-content-between mb-2">
                <span>Overall Progress</span>
                <span className="font-weight-bold">{plan.completionPercentage}%</span>
              </div>
              <div className="progress-bar-container large">
                <div 
                  className="progress-bar" 
                  style={{ width: `${plan.completionPercentage}%` }}
                ></div>
              </div>
            </div>
          </div>

          <div className="glass-card plan-tasks">
            <h3>Plan Tasks</h3>
            
            {plan.items && plan.items.length > 0 ? (
              <div className="task-list mt-3">
                {plan.items.map(item => (
                  <div key={item.id} className={`task-item ${item.completed ? 'completed' : ''}`}>
                    <div 
                      className="task-checkbox" 
                      onClick={() => toggleItemCompletion(item.id, item.completed)}
                    >
                      {item.completed ? (
                        <MdCheckCircle className="icon-success" />
                      ) : (
                        <MdRadioButtonUnchecked className="icon-pending" />
                      )}
                    </div>
                    <div className="task-content">
                      <div className="task-title-row">
                        <span className={`task-type-badge ${item.itemType.toLowerCase()}`}>
                          {item.itemType}
                        </span>
                        <h4>{item.title}</h4>
                      </div>
                      <p className="task-desc">{item.description}</p>
                      {item.scheduledDate && (
                        <p className="task-date">Scheduled: {new Date(item.scheduledDate).toLocaleDateString()}</p>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-secondary mt-3">No tasks found in this plan.</p>
            )}
          </div>
        </div>
      ) : (
        <EmptyState 
          icon={MdEventNote}
          title="No Active Fitness Plan"
          description="You don't have an active fitness plan right now. Generate an AI recommendation and create a plan from it."
        />
      )}
    </div>
  );
};

export default FitnessPlan;
