import React from 'react';
import { MdInfoOutline } from 'react-icons/md';
import './EmptyState.css';

const EmptyState = ({ 
  icon: Icon = MdInfoOutline, 
  title = 'No Data Found', 
  description = 'There is currently no data to display.', 
  action 
}) => {
  return (
    <div className="empty-state glass-card animate-fade-in">
      <div className="empty-state-icon">
        <Icon />
      </div>
      <h3 className="empty-state-title">{title}</h3>
      <p className="empty-state-description">{description}</p>
      {action && (
        <div className="empty-state-action">
          {action}
        </div>
      )}
    </div>
  );
};

export default EmptyState;
