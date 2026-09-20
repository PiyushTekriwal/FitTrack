import React from 'react';
import { MdErrorOutline } from 'react-icons/md';
import './ErrorMessage.css';

const ErrorMessage = ({ message = 'An unexpected error occurred.', onRetry }) => {
  return (
    <div className="error-message glass-card animate-fade-in">
      <MdErrorOutline className="error-icon" />
      <div className="error-content">
        <h3>Error</h3>
        <p>{message}</p>
      </div>
      {onRetry && (
        <button className="btn-primary retry-btn" onClick={onRetry}>
          Retry
        </button>
      )}
    </div>
  );
};

export default ErrorMessage;
