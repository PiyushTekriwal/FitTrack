import React from 'react';
import './LoadingSpinner.css';

const LoadingSpinner = ({ message = 'Loading...', fullScreen = false }) => {
  return (
    <div className={`spinner-container ${fullScreen ? 'full-screen' : ''}`}>
      <div className="spinner">
        <div className="spinner-circle"></div>
      </div>
      {message && <p className="spinner-message">{message}</p>}
    </div>
  );
};

export default LoadingSpinner;
